#!/bin/bash
# zk集群操作，在一个节点上执行
# setup_zk_cluster.sh start
# setup_zk_cluster.sh status
# setup_zk_cluster.sh stop

HOSTS=(
"node1"
"node2"
"node3"
)

for HOST in "${HOSTS[@]}"
do
  echo "--------------------------------zk node $HOST, $1--------------------------------"
  ssh $HOST $ZOOKEEPER_HOME/bin/zkServer.sh $1
done