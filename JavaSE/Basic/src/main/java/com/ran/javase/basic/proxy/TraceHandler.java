package com.ran.javase.basic.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * TraceHandler
 *
 * @author rwei
 * @since 2023/11/10 15:33
 */
public class TraceHandler implements InvocationHandler {
    private Object target;

    public TraceHandler(Object t) {
        this.target = t;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //打印传入参数
        System.out.print(target);
        //打印方法名
        System.out.print("." + method.getName() + "(");
        if (args != null) {
            for (int i=0;i<args.length;i++) {
                System.out.print(args[i]);
                if (i < args.length - 1) {
                    System.out.print(", ");
                }
            }
        }
        System.out.println(")");
        return method.invoke(target, args);
    }
}
