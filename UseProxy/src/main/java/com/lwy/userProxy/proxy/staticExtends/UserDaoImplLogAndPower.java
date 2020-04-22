package com.lwy.userProxy.proxy.staticExtends;

public class UserDaoImplLogAndPower extends UserDaoImplPower {
    @Override
    public void query() {

        System.out.println("logs--extend");
        super.query();
    }
}
