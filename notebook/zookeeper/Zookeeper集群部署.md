# Zookeeper集群部署

准备好java8环境，下载zookeeper，选择3.5版本，与大多数组件都兼容

```shell
wget https://archive.apache.org/dist/zookeeper/zookeeper-3.5.5/apache-zookeeper-3.5.5-bin.tar.gz
```

对每台节点，解压文件到指定目录：

```shell
tar -zxvf apache-zookeeper-3.5.5-bin.tar.gz -C /opt/
```

创建软链接
```shell
ln -s apache-zookeeper-3.5.5-bin/ zookeeper
```

修改`/opt/apache-zookeeper-3.5.5-bin/conf`目录下的`zoo_sample.cfg`，复制一份命名为`zoo.cfg`

```shell
cp zoo_sample.cfg zoo.cfg
```

修改数据目录：

```
dataDir=/data/zookeeper
```

在最下方添加集群server信息，2188和2888分别是集群内通信和Leader选举使用的端口

```
server.1=node1:2188:2888
server.2=node2:2188:2888
server.3=node3:2188:2888
```

在 `conf` 目录下创建 `java.env` 文件，设置堆内存

```shell
#!/bin/sh
export JAVA_HOME=/usr/lib/jvm/java
export JVMFLAGS="-Xms256m -Xmx256m $JVMFLAGS"
```

在`dataDir`下生成`myid`文件，表示每个节点选举成为 `Leader` 的优先级

```shell
mkdir -p /data/zookeeper/
echo "1" > /data/zookeeper/myid
```

配置完毕，启动

```shell
zkServer.sh start
zkServer.sh status
```

查看进程的内存

```shell
jmap -heap 32658
```

