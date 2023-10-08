package com.ran.guice.provider;

import com.google.inject.*;

/**
 * ProviderInjectMain
 * 注入Provider来注入类
 * @author rwei
 * @since 2023/10/8 21:29
 */
public class ProviderInjectMain {
    @Inject
    private Provider<Service> provider;

    public static void main(String[] args) {
        ProviderInjectMain instance = Guice.createInjector(new Module() {
            public void configure(Binder binder) {
                binder.bind(Service.class).toProvider(HomeServiceProvider.class);
            }
        }).getInstance(ProviderInjectMain.class);
        instance.provider.get().execute();
    }
}
