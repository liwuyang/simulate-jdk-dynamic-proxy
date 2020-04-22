package com.lwy.myProxy.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理逻辑类
 */
public class UserInvocationHandler implements InvocationHandler {

    Object target;

    public UserInvocationHandler(Object target) {
        this.target = target;
    }

    /**
     * @param proxy  代理对象
     * @param method 目标方法
     * @param args   目标方法参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        System.out.println("------------------proxy--------------------");

        return method.invoke(target, args);
    }
}
