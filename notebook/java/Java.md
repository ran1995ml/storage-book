@ResponseBody，直接将方法返回值序列化成json返回给客户端
@RequestMapping，给整个控制器设置一个基本url
@RequestParam，从http请求获取参数值，作用于方法参数上


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")，json中有一个type属性标识对象的具体类型
@JsonCreator，标记一个构造方法，指示反序列化时应该使用的方法
@JacksonInject，指示反序列化时应该注入的特定的值

ComparisonChain 是 Google Guava 库中的一个类，用于创建链式比较器，帮助你进行多个字段的比较。通过 ComparisonChain，你可以按顺序比较多个字段，并在找到不等的情况下立即返回结果，而不必一直比较所有字段。

# IO

## BIO

从外部到程序称为输入流；从程序到外部，称为输出流。

BIO，读取发送数据的过程，是线程阻塞的。实现并发响应，每个连接需要独立的线程单独处理。

## NIO

同步非阻塞模式，如果没有可用的数据，不会保持线程阻塞，一个线程可以处理多个请求。BIO以流的方式处理数据，NIO以块的方式处理数据。基于 `Channel` 和 `Buffer` 操作，数据总是从 `Channel` 读取到 `Buffer`，或从 `Buffer` 写入到 `Channel`，`Selector` 用于监听多个 `Channel` 的事件，如连接请求和数据到达，使用单个线程就可以监听多个 `Channel`。

### ByteBuffer

用于IO操作的缓冲区，`ByteBuffer` 最为常用，支持堆内存和堆外内存。使用堆外内存的好处是，减少 `GC` 线程资源占有，直接操作堆外内存，可以节省一次堆外内存和堆内内存的复制。

可理解为一组基本数据类型，存储地址连续的数组，支持读写操作，通过 `capacity`、`position`、`limit` 保存数据的当前位置状态：

- `capacity`：缓冲区数组的总长度；
- `position`：下一个要操作的数据元素位置；
- `limit`：缓冲区数据中不可操作的下一个元素位置

### Channel

读数据：创建一个 `Buffer`，请求 `Channel` 读取数据；写数据：创建一个 `Buffer`，请求 `Channel` 写入数据。`Channel` 可以读和写，标准IO流是单向的；`Channel` 可以异步读写，标准IO需要线程阻塞等到直到读写操作完成；`Channel` 总是基于 `Buffer` 读写。

### Selector

对应一个线程，可检查多个 `Channel` 的状态是否可读可写。基于操作系统提供的IO复用功能，实现单线程管理多个网络连接，根据不同的事件，在各个 `Channel` 上切换。多个 `Channel` 以事件的方式注册到同一个 `Selector`，若有事件发生，获取事件后对每个事件进行相应的处理。

- `ServerSocketChannel`：监听新的客户端 `Socket` 连接，获得和客户端连接通道 `SocketChannel`，每个客户端生成对应的 `SocketChannel`；
- `SocketChannel`：负责读写操作，把缓冲区的数据写入 `Channel`，或把 `Channel` 的数据读到缓冲区；
- `Selector`：可注册多个 `SocketChannel`，注册后返回一个 `SelectionKey`；
- `SlectionKey`：关联 `SocketChannel` 和 `Selector`，**OP_ACCEPT**，有新的网络连接，值为16；**OP_CONNECT**，连接已经建立，值为8；**OP_READ**，读操作，值为1；**OP_WRITE**，写操作，值为4；

### 零拷贝

减少数据复制次数和上下文切换，零指的是内存中数据拷贝次数为0

#### mmap

通过内存映射，将文件映射到内核缓冲区，用户空间可以共享内核空间的数据，这样在进行网络传输时，可以减少内核空间到用户空间的拷贝次数，适合小数据量读写。

#### sendFile

数据不经过用户态，直接从内核缓冲区拷贝到协议栈，和用户态无关，减少了一次上下文切换，适合大文件传输。

## Reference

https://juejin.cn/post/6844903993844432909
https://cloud.tencent.com/developer/article/1739347

# AOP
面向切面编程，封装与业务无关，但业务模块共同调用的代码，减少系统的重复代码，降低模块间的耦合度。需要考虑的三个点：

1. 在哪里切入；
2. 什么时候切入；
3. 切入后做什么
