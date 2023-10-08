package com.ran.guice.inject;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;

/**
 * StaticInjectMain
 * 静态成员变量注入
 * @author rwei
 * @since 2023/10/8 21:12
 */
public class StaticInjectMain {
    @Inject
    private static Service service;

    public static void main(String[] args) {
        Guice.createInjector(new Module() {
            public void configure(Binder binder) {
                binder.requestStaticInjection(StaticInjectMain.class);
            }
        });
        StaticInjectMain.service.execute();
    }
}
