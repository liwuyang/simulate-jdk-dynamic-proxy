package com.lwy.userProxy.proxy.staticPolymerization;

import com.lwy.userProxy.dao.UserDao;

// 代理对象
// 静态代理 聚合实现
public class UserDaoImplLog implements UserDao {

    UserDao userDao;

    public UserDaoImplLog(UserDao userDao) {
        this.userDao = userDao;
    }

    public void query() {
        System.out.println("log--polymerization");
        userDao.query();

    }
}
