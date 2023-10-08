package com.ran.guice.providerby;

import com.google.inject.Provider;

/**
 * HomeServiceProvider
 *
 * @author rwei
 * @since 2023/10/8 21:31
 */
public class HomeServiceProvider implements Provider<Service> {

    public Service get() {
        return new HomeService();
    }
}
