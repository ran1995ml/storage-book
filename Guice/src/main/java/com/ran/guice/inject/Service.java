package com.ran.guice.inject;

import com.google.inject.ImplementedBy;

/**
 * Service
 *
 * @author rwei
 * @since 2023/10/8 21:04
 */
@ImplementedBy(ServiceImpl1.class)
public interface Service {
    void execute();
}
