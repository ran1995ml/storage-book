# Hadoop集群部署

下载hadoop3.3.6

```shell
wget https://downloads.apache.org/hadoop/common/hadoop-3.3.6/hadoop-3.3.6.tar.gz
```

解压：

```shell
tar -zxvf hadoop-3.3.6.tar.gz -C /opt/
```

创建软链接

```shell
ln -s hadoop-3.3.6/ hadoop
```

在 `etc/hadoop/hadoop-env.sh` 配置 `JAVA_HOME`，配置`user` ，  `/etc/profile` 配置 `HADOOP_HOME`

```shell
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk
export HDFS_NAMENODE_USER="root"
export HDFS_DATANODE_USER="root"
export HDFS_ZKFC_USER="root"
export HDFS_JOURNALNODE_USER="root"
export HADOOP_NAMENODE_OPTS="-Xms256m -Xmx256m"
export HADOOP_DATANODE_OPTS="-Xms256m -Xmx256m"
```

配置 `etc/hadoop` 目录下的配置文件 `core-site.xml`、`hdfs-site.xml`、`yarn-site.xml` 和 `mapred-site.xml`

在 `/etc/hadoop/workers` 文件中添加 `datanode`

```
node1
node2
node3
```

启动每个节点的 `journalnode`

```shell
hdfs --daemon start journalnode
```

在任意一个 `namenode` 上格式化namenode

```shell
hdfs namenode -format
```

格式化后启动 `namenode`

```shell
hdfs --daemon start namenode
```

另一个 `namenode` 同步数据

```shell
hdfs namenode -bootstrapStandby
```

在任意一个 `namenode` 上格式化 `zkfc` 进程

```shell
hdfs zkfc -formatZK
```

启动 `hdfs` 进程，`zkfc` 和 `journalnode` 会一起启动

```shell
start-dfs.sh
```

