# 安装

下载

```shell
curl -fsSL https://get.docker.com | bash -s docker --mirror Aliyun
```

创建相关文件夹

```shell
mkdir -p /data/docker /etc/docker
```

配置

```
{
  "data-root": "/data/docker",
  "storage-driver": "overlay2",
  "insecure-registries": ["registry.access.redhat.com","quay.io","harbor.od.com"],
  "registry-mirrors": ["https://q2gr04ke.mirror.aliyuncs.com"],
  "bip": "172.7.21.1/24",
  "exec-opts": ["native.cgroupdriver=systemd"],
  "live-restore": true
}
```

