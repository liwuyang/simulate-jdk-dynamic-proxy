package com.lwy.myProxy.dao;

import com.lwy.myProxy.entity.User;

public class UserDaoImpl implements UserDao {
    public void query() {
        System.out.println("query");
    }

    public void queryParam(String string) {
        System.out.println(string);
    }

    public String queryString() {
        return "queryString";
    }

    @Override
    public User queryUser() {
        return new User("lwy");
    }

    @Override
    public User queryUserByOther(String string, Integer integer, User user) {
        return new User("test");
    }
}
