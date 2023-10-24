`Futures.transform` : 接受一个输入的 ListenableFuture 和一个 AsyncFunction（异步函数），并返回一个新的 ListenableFuture，该新 ListenableFuture 会在原始 ListenableFuture 的结果可用时应用提供的函数。
`Futures.immediateFuture` : 创建一个已知结果的 ListenableFuture
`SettableFuture` : 实现了 `ListenableFuture` 接口，允许显式设置返回结果，不必等待结果返回

`@GuardedBy("giant")` : 访问某个字段或方法必须标注某个锁

`BlockingQueue` : 允许多个线程同时进行队列操作
`CountDownLatch` : 一个或多个线程完成某些操作，才继续执行。通过计数器设置初始值，变为0，等待的线程才继续执行