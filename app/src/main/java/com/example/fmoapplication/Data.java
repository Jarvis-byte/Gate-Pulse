package com.example.fmoapplication;

public class Data {
    String uid;
    String name;
    String date;
    String time_FROM;
    String time_to;
    boolean isDoneForToday;
    int approvalStatus;

    public Data() {
    }

    public Data(String uid, String name, String date, String time_FROM, String time_to, boolean isDoneForToday, int approvalStatus) {
        this.uid = uid;
        this.name = name;
        this.date = date;
        this.time_FROM = time_FROM;
        this.time_to = time_to;
        this.isDoneForToday = isDoneForToday;
        this.approvalStatus = approvalStatus;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime_FROM() {
        return time_FROM;
    }

    public void setTime_FROM(String time_FROM) {
        this.time_FROM = time_FROM;
    }

    public String getTime_to() {
        return time_to;
    }

    public void setTime_to(String time_to) {
        this.time_to = time_to;
    }

    public boolean isDoneForToday() {
        return isDoneForToday;
    }

    public int getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(int approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}
