package com.ran.javase.basic.console;

import java.util.Scanner;

/**
 * ConsoleInput
 * 读取console输入的内容
 * @author rwei
 * @since 2023/11/5 22:18
 */
public class ConsoleInput {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        System.out.println(s);
        int i = scanner.nextInt();
        System.out.println(i);
    }
}
