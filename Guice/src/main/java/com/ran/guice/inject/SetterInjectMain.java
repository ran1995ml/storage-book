package com.ran.guice.inject;

import com.google.inject.Guice;
import com.google.inject.Inject;

/**
 * SetterInjectMain
 * setter方法注入
 * @author rwei
 * @since 2023/10/8 21:11
 */
public class SetterInjectMain {
    private Service service;

    public static void main(String[] args) {
        SetterInjectMain instance = Guice.createInjector().getInstance(SetterInjectMain.class);
        instance.getService().execute();
    }

    public Service getService() {
        return service;
    }

    @Inject
    public void setService(Service service) {
        this.service = service;
    }
}
