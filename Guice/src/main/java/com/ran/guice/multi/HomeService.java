package com.ran.guice.multi;

/**
 * HomeService
 *
 * @author rwei
 * @since 2023/10/8 21:17
 */
public class HomeService implements Service {
    public void execute() {
        System.out.println("home service");
    }
}
