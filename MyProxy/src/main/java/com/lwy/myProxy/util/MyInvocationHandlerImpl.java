package com.lwy.myProxy.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyInvocationHandlerImpl implements MyInvocationHandler {

    // 传入目标对象
    Object target;

    public MyInvocationHandlerImpl(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Method method, Object[] args) {

        System.out.println("---------------proxy-----------------");

        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
