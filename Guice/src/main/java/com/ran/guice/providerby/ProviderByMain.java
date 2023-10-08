package com.ran.guice.providerby;

import com.google.inject.Guice;
import com.google.inject.Inject;

/**
 * ProviderByMain
 *
 * @author rwei
 * @since 2023/10/8 21:32
 */
public class ProviderByMain {
    @Inject
    private Service service;

    public static void main(String[] args) {
        ProviderByMain instance = Guice.createInjector().getInstance(ProviderByMain.class);
        instance.service.execute();
    }
}
