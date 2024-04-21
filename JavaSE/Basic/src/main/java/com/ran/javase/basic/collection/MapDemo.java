package com.ran.javase.basic.collection;

import java.util.concurrent.ConcurrentHashMap;

/**
 * MapDemo
 *
 * @author rwei
 * @since 2023/12/8 16:31
 */
public class MapDemo {
    public static void main(String[] args) {
        computeIfAbsentTest();
    }

    public static void computeIfAbsentTest() {
        ConcurrentHashMap<String, Integer> concurrentMap = new ConcurrentHashMap<>();
        concurrentMap.put("1", 1);
        concurrentMap.put("2", 1);
        Integer value1 = concurrentMap.computeIfAbsent("2", Integer::valueOf);
        System.out.println(value1);
        Integer value2 = concurrentMap.computeIfAbsent("3", Integer::valueOf);
        System.out.println(value2);
        Integer value3 = concurrentMap.putIfAbsent("4", 4);
        System.out.println(value3);
        Integer value4 = concurrentMap.putIfAbsent("3", 1);
        System.out.println(value4);
        Integer value5 = concurrentMap.remove("5");
        System.out.println(value5);
    }
}
