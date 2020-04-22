package com.lwy.userProxy.proxy.staticPolymerization;

import com.lwy.userProxy.dao.UserDao;

// 静态代理 聚合实现
public class UserDaoImplPower implements UserDao {

    UserDao userDao;

    public UserDaoImplPower(UserDao userDao) {
        this.userDao = userDao;
    }

    public void query() {
        System.out.println("power--polymerization");
        userDao.query();

    }
}
