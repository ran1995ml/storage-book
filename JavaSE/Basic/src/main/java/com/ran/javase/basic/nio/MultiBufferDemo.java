package com.ran.javase.basic.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * MultiBufferDemo
 * 多个buffer完成读写操作
 * @author rwei
 * @since 2023/12/5 16:21
 */
public class MultiBufferDemo {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(6666);
        channel.socket().bind(inetSocketAddress);

        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        SocketChannel socketChannel = channel.accept();
        int messageLength = 8;

        while (true) {
            try {
                //累计读取的字节数
                int byteRead = 0;

                while (byteRead < messageLength) {
                    long length = socketChannel.read(byteBuffers);
                    byteRead += 1;
                    Arrays.stream(byteBuffers).map(buffer -> "postion=" + buffer.position() + ", limit=" + buffer.limit())
                            .forEach(System.out::println);
                }

                Arrays.asList(byteBuffers).forEach(Buffer::flip);
                int byteWrite = 0;
                while (byteWrite < messageLength) {
                    long length = socketChannel.write(byteBuffers);
                    byteWrite += 1;
                }

                Arrays.asList(byteBuffers).forEach(Buffer::clear);
                System.out.println("byteRead:" + byteRead + " byteWrite:" + byteWrite + " messageLength:" + messageLength);
            } catch (Exception e) {
                System.exit(1);
            }
        }
    }
}
