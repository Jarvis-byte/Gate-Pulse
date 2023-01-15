package com.example.fmoapplication;

public class User {
    private String Name;
    private String Uid;

    public User(String name, String uid) {
        Name = name;
        Uid = uid;
    }

    public User() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
