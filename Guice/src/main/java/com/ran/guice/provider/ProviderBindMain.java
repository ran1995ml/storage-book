package com.ran.guice.provider;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;

/**
 * ProviderBindMain
 * 使用Provider注入对象
 * @author rwei
 * @since 2023/10/8 21:28
 */
public class ProviderBindMain {
    @Inject
    private Service service;

    public static void main(String[] args) {
        ProviderBindMain instance = Guice.createInjector(new Module() {
            public void configure(Binder binder) {
                binder.bind(Service.class).toProvider(HomeServiceProvider.class);
            }
        }).getInstance(ProviderBindMain.class);
        instance.service.execute();
    }
}
