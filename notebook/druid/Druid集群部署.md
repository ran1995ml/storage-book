# Druid集群部署

下载二进制包

```shell
wget https://downloads.apache.org/druid/26.0.0/apache-druid-26.0.0-bin.tar.gz
```

解压

```shell
tar -zxvf apache-druid-26.0.0-bin.tar.gz -C /opt/
```

配置 `common.runtime.properties`

```
# 设置hostname
druid.host=node3

# 配置扩展
druid.extensions.loadList=["druid-hdfs-storage", "druid-kafka-indexing-service", "druid-datasketches","mysql-metadata-storage"]


# 指定zk
druid.zk.service.host=node1:2181,node2:2181,node3:2181
druid.zk.paths.base=/druid

# metadata设置
druid.metadata.storage.type=mysql
druid.metadata.storage.connector.connectURI=jdbc:mysql://node3:3306/druid
druid.metadata.storage.connector.user=root
druid.metadata.storage.connector.password=123456

# deep storage
druid.storage.type=hdfs
druid.storage.storageDirectory=hdfs://mycluster/druid/segments

druid.indexer.logs.type=hdfs
druid.indexer.logs.directory=hdfs://mycluster/druid/indexing-logs
```

创建 `druid` 数据库

```shell
# 下载mysql5.7，5.6有字符串长度限制
CREATE DATABASE druid DEFAULT CHARACTER SET utf8mb4;
```

下载 `mysql-connector` ， 放在 `extensions/mysql-metadata-storage` 目录下

```shell
wget https://repo1.maven.org/maven2/mysql/mysql-connector-java/5.1.49/mysql-connector-java-5.1.49.jar
```

根据需要配置 `jvm.config` 和 `runtime.properties`

将 `hdfs` 的 `xml` 配置文件放到每个节点的 `conf/druid/cluster/_common/` 目录下

启动

```shell
nohup bin/start-cluster-master-no-zk-server > /dev/null 2>&1 &
nohup bin/start-cluster-query-server  > /dev/null 2>&1 &
nohup bin/start-cluster-data-server > /dev/null 2>&1 &
```

