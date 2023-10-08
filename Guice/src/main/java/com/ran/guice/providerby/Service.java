package com.ran.guice.providerby;

import com.google.inject.ProvidedBy;


/**
 * Service
 *
 * @author rwei
 * @since 2023/10/8 21:30
 */
@ProvidedBy(HomeServiceProvider.class)
public interface Service {
    void execute();
}
