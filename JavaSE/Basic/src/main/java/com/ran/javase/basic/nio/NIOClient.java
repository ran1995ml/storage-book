package com.ran.javase.basic.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * NIOClient
 *
 * @author rwei
 * @since 2023/12/6 14:48
 */
public class NIOClient {
    public static void main(String[] args) throws InterruptedException {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.configureBlocking(false);
            InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);
            if (!socketChannel.connect(inetSocketAddress)) {
                while (!socketChannel.finishConnect()) {
                    System.out.println("Waiting for connection, do other works");
                }
            }
            //连接成功后发送数据
            String str = "nio";
            ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
            socketChannel.write(buffer);
        } catch (Exception e) {
            System.exit(1);
        }
    }
}
