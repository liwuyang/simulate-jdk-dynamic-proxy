package com.lwy.myProxy.dao;

import com.lwy.myProxy.entity.User;

public interface UserDao {

    public void query();

    public void queryParam(String string);

    public String queryString();

    public User queryUser();

    public User queryUserByOther(String string,Integer integer,User user);

}
