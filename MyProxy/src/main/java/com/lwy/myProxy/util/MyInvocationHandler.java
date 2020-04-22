package com.lwy.myProxy.util;

import java.lang.reflect.Method;

public interface MyInvocationHandler {

    public Object invoke(Method method, Object[] args);

}
