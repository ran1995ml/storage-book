package com.ran.guice.multi;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

/**
 * NoAnnoInjectMain
 * 不声明注解的形式
 * @author rwei
 * @since 2023/10/8 21:23
 */
public class NoAnnoInjectMain {
    @Inject
    @Named("Home")
    private Service homeService;

    @Inject
    @Named("Www")
    private Service wwwService;

    public static void main(String[] args) {
        NoAnnoInjectMain instance = Guice.createInjector(new Module() {
            public void configure(Binder binder) {
                binder.bind(Service.class).annotatedWith(Names.named("Www")).to(WService.class);
                binder.bind(Service.class).annotatedWith(Names.named("Home")).to(HomeService.class);
            }
        }).getInstance(NoAnnoInjectMain.class);
        instance.homeService.execute();
        instance.wwwService.execute();
    }
}
