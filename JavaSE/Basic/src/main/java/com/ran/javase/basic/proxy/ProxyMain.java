package com.ran.javase.basic.proxy;

import java.lang.reflect.*;
import java.util.*;

/**
 * ProxyMain
 * 使用代理类跟踪方法的调用
 * @author rwei
 * @since 2023/11/10 14:50
 */
public class ProxyMain {
    public static void main(String[] args) {
        Object[] elements = new Object[1000];

        for (int i=0;i<elements.length;i++) {
            Integer value = i + 1;
            InvocationHandler handler = new TraceHandler(value);
            Object proxy = Proxy.newProxyInstance(null, new Class[]{Comparable.class}, handler);
            elements[i] = proxy;
        }

        Integer key = new Random().nextInt(elements.length) + 1;
        int result = Arrays.binarySearch(elements, key);

        if (result >= 0) {
            System.out.println(elements[result]);
        }
    }
}
