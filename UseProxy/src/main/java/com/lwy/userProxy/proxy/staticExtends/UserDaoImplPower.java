package com.lwy.userProxy.proxy.staticExtends;

import com.lwy.userProxy.dao.UserDaoImpl;

public class UserDaoImplPower extends UserDaoImpl {
    @Override
    public void query() {
        System.out.println("power--extends");
        super.query();
    }
}
