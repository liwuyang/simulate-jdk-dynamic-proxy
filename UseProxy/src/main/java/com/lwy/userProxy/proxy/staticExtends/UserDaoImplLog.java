package com.lwy.userProxy.proxy.staticExtends;

import com.lwy.userProxy.dao.UserDaoImpl;

// 代理对象
// 静态代理  继承实现
public class UserDaoImplLog extends UserDaoImpl {


    @Override
    public void query() {
        System.out.println("log--extends");
        super.query();
    }
}
