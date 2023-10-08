package com.ran.guice.inject;

import com.google.inject.Guice;
import com.google.inject.Inject;

/**
 * ConstructorInjectMain
 * 构造函数注入
 * @author rwei
 * @since 2023/10/8 21:02
 */
public class ConstructorInjectMain {
    private Service service;

    @Inject
    public ConstructorInjectMain(Service service) {
        this.service = service;
    }

    public static void main(String[] args) {
        ConstructorInjectMain instance = Guice.createInjector().getInstance(ConstructorInjectMain.class);
        instance.getService().execute();
    }

    public Service getService() {
        return service;
    }
}