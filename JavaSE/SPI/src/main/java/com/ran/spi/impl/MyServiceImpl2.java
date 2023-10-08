package com.ran.spi.impl;

import com.ran.spi.MyServiceProvider;

/**
 * MyServiceImpl2
 *
 * @author rwei
 * @since 2023/10/8 21:38
 */
public class MyServiceImpl2 implements MyServiceProvider {
    public String getName() {
        return "service 2";
    }
}
