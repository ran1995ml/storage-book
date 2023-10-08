# Druid源码分析

目录结构：

<img src="./picture/CodeDir.png" alt="image-20230727163114980" style="zoom:50%;" />

## Service启动

解析命令行使用 `Airline`，通过命令行参数构建 `Runnable` 对象，调用 `run()` 启动服务

```java
public class Main
{
  public static void main(String[] args)
  {
    //解析命令行
    final Runnable command = cli.parse(args);
    if (!(command instanceof Help)) { // Hack to work around Help not liking being injected
      //注入command对象
      injector.injectMembers(command);
    }
    //启动进程
    command.run();
  }
}
```

服务启动使用了模板设计模式

<img src="./picture/CliRunnable.png" alt="image-20230727165157814" style="zoom:50%;" />

调用`ServerRunnable`的`run()`方法启动对应的服务。

```java
public abstract class ServerRunnable extends GuiceRunnable
{
  @Override
  public void run()
  {
    final Injector injector = makeInjector(getNodeRoles(getProperties()));
    //从injector里拿装载的Lifecyle
    final Lifecycle lifecycle = initLifecycle(injector);

    try {
      //等待线程运行结束
      lifecycle.join();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
```

通过 `injector` 拿到 `Lifecycle` 对象，调用 `start()` 方法，启动服务

```java
public abstract class GuiceRunnable implements Runnable
{
  //子类中实现，该方法用于bind对象
  protected abstract List<? extends Module> getModules();
  
  //创建Injector，注入依赖，如CuratorModule、FirehoseModule等
  public Injector makeInjector(Set<NodeRole> nodeRoles)
  {
    try {
      return ServerInjectorBuilder.makeServerInjector(baseInjector, nodeRoles, getModules());
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public static Lifecycle initLifecycle(Injector injector, Logger log)
  {
    try {
      final Lifecycle lifecycle = injector.getInstance(Lifecycle.class);
      final StartupLoggingConfig startupLoggingConfig = injector.getInstance(StartupLoggingConfig.class);

      Long directSizeBytes = null;
      try {
        directSizeBytes = JvmUtils.getRuntimeInfo().getDirectMemorySizeBytes();
      }
      catch (UnsupportedOperationException ignore) {
        // querying direct memory is not supported
      }

      log.info(
          "Starting up with processors [%,d], memory [%,d], maxMemory [%,d]%s. Properties follow.",
          JvmUtils.getRuntimeInfo().getAvailableProcessors(),
          JvmUtils.getRuntimeInfo().getTotalHeapSizeBytes(),
          JvmUtils.getRuntimeInfo().getMaxHeapSizeBytes(),
          directSizeBytes != null ? StringUtils.format(", directMemory [%,d]", directSizeBytes) : ""
      );

      try {
        //启动进程
        lifecycle.start();
      }
      catch (Throwable t) {
        log.error(t, "Error when starting up.  Failing.");
        System.exit(1);
      }

      return lifecycle;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
```

### Service生命周期

`Druid` 服务的生命周期抽象成 `Lifecycle` 类，包含4个 `Stage`：

- `INIT`：主要用于 `log4j` 的初始化；
- `NORMAL`：大多数对象的初始化都在这个阶段；
- `SERVER`：启动 `jetty` 服务器；
- `ANNOUNCEMENTS`：上述阶段完成后，会进入此状态，表示服务可用，通知整个集群

```java
public class Lifecycle
{
  private final Lock startStopLock = new ReentrantLock();
  
  private final NavigableMap<Stage, CopyOnWriteArrayList<Handler>> handlers;
  
  //保证当前状态的可见性
  private final AtomicReference<State> state = new AtomicReference<>(State.NOT_STARTED);
  
  private Stage currStage = null;
  
  public enum Stage
  {
    INIT,  //专门用于log4j的初始化
    NORMAL, //对大部分对象的配置，如zkClient，MetadataConnector的初始化
    SERVER, //适用于所有服务器对象
    ANNOUNCEMENTS //向集群宣告
  }
  
  private enum State
  {
    NOT_STARTED, //start()调用前
    RUNNING,  //start()调用后，stop()调用前
    STOP  //stop()调用后
  }
  
  //主要在NORMAL阶段，初始化其他对象
  public interface Handler
  {
    void start() throws Exception;

    void stop();
  }
  
  //大部分对象，如zkClient，MetadataConnector注入时调用此方法，在NORMAL阶段调用，添加handler
  public void addHandler(Handler handler, Stage stage)
  {
    if (!startStopLock.tryLock()) {
      throw new ISE("Cannot add a handler in the process of Lifecycle starting or stopping");
    }
    try {
      if (!state.get().equals(State.NOT_STARTED)) {
        throw new ISE("Cannot add a handler after the Lifecycle has started, it doesn't work that way.");
      }
      handlers.get(stage).add(handler);
    }
    finally {
      startStopLock.unlock();
    }
  }
  
  public Lifecycle(String name)
  {
    Preconditions.checkArgument(StringUtils.isNotEmpty(name), "Lifecycle name must not be null or empty");
    this.name = name;
    handlers = new TreeMap<>();
    //记录每个stage的执行
    for (Stage stage : Stage.values()) {
      handlers.put(stage, new CopyOnWriteArrayList<>());
    }
  }
  
  public void join() throws InterruptedException
  {
    ensureShutdownHook();
    Thread.currentThread().join();
  }
  
  //启动进程
  public void start() throws Exception
  {
    //加锁
    startStopLock.lock();
    try {
      //不等于NOT_STARTED，可能已经RUNNING或者停止
      if (!state.get().equals(State.NOT_STARTED)) {
        throw new ISE("Already started");
      }
      //等于NOT_STARTED，再判断一次是否有其他线程启动，若还是NOT_STARTED，置为RUNNING
      if (!state.compareAndSet(State.NOT_STARTED, State.RUNNING)) {
        throw new ISE("stop() is called concurrently with start()");
      }
      for (Map.Entry<Stage, ? extends List<Handler>> e : handlers.entrySet()) {
        currStage = e.getKey();
        log.info("Starting lifecycle [%s] stage [%s]", name, currStage.name());
        for (Handler handler : e.getValue()) {
          handler.start();
        }
      }
      log.info("Successfully started lifecycle [%s]", name);
    }
    finally {
      startStopLock.unlock();
    }
  }
  
  //结束进程
  public void stop()
  {
    //检查进程是否已经关闭
    if (!state.compareAndSet(State.RUNNING, State.STOP)) {
      log.info("Lifecycle [%s] already stopped and stop was called. Silently skipping", name);
      return;
    }
    startStopLock.lock();
    try {
      Exception thrown = null;
			//逆序处理每个Stage的handler
      for (Stage s : handlers.navigableKeySet().descendingSet()) {
        log.info("Stopping lifecycle [%s] stage [%s]", name, s.name());
        for (Handler handler : Lists.reverse(handlers.get(s))) {
          try {
            handler.stop();
          }
          catch (Exception e) {
            log.warn(e, "Lifecycle [%s] encountered exception while stopping %s", name, handler);
            if (thrown == null) {
              thrown = e;
            } else {
              thrown.addSuppressed(e);
            }
          }
        }
      }

      if (thrown != null) {
        throw new RuntimeException(thrown);
      }
    }
    finally {
      startStopLock.unlock();
    }
  }
}
```

### 对象注入

`Lifecycle` 用来管理注入的对象，注入需要创建 `Handler`，可选的注入方式：

1. 注入的对象包含 `start` 和 `stop` 方法，通过 `Lifecycle` 已经实现的 `StartCloseHandler` 封装实例

   ```java
   public <T> T addStartCloseInstance(T o)
   {
     addHandler(new StartCloseHandler(o));
     return o;
   }
   
   private static class StartCloseHandler implements Handler
   {
     private static final Logger log = new Logger(StartCloseHandler.class);
   
     private final Object o;
     private final Method startMethod;
     private final Method stopMethod;
   
     public StartCloseHandler(Object o)
     {
       this.o = o;
       try {
         startMethod = o.getClass().getMethod("start");
         stopMethod = o.getClass().getMethod("close");
       }
       catch (NoSuchMethodException e) {
         throw new RuntimeException(e);
       }
     }
   
     //调用实例的start方法
     @Override
     public void start() throws Exception
     {
       log.info("Starting object[%s]", o);
       startMethod.invoke(o);
     }
   
     //调用实例的stop方法
     @Override
     public void stop()
     {
       log.info("Stopping object[%s]", o);
       try {
         stopMethod.invoke(o);
       }
       catch (Exception e) {
         log.error(e, "Unable to invoke stopMethod() on %s", o.getClass());
       }
     }
   }
   ```

2. 有 `@LifecycleStart` 和 `@LifecycleStop` 注解标注的对象，通过 `Lifecycle` 已经实现的 `AnnotationBasedHandler` 封装实例

   ```java
   public <T> T addManagedInstance(T o)
   {
     addHandler(new AnnotationBasedHandler(o));
     return o;
   }
   
   private static class AnnotationBasedHandler implements Handler
   {
     private static final Logger log = new Logger(AnnotationBasedHandler.class);
   
     private final Object o;
   
     public AnnotationBasedHandler(Object o)
     {
       this.o = o;
     }
   
     @Override
     public void start() throws Exception
     {
       for (Method method : o.getClass().getMethods()) {
         boolean doStart = false;
         for (Annotation annotation : method.getAnnotations()) {
           //查找实例带有@LifecycleStart注解的方法
           if (LifecycleStart.class.getName().equals(annotation.annotationType().getName())) {
             doStart = true;
             break;
           }
         }
         if (doStart) {
           log.debug("Invoking start method[%s] on object[%s].", method, o);
           method.invoke(o);
         }
       }
     }
   
     @Override
     public void stop()
     {
       for (Method method : o.getClass().getMethods()) {
         boolean doStop = false;
         for (Annotation annotation : method.getAnnotations()) {
           if (LifecycleStop.class.getName().equals(annotation.annotationType().getName())) {
             doStop = true;
             break;
           }
         }
         if (doStop) {
           log.debug("Invoking stop method[%s] on object[%s].", method, o);
           try {
             method.invoke(o);
           }
           catch (Exception e) {
             log.error(e, "Exception when stopping method[%s] on object[%s]", method, o);
           }
         }
       }
     }
   }
   ```

3. 自定义 `Handler` 传入

### Coordinator启动初始化

根据 `Coordinator` 的启动日志，`Coordinator` 启动后先加载 `jar` 包，初始化配置后进入 `INIT` 状态，随后进入 `NORMAL` 状态，对注入的对象实例进行初始化：

- `Zookeeper`：完成对 `Zookeeper` 的连接
- `MetadataStorageConnector`：完成 `metadata db` 的连接初始化，并创建 `druid_segments` 等表
- `HttpServerInventoryView`：启动调用服务发现
- `CoordinatorServerView`：获取集群中正在加载的 `segment` 的状态
- `MetadataRuleManager`：获取 `datasource` 的载入规则

之后进行 `Leader` 选举，选举成功初始化对象实例：

- `SegmentsMetadataManager`：加载 `segment` 的元数据信息
- `SegmentAllocationQueue`：初始化 `segment` 分配队列
- `MetadataStorageActionHandler`：从 `druid_tasks` 中还原 `task`

对象初始化完成，进入 `Server` 状态，启动 `jetty` 服务器；启动成功，通知整个集群该服务可用，进入 `ANNOUNCEMENTS` 状态，之后定期执行 `CoordinatorDuty`，包括 `BalanceSegments` 、`KillSupervisors` 和 `LogUsedSegments` 等

### Overlord启动初始化

若 `Overlord` 和 `Coordinator` 部署在同一个 `JVM` 中，`Coordinator` 进入 `ANNOUNCEMENTS` 状态后，开始 `Overlord` 的启动初始化。启动名为`task-master` 的 `lifecycle`，进入 `NORMAL` 状态后，初始化对象：

- `WorkerTaskRunner`：管理 `MiddleManager` 上的任务，使用内部发现机制获取信息
- `TaskQueue`：生产者和 `TaskRunner` 间的接口，从生产者接收任务分配给 `TaskRunner`
- `TaskLockbox`：记录目前正在活跃的任务，锁定了哪些 `interval` 和 `segment`，辅助任务的分配
- `SupervisorManager`：管理 `Supervisor` 的创建和生命周期

选举 `Leader` 成功后，进入 `Server` 状态启动 `jetty` 服务器，通知集群服务可用，进入 `ANNOUNCEMENTS` 状态，定期执行 `Overlord`，包括 `TaskLogAutoCleaner` 和 `DurableStorageCleaner`



## Coordinator && Overlord

属于Master节点，可部署在同一个JVM进程中，也可分开部署。

## Leader选举

Druid的Master节点的leader选举，基于Curator实现。

![image-20230928151049675](/Users/rwei/Library/Application Support/typora-user-images/image-20230928151049675.png)

Coordinator的Leader选举在 `DruidCoordinator` 中调用，Overlord的Leader选举在 `TaskMaster` 调用

```java
public class CuratorDruidLeaderSelector implements DruidLeaderSelector
{
  private static final EmittingLogger log = new EmittingLogger(CuratorDruidLeaderSelector.class);

  private final LifecycleLock lifecycleLock = new LifecycleLock();

  private final DruidNode self;
  private final CuratorFramework curator;
  private final String latchPath;

  private ExecutorService listenerExecutor;

  private DruidLeaderSelector.Listener listener = null;
  private final AtomicReference<LeaderLatch> leaderLatch = new AtomicReference<>();

  private volatile boolean leader = false;
  private volatile int term = 0;

  public CuratorDruidLeaderSelector(CuratorFramework curator, @Self DruidNode self, String latchPath)
  {
    this.curator = curator;
    this.self = self;
    this.latchPath = latchPath;
    this.leaderLatch.set(createNewLeaderLatch());
  }

  private LeaderLatch createNewLeaderLatch()
  {
    return new LeaderLatch(curator, latchPath, self.getServiceScheme() + "://" + self.getHostAndPortToUse());
  }

  private LeaderLatch createNewLeaderLatchWithListener()
  {
    final LeaderLatch newLeaderLatch = createNewLeaderLatch();

    newLeaderLatch.addListener(
        new LeaderLatchListener()
        {
          @Override
          public void isLeader()
          {
            try {
              //已经成为Leader，跳过选举
              if (leader) {
                log.warn("I'm being asked to become leader. But I am already the leader. Ignored event.");
                return;
              }

              leader = true;
              term++;
              //执行成为leader后的操作
              listener.becomeLeader();
            }
            catch (Exception ex) {
              log.makeAlert(ex, "listener becomeLeader() failed. Unable to become leader").emit();
							
              CloseableUtils.closeAndSuppressExceptions(
                  createNewLeaderLatchWithListener(),
                  e -> log.warn("Could not close old leader latch; continuing with new one anyway.")
              );

              leader = false;
              try {
                //如果选举出现异常，sleep一小段时间让出机会给其他节点
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5000));
                leaderLatch.get().start();
              }
              catch (Exception e) {
                log.makeAlert(e, "I am a zombie").emit();
              }
            }
          }

          @Override
          public void notLeader()
          {
            try {
              if (!leader) {
                log.warn("I'm being asked to stop being leader. But I am not the leader. Ignored event.");
                return;
              }

              leader = false;
              listener.stopBeingLeader();
            }
            catch (Exception ex) {
              log.makeAlert(ex, "listener.stopBeingLeader() failed. Unable to stopBeingLeader").emit();
            }
          }
        },
        listenerExecutor
    );

    return leaderLatch.getAndSet(newLeaderLatch);
  }

  @Override
  public void registerListener(DruidLeaderSelector.Listener listener)
  {
    Preconditions.checkArgument(listener != null, "listener is null.");

    if (!lifecycleLock.canStart()) {
      throw new ISE("can't start.");
    }
    try {
      this.listener = listener;
      //创建线程执行Leader选举
      this.listenerExecutor = Execs.singleThreaded(
          StringUtils.format(
              "LeaderSelector[%s]",
              StringUtils.encodeForFormat(latchPath)
          )
      );

      createNewLeaderLatchWithListener();
      leaderLatch.get().start();

      lifecycleLock.started();
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    finally {
      lifecycleLock.exitStart();
    }
  }

  @Override
  public void unregisterListener()
  {
    if (!lifecycleLock.canStop()) {
      throw new ISE("can't stop.");
    }

    CloseableUtils.closeAndSuppressExceptions(leaderLatch.get(), e -> log.warn(e, "Failed to close LeaderLatch."));
    listenerExecutor.shutdownNow();
  }
}
```

`DruidCoordinator` 对象实例化后进行Leader选举

```java
@ManageLifecycle
public class DruidCoordinator
{
  @LifecycleStart
  public void start()
  {
    synchronized (lock) {
      if (started) {
        return;
      }
      started = true;
			//启动后注册Listener执行选举
      coordLeaderSelector.registerListener(
          new DruidLeaderSelector.Listener()
          {
            @Override
            public void becomeLeader()
            {
              //执行选举leader成功后的操作
              DruidCoordinator.this.becomeLeader();
            }

            @Override
            public void stopBeingLeader()
            {
              //执行选举leader失败后的操作
              DruidCoordinator.this.stopBeingLeader();
            }
          }
      );
    }
  }
}
```

`TaskMaster` 实例化后执行Overlord的Leader选举

```java
//用于Overlord的Leader选举，暴露查询task信息的方法
public class TaskMaster implements TaskCountStatsProvider, TaskSlotCountStatsProvider
{
  private final ReentrantLock giant = new ReentrantLock(true);
  
  @LifecycleStart
  public void start()
  {
    giant.lock();

    try {
      overlordLeaderSelector.registerListener(leadershipListener);
    }
    finally {
      giant.unlock();
    }
  }
  
  @LifecycleStop
  public void stop()
  {
    giant.lock();

    try {
      gracefulStopLeaderLifecycle();
      overlordLeaderSelector.unregisterListener();
    }
    finally {
      giant.unlock();
    }
  }
}
```

