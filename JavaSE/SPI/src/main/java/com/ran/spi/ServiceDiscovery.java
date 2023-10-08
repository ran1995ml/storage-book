package com.ran.spi;

import java.util.ServiceLoader;

/**
 * ServiceDiscovery
 *
 * @author rwei
 * @since 2023/10/8 21:36
 */
public class ServiceDiscovery {
    public static void main(String[] args) {
        ServiceLoader<MyServiceProvider> serviceLoader = ServiceLoader.load(MyServiceProvider.class);
        for (MyServiceProvider provider : serviceLoader) {
            System.out.println(provider.getName());
        }
    }
}
