package com.ran.javase.basic.nio;

import java.nio.IntBuffer;

/**
 * BufferDemo
 *
 * @author rwei
 * @since 2023/12/5 11:19
 */
public class BufferDemo {
    public static void main(String[] args) {
        //写入buffer
        IntBuffer intBuffer = IntBuffer.allocate(5);
        for (int i=0;i<intBuffer.capacity();i++) {
            intBuffer.put(i * 2);
        }

        //读写切换
        intBuffer.flip();

        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());
        }
    }
}
