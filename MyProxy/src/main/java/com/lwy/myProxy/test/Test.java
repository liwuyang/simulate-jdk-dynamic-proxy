package com.lwy.myProxy.test;

import com.lwy.myProxy.dao.UserDao;
import com.lwy.myProxy.dao.UserDaoImpl;
import com.lwy.myProxy.entity.User;
import com.lwy.myProxy.proxy.MyProxyNew;
import com.lwy.myProxy.util.MyInvocationHandlerImpl;

public class Test {

    public static void main(String[] args) {

        // 使用自定义代理
        // 代理类里面使用的URLClassLoader，因为要加载的类不在工程里，是我们自己生成到本地磁盘的文件
        /*UserDao userDao = (UserDao) MyProxy.newInstance(new UserDaoImpl());

        userDao.query();
        userDao.queryParam("queryParam");
        System.out.println(userDao.queryString());
        System.out.println(userDao.queryUser());*/

        // 使用自定义代理
        // 代理类里面使用的URLClassLoader，因为要加载的类不在工程里，是我们自己生成到本地磁盘的文件
        UserDao userDao = (UserDao) MyProxyNew.newInstance(UserDao.class,
                new MyInvocationHandlerImpl(new UserDaoImpl()));

        userDao.query();
        userDao.queryParam("queryParam");
        System.out.println(userDao.queryString());
        System.out.println(userDao.queryUser());
        System.out.println(userDao.queryUserByOther("test", 1, new User("lwy")));

        /*// 使用JDK动态代理
        UserDao userDao = (UserDao) Proxy.newProxyInstance(Test.class.getClassLoader(),
                new Class[]{UserDao.class},
                new UserInvocationHandler(new UserDaoImpl()));

        userDao.query();
        userDao.queryParam("queryParam");
        System.out.println(userDao.queryString());
        System.out.println(userDao.queryUser());*/
    }

}
