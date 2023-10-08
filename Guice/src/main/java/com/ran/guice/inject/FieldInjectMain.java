package com.ran.guice.inject;

import com.google.inject.Guice;
import com.google.inject.Inject;

/**
 * FieldInjectMain
 * 成员变量注入
 * @author rwei
 * @since 2023/10/8 21:05
 */
public class FieldInjectMain {
    @Inject
    private Service service;

    public Service getService() {
        return service;
    }

    public static void main(String[] args) {
        FieldInjectMain instance = Guice.createInjector().getInstance(FieldInjectMain.class);
        instance.getService().execute();
    }
}
