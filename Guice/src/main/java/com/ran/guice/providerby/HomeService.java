package com.ran.guice.providerby;

/**
 * HomeService
 *
 * @author rwei
 * @since 2023/10/8 21:31
 */
public class HomeService implements Service{
    public void execute() {
        System.out.println("home");
    }
}
