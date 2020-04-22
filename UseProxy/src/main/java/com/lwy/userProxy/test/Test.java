package com.lwy.userProxy.test;

import com.lwy.userProxy.dao.UserDao;
import com.lwy.userProxy.dao.UserDaoImpl;
import com.lwy.userProxy.proxy.staticExtends.UserDaoImplLogAndPower;
import com.lwy.userProxy.proxy.staticPolymerization.UserDaoImplLog;
import com.lwy.userProxy.proxy.staticPolymerization.UserDaoImplPower;

public class Test {

    public static void main(String[] args) {
        // 继承方式
        UserDao userDao = new UserDaoImplLogAndPower();
        userDao.query();

        // 聚合方式
        UserDao target = new UserDaoImplPower(new UserDaoImpl());
        UserDao proxy = new UserDaoImplLog(target);
        proxy.query();
    }

}
