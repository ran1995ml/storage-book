package com.ran.guice.base;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Main
 *
 * @author rwei
 * @since 2023/10/8 20:58
 */
public class Main {
    public static void main(String[] args) {
        bindOneClass();
    }

    /**
     * 一个接口只有一个实现类的情况
     * 绑定接口的实现类
     */
    public static void bindOneClass() {
        Injector injector = Guice.createInjector(new Module() {
            public void configure(Binder binder) {
                binder.bind(HelloWorld.class).to(HelloWorldImpl1.class);
            }
        });
        HelloWorld instance = injector.getInstance(HelloWorld.class);
        System.out.println(instance.sayHello());
        System.out.println(instance.getClass().getName());
    }
}
