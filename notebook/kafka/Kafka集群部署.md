# Kafka集群部署

准备好 `scala` 环境下载二进制包

```shell
wget https://archive.apache.org/dist/kafka/2.7.2/kafka_2.12-2.7.2.tgz
```

解压

```shell
tar -zxvf kafka_2.12-2.7.2.tgz -C /opt/
```

创建日志目录

```shell
mkdir -p /data/kafka/log
```

修改 `server.properties`

```
# server.properties
broker.id=0 # broker唯一标识
listeners=PLAINTEXT://node1:9092 # broker监听地址，填本地ip，不要填localhost
log.dirs=/data/kafka/log
zookeeper.connect=node1:2181,node2:2181,node3:2181
```

调整 `broker` 启动内存，`kafka-server-start.sh` 开头添加

```shell
export KAFKA_HEAP_OPTS="-Xmx512m -Xms512m"
```

启动 `broker`

```shell
bin/kafka-server-start.sh -daemon config/server.properties
```

创建一个测试 `topic`

```shell
bin/kafka-topics.sh --create --zookeeper node1:2181 -replication-factor 1 --partitions 1 --topic test
```

查看 `topic`

```shell
bin/kafka-topics.sh --list --zookeeper node1:2181
```

启动生产者测试

```shell
bin/kafka-console-producer.sh --bootstrap-server node1:9092,node2:9092,node3:9092 --topic test
```

启动消费者

```shell
bin/kafka-console-consumer.sh --bootstrap-server node1:9092,node2:9092,node3:9092 --topic test --from-beginning
```

