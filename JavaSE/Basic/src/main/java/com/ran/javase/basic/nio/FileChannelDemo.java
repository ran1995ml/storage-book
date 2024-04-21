package com.ran.javase.basic.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * FileChannelDemo
 *
 * @author rwei
 * @since 2023/12/5 14:05
 */
public class FileChannelDemo {
    public static void main(String[] args) {
        String str = "nio";
        String path = "/Users/rwei/IdeaProjects/storage-notebook/JavaSE/Basic/src/main/java/com/ran/javase/basic/nio/file/test.txt";
//        write(str, path);
//        read(path);
//        copy(path);
        transfer(path);
    }

    public static void write(String str, String path) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            FileChannel channel = fileOutputStream.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.put(str.getBytes());
            byteBuffer.flip();
            channel.write(byteBuffer);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void read(String path) {
        File file = new File(path);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            FileChannel channel = fileInputStream.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
            channel.read(byteBuffer);
            System.out.println(new String(byteBuffer.array()));
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copy(String path) {
        String newPath = path.replace("test", "test1");
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            FileChannel inputChannel = fileInputStream.getChannel();
            try (FileOutputStream fileOutputStream = new FileOutputStream(newPath)) {
                FileChannel outputChannel = fileOutputStream.getChannel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                while (true) {
                    byteBuffer.clear();
                    int read = inputChannel.read(byteBuffer);
                    if (read == -1) break;
                    byteBuffer.flip();
                    outputChannel.write(byteBuffer);
                }
                outputChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            inputChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void transfer(String path) {
        String newPath = path.replace("test", "test2");
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(newPath)) {
                FileChannel inputChannel = fileInputStream.getChannel();
                FileChannel outputChannel = fileOutputStream.getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                inputChannel.close();
                outputChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
