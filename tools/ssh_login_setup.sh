#!/bin/bash
# 设置集群免密登陆，选择一台主机执行

# 公网ip
HOSTS=(
"172.22.0.3"
"172.22.0.16"
"172.22.0.4"
)

USER="root"

PASSWARD=""

SSH_PUB_KEY="/$USER/.ssh/id_rsa.pub"

ssh_copy_id() {
  expect -c "set timeout -1;
  spawn ssh-copy-id -i $4 $2@$1;
  expect {
      *(yes/no)* {send -- yes\r;exp_continue;}
      *password:* {send -- $3\r;exp_continue;}
      eof { exit 0;}
    }"
}

# 判断公钥是否存在
[ ! -f $SSH_PUB_KEY ] && {
  ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
}

for HOST in "${HOSTS[@]}"
do
  ssh_copy_id $HOST $USER $PASSWARD $SSH_PUB_KEY
done