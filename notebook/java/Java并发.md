`Futures.transform` : 接受一个输入的 ListenableFuture 和一个 AsyncFunction（异步函数），并返回一个新的 ListenableFuture，
该新 ListenableFuture 会在原始 ListenableFuture 的结果可用时应用提供的函数。
`Futures.immediateFuture` : 创建一个已知结果的 ListenableFuture
`SettableFuture` : 实现了 `ListenableFuture` 接口，允许显式设置返回结果，不必等待结果返回

`@GuardedBy("giant")` : 访问某个字段或方法必须标注某个锁

`BlockingQueue` : 允许多个线程同时进行队列操作
`CountDownLatch` : 一个或多个线程完成某些操作，才继续执行。通过计数器设置初始值，变为0，等待的线程才继续执行

notify 和 notifyAll，notify只唤醒一个线程，notifyAll唤醒所有线程
wait，使当前线程进入等待状态，同时释放持有的锁

`MoreExecutors.listeningDecorator`:将普通的 ExecutorService 装饰成 ListeningExecutorService，使其支持 ListenableFuture

lockInterruptibly 是 Java 中 Lock 接口的一种实现方式，它提供了可中断的锁申请方法。
当线程在等待获取锁的过程中，如果其他线程调用了 interrupt 方法，那么当前线程就会收到 InterruptedException 异常，从而有机会响应中断而不是无限等待。