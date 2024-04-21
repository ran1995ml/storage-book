package com.ran.javase.basic.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * ReflectMain
 * 利用反射获取所有类的元信息
 * @author rwei
 * @since 2023/11/8 11:27
 */
public class ReflectMain {
    public static void main(String[] args) throws ClassNotFoundException {
        String name = "java.util.Date";
        Class<?> cl = Class.forName(name);

        printModifier(cl);
        printSuper(cl);
        printConstructors(cl);
        printMethods(cl);
        printFields(cl);
    }

    /**
     * 返回该类的权限范围
     * @param cl class
     */
    public static void printModifier(Class<?> cl) {
        String modifier = Modifier.toString(cl.getModifiers());
        System.out.println(modifier);
    }

    /**
     * 返回父类
     * @param cl class
     */
    public static void printSuper(Class<?> cl) {
        Class<?> superclass = cl.getSuperclass();
        System.out.println(superclass.getName());
    }

    /**
     * 返回所有的构造函数
     * @param cl class
     */
    public static void printConstructors(Class<?> cl) {
        Constructor<?>[] constructors = cl.getDeclaredConstructors();
        for (Constructor<?> c: constructors) {
            String name = c.getName();
            System.out.print(name + "(");
            String modifier = Modifier.toString(c.getModifiers());
            Class<?>[] parameterTypes = c.getParameterTypes();
            for (Class<?> type: parameterTypes) {
                System.out.print(type.getName() + " ");
            }
            System.out.println(")");
        }
    }

    /**
     * 返回所有的方法
     * @param cl class
     */
    public static void printMethods(Class<?> cl) {
        Method[] methods = cl.getDeclaredMethods();
        for (Method method: methods) {
            Class<?> returnType = method.getReturnType();
            String methodName = method.getName();
            String modifier = Modifier.toString(method.getModifiers());
            System.out.print(modifier + " " + returnType.getName() + " " + methodName + "(");
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> type: parameterTypes) {
                System.out.print(type + " ");
            }
            System.out.println(")");
        }
    }

    /**
     * 返回所有的域
     * @param cl class
     */
    public static void printFields(Class<?> cl) {
        Field[] fields = cl.getDeclaredFields();
        for (Field field: fields) {
            Class<?> type = field.getType();
            String name = field.getName();
            String modifier = Modifier.toString(type.getModifiers());
            System.out.println(modifier + " " + type.getName() + " " + name);
        }
    }
}
