Kubernetes，抽象了数据中心的硬件基础设施，对外暴露一个巨大的资源池，让我们在部署和运行组件时，不必关注底层服务器。为组件选择合适的服务器，保证每个组件能轻易发现其他组件，彼此之间实现通信。

Kubernetes使用容器技术提供应用的隔离。容器里运行的进程实际运行在宿主机的操作系统上，但和其他进程是隔离的。对容器而言，好像是在机器和操作系统上运行的唯一一个进程。

容器隔离机制：Linux命名空间，每个进程只看到自己的系统视图（文件、进程、网络接口）；Linux控制组，限制了进程能使用的资源量。

Docker容器镜像和虚拟机镜像很大不同之处是由多层构成，可在多个镜像间共享和征用。如果某个已经被下载的容器镜像包含后面下载镜像的某些层，则后面下载的镜像无须再下载。

Docker将应用程序和依赖的整个环境打包在一起。

每个容器都有自己隔离的文件系统，如何共享文件？镜像由多层构成，不同镜像都能使用相同的父镜像作为基础镜像，提升了在网络上的分发效率，相同层被之前的镜像传输，这些层不需要再传输。层还能减少镜像的存储空间。但一个容器写入某些文件，另一个是无法看见文件变更的。共享文件但仍彼此隔离，因为容器镜像层是只读的。

Kubernetes系统架构，由一个主节点和若干个工作节点组成。把一个应用列表提交到主节点，Kubernetes将其部署到集群的工作节点。可以把Kubernetes看作集群的操作系统。

主节点包含的组件：

- Kubernetes API服务器
- Scheduler，调度应用，为每个可部署组件分配一个工作节点
- Controller Manager，执行集群级别的功能，复制组件、持续跟踪工作节点、处理节点失败
- Etcd，分布式数据存储，持久化存储集群配置

工作节点包含的组件：

- Docker
- Kubelet，与API服务器通信，管理所在节点的容器
- Kube-proxy，负责组件之间的负载均衡

API服务器处理应用描述时，调度器调度指定组的容器到可用工作节点上，调度是基于每组所需的计算资源，以及调度时每个节点未分配的资源。选定节点上的Kubelet指示容器运行时拉取的镜像并运行容器。

容器运行起来，Kubernetes会不断确认应用程序的部署状态。为能轻松找到特定服务的容器，通过一个静态IP地址暴露所有容器，且暴露给集群中运行的所有应用程序。

```dockerfile
FROM node:7 # 使用node的tag7版本
ADD app.js /app.js #将app.js添加到根目录
ENTRYPOINT ["node", "app.js"] #镜像运行时执行的命令
```

构建运行镜像：

```shell
docker build -t kubia . # 构建一个名为kubia的镜像
docker run --name kubia-container -p 8080:8080 -d kubia # 以kubia镜像构建容器，本机8080端口映射到容器的8080端口
```

镜像构建过程不是Docker客户端进行的，将整个目录上传到Docker守护进程构建，首次会从镜像仓库拉取镜像。

构建镜像时，Dockerfile的每一条单独的指令都会创建一个新层。

Minikube配置Kubernetes单节点：

```shell
curl -Lo minikube https://storage.googleapis.com/minikube/releases/v0.23.0/minikube-linux-amd64
chmod +x minikube
mv minikube /usr/local/bin/
```

启动

```shell
minikube start
```

下载kubenetes客户端kubectl

```shell
curl -LO https://storage.googleapis.com/kubenetes-release/release/$(curl -s https://storage.googleapis.com/kubenetes-release/release/stable.txt)/bin/linux/amd64/kubectl
chmod +x kubectl
mv kubectl /usr/local/bin/
```

Pod，不处理单个容器，代表一组紧密相关的容器，总是一起运行在同一个工作节点上，同一个Linux命名空间。每个pod像一个独立的逻辑机器，拥有自己的IP、主机名、进程，运行一个独立的应用程序。

如果pod停留在挂起状态，可能是kubernetes无法从镜像中心拉取镜像。

运行kubectl命令，会向kubernetes api服务器发送http请求，在集群中创建一个新的ReplicationController对象，利用此对象创建一个新的pod，调度器将其调度到一个工作节点上。Kubectl看到pod被调度到节点上，就告知docker从镜像中心拉取镜像。

如果要从外部访问pod，需要通过服务对象公开它，创建一个特殊的LoadBalancer类型的服务，会创建一个外部的负载均衡，通过负载均衡的公共IP访问pod。

```shell
kubectl expose rc kubia --type=LoadBalancer --name kubia-http
```

服务对象，类似于pod和Node的对象，列出服务

```shell
kubectl get services
```

ReplicationController始终存在一个运行中的pod实例，用于复制pod保持运行。如果pod消失，ReplicationController会创建一个新pod替换消失的pod。

服务被创建时，会得到一个静态ip，服务生命周期中不会改变。客户端通过固定IP连接到服务，不直接连接pod。服务表示一组或多组提供相同服务pod的静态地址，到达服务ip和端口的请求被转发到属于该服务的一个容器的ip和端口。

Kubenetes的基本原则，不是告诉它应该执行什么操作，而是声明性地改变系统的期望状态，让Kubernetes检查当前状态是否与期望的状态一致。

当pod有多个实例，请求会随机切换到不同的pod。

一个pod绝不会跨越多个工作节点。

容器被设计为每个容器只运行一个进程，如果运行多个不相关的进程，很难确定每个进程分别记录了什么。因此将容器绑定在一起，作为一个单元管理。Kubernetes配置Docker让一个pod内的所有容器共享相同的linux命名空间。pod里容器间实现的是部分隔离。

大多数容器的文件系统来自容器镜像，默认情况下每个容器的文件系统与其他容器完全隔离。可以使用Volume的Kubernetes资源共享文件目录。

Kubernetes集群中的所有pod都在同一个共享网络地址空间中，这意味着每个pod都可通过其他pod的IP地址互相访问，它们之间没有NAT网关。

pod可看作逻辑主机，是扩缩容的基本单位。Kubernetes不能横向扩展单个容器，只能扩缩整个pod。多个容器添加到蛋哥pod主要原因是应用可能由一个主进程和一个或多个辅助进程组成。如果需要将多个容器放入一个pod，需要考虑：

- 需要一起运行还是可以在不同的主机上；
- 代表一个整体还是相互独立的组件
- 必须一起扩缩容还是可以分别进行

pod和其他Kubernetes资源通常向Kubernetes REST API提供json或yaml创建。pod定义的组成：

- Kubernetes API版本和YAML描述的资源类型
- Metadata，包括名称、命名空间、标签和容器的其他信息
- Spec，包含pod内容的实际说明，pod的容器、卷和其他数据
- Status，包含运行中的pod的当前信息，pod所处的条件、每个容器的描述和状态，内部IP和其他基本信息

不通过service与特定的pod通信，可将配置本地端口转发到该pod

```shell
kubectl port-forward kubia-manual 8888:8080
```

另一个终端中，可以向pod发送请求

```shell
curl localhost:8888
```

标签，用于组织pod和其他的Kubernetes资源。是可以附加到资源的任意键值对，用于选择具有该确切标签的资源。只要key是唯一的，一个资源可以拥有多个标签。

列出标签：

```shell
kubectl get pods --show-labels
```

列出指定的标签：

```shell
kubectl get pods -L env
```

标签选择器，选择标记特定标签的pod子集对其执行操作。

注解，也是键值对，和标签非常相似。不是为了保存标识信息而存在的，不能像标签一样用于对对象分组。注解可以容纳更多信息，主要为每个pod或其他API对象添加说明。

大多数类型的资源都和namespace相关，资源名称只需要在命名空间内保持唯一，namespace为资源名称提供了一个作用域。除隔离资源，namespace还可以允许某些用户访问某些特定资源，限制单个用户可用的计算资源数量。但namespace并不提供对正在运行的对象的任何隔离。

Kubernetes的所有内容都是一个API对象，可通过向API服务器提交YAML实现创建、更新、删除。

删除当前命名空间所有资源：

```shell
kubectl delete all --all
```

并不是完全删除所有内容，一些资源如Secret会被保留下来，需要明确指定删除。



Kubernetes可通过存活探针检查容器是否在运行，若探测失败，会定期执行探针并重启容器。三种探测容器的机制：

- HTTP GET探针，对容器的IP地址发送HTTP请求，收到正确相应则认为成功，否则重启容器；
- TCP SOCKET探针，建立TCP连接，连接成功建立则成功；
- Exec探针，在容器内执行任意命令，检查命令退出状态码，0则成功。

HTTP探针配置，该探针告诉Kubernetes定期执行HTTP请求，确定容器是否健康。

```yaml
apiVersion: v1
kind: pod
metadata:
	name: kubia-liveness
spec:
	containers:
	- image: luksa/kubia-unhealthy
		name: kubia
		livenessProbe:
			httpGet:
				path: /
				port: 8080
			initialDelaySeconds: 15
```

查看前一个崩掉的容器日志：

```shell
kubectl logs mypod --previous
kubectl describe pod kubia # 可查看重启容器的原因
```

退出代码等于128+x，x是终止进程的信号编号。Kubernetes发现容器不健康，会创建一个全新的容器，而不是重启。务必要设置一个初始延迟，避免应用程序没有准备好接收请求导致探测失败。

Kubernetes在容器崩溃或探针失败时，重启容器保持运行，是由承载pod的节点上的kubelet执行。如果节点崩溃，ControlPlane必须为所由随节点停止运行的pod创建替代品。直接在节点上创建的pod不会执行，只被kubelet管理。因此为确保pod在另一个节点上重启，需要用ReplicationController等机制管理pod。

ReplicationController是一种Kubernetes资源，可确保pod始终保持运行状态，旨在创建和管理一个pod的多个副本。会持续监控正在运行的pod列表，确保pod数始终与标签选择器匹配。

ReplicationController的组成：

- Label selector: 确定ReplicationController作用域中有哪些pod；
- Replica count: 副本个数，指定应运行的pod数；
- Pod template: pod模板，用于创建新的pod副本。

更改标签选择器和pod模板对现有pod没有影响，会使现有pod脱离ReplicationController的范围，停止监控它们。

定义ReplicationController时不要指定pod选择器，让Kubernetes从pod模板提取。

可以删除ReplicationController保持pod运行，只是pod脱离了它的控制，可用于替换ReplicationController。

ReplicaSet表达能力更强，还允许匹配缺少某个标签的pod，或包含特定标签的pod。创建ReplicaSet需要指定其他的apiVersion。

DaemonSet，在每个节点上只运行一个pod副本，没有期望的副本数的概念，如果节点下线，不会在其他地方重新创建pod。甚至DaemonSet会将pod部署到不可调度的节点上。

Job资源，运行一种pod，在内部进程成功结束时，不重启容器，被认为处于完成状态，因此必须明确设置重启策略。completions可设置作业的pod运行多少次，parallelism属性指定多少个pod并行执行。activeDeadlineSeconds可限制pod的时间。



服务，是一种为一组功能相同的pod提供单一不变的接入点的资源。服务的IP地址和端口不会改变，客户端通过IP地址和端口号建立连接，这些连接会被路由到提供该服务的任意一个pod上，不需要知道每个单独的提供服务的pod。

服务的IP地址只能在集群内部访问，服务的主要目标是使集群内部的其他pod可以访问当前这组pod。如果希望特定客户端的请求每次都指向同一个pod，可以设置服务的sessionAffinity属性为ClientIP。会话亲和性不是在HTTP层面上工作，cookie是HTTP协议的一部分，所以不能支持cookie。

如果要创建一个有多端口的服务，必须给每个端口指定名字。标签选择器应用于整个服务，不能对每个端口做单独配置。如果不同的pod有不同的端口映射关系，需要创建两个服务。

pod开始运行时，Kubernetes会初始化一系统环境变量指向现在存在的服务。获得服务IP地址和端口号可通过环境变量，也可以通过内部的DNS服务获得，如通过backend-database.default.svc.cluster.local访问。服务的IP是一个虚拟IP，只有在与服务端口结合时才能有意义，直接ping不同。

服务并不是和pod直接相连的，有一种资源介于二者之间，就是Endpoint资源。Endpoint资源就是暴露一个服务的IP地址和端口的列表。如果创建不包含pod选择器的服务，将不会创建Endpoint资源。

ExternalName类型的服务，仅在DNS级别实施，客户端直接连接到外部服务，完全绕过服务代理，这个类型的服务甚至不用获得IP。

如果服务需要提供给外部客户端访问，可创建Ingress资源，通过一个IP地址公开多个服务，运行在HTTP层。NodePort类型，可提供一个专用的接口供外部客户端访问。LoadBalancer，NodePort的扩展，通过专用的负载均衡器IP连接到服务。

负载均衡选择的pod不一定在接收连接的同一节点上运行，需要额外的网络跳转才能到达pod。可以设置externalTrafficPolicy将外部通信重定向到接收连接的节点上运行的pod阻止额外的跳数。但如果没有本地pod存在，连接会挂起。

LoadBalancer需要自己的负载均衡器及独有的公有IP地址，Ingress只需要一个公网IP就能为许多服务提供访问。客户端向Ingress发送请求，Ingress会根据请求的主机名和路径决定请求转发到的服务。Ingress基于HTTP，能提供cookie。只有Ingress控制器运行，Ingress资源才能正常工作。

客户端对域名执行DNS查找，DNS服务器返回Ingress控制器的IP。客户端向Ingress控制器发送HTTP请求，在host头中指定域名，控制器从头部确定客户端要访问的服务，通过该服务关联的Endpoint对象查看pod的IP，转发请求。Ingress控制器不将请求转发给服务，只是用它选择一个pod。

就绪探针，判断pod是否准备好提供服务，检查通过加入就绪列表，否则移除就绪列表。



我们希望新容器可以在之前容器结束的位置继续运行，保存实际数据的目录。Kubernetes通过定义存储卷满足这个需求，是pod的一部分，和pod共享相同的生命周期。pod启动时创建卷，删除时销毁卷。

