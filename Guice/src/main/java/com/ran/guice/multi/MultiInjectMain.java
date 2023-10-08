package com.ran.guice.multi;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;

/**
 * MultiInjectMain
 *
 * @author rwei
 * @since 2023/10/8 21:20
 */
public class MultiInjectMain {
    @Inject
    @Home
    private Service homeService;

    @Inject
    @Www
    private Service wwwService;

    public static void main(String[] args) {
        MultiInjectMain instance = Guice.createInjector(new Module() {
            public void configure(Binder binder) {
                binder.bind(Service.class).annotatedWith(Www.class).to(WService.class);
                binder.bind(Service.class).annotatedWith(Home.class).to(HomeService.class);
            }
        }).getInstance(MultiInjectMain.class);
        instance.homeService.execute();
        instance.wwwService.execute();
    }
}
