package com.lwy.myProxy.entity;

public class User {

    public User(String name) {
        this.name = name;
    }

    public User() {
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
