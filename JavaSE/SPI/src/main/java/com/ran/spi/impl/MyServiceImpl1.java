package com.ran.spi.impl;

import com.ran.spi.MyServiceProvider;

/**
 * MyServiceImpl1
 *
 * @author rwei
 * @since 2023/10/8 21:37
 */
public class MyServiceImpl1 implements MyServiceProvider {
    public String getName() {
        return "my service 1";
    }
}
