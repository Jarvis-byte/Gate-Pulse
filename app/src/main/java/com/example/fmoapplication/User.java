package com.example.fmoapplication;

public class User {
    private String Name;
    private String Uid;
    private String isAdmin;
    private String isVerified;
    private String Email;


    public User() {
    }

    public User(String name, String uid, String isAdmin, String isVerified, String email) {
        Name = name;
        Uid = uid;
        this.isAdmin = isAdmin;
        this.isVerified = isVerified;
        Email = email;
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

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
