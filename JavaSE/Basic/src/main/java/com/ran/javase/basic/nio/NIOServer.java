package com.ran.javase.basic.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * NIOServer
 * 服务端和客户端
 * @author rwei
 * @since 2023/12/5 22:17
 */
public class NIOServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        try (Selector selector = Selector.open()) {
            serverSocketChannel.socket().bind(new InetSocketAddress(6666));
            //设为非阻塞
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                try {
                    //等待1s无事发生，直接返回
                    if (selector.select(1000) == 0) {
                        System.out.println("Waiting for 1 second, no connection.");
                        continue;
                    }

                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        //如果是OP_ACCEPT事件，有新的client连接
                        if (key.isAcceptable()) {
                            //客户端生成socketChannel，注册到selector，关联buffer
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            System.out.println("Client connect");
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                        }
                        //发生OP_READ，获取关联的channel和buffer
                        if (key.isReadable()) {
                            try (SocketChannel channel = (SocketChannel) key.channel()) {
                                ByteBuffer buffer = (ByteBuffer) key.attachment();
                                channel.read(buffer);
                                System.out.println("Client: " + new String(buffer.array()));
                            }
                        }
                        //移除当前selectionKey，防止重复操作
                        iterator.remove();
                    }
                } catch (Exception e) {
                    System.exit(1);
                }
            }
        }
    }
}
