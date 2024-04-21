package com.ran.javase.basic.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * BIOServer
 * BIO实现服务端
 * @author rwei
 * @since 2023/12/5 10:18
 */
public class BIOServer {
    public static void main(String[] args) {
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket(6666)) {
            while (true) {
                try {
                    System.out.println(Thread.currentThread().getId() + ":" + Thread.currentThread().getName());
                    System.out.println("Waiting for connecting");
                    final Socket socket = serverSocket.accept();
                    System.out.println("Connected client");
                    newCachedThreadPool.execute(() -> handler(socket));
                } catch (Exception e) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handler(Socket socket) {
        try {
            System.out.println(Thread.currentThread().getId() + ":" + Thread.currentThread().getName());
            byte[] bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();
            while (true) {
                System.out.println(Thread.currentThread().getId() + ":" + Thread.currentThread().getName());
                System.out.println("reading...");
                int read = inputStream.read(bytes);
                if (read != -1) {
                    System.out.println(new String(bytes, 0, read));
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Closing connection");
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
