# Netty

`NIO` 有很多问题，例如 `epoll` 回导致 `Selector` 空轮询，导致 `CPU` 100%。`Netty` 主要对 `NIO` 做了封装，并解决客户端断连重连、网络闪断、半包读写、失败缓存、网络拥塞等问题。

## Reference

https://cloud.tencent.com/developer/article/1754078