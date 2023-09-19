package com.example.fmoapplication.Model;

public class AddVisitor {
    private String uid;
    private String nameofsubmitor;
    private String nameOfVisitor;
    private String purposeOfvisit;
    private String date;
    private String Time_from;
    private String Time_to;
    private int seen;
    private String seenBy;

    public AddVisitor(String uid, String nameofsubmitor, String nameOfVisitor, String purposeOfvisit, String date, String time_from, String time_to, int seen, String seenBy) {
        this.uid = uid;
        this.nameofsubmitor = nameofsubmitor;
        this.nameOfVisitor = nameOfVisitor;
        this.purposeOfvisit = purposeOfvisit;
        this.date = date;
        Time_from = time_from;
        Time_to = time_to;
        this.seen = seen;
        this.seenBy = seenBy;
    }

    public AddVisitor() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNameofsubmitor() {
        return nameofsubmitor;
    }

    public void setNameofsubmitor(String nameofsubmitor) {
        this.nameofsubmitor = nameofsubmitor;
    }

    public String getNameOfVisitor() {
        return nameOfVisitor;
    }

    public void setNameOfVisitor(String nameOfVisitor) {
        this.nameOfVisitor = nameOfVisitor;
    }

    public String getPurposeOfvisit() {
        return purposeOfvisit;
    }

    public void setPurposeOfvisit(String purposeOfvisit) {
        this.purposeOfvisit = purposeOfvisit;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime_from() {
        return Time_from;
    }

    public void setTime_from(String time_from) {
        Time_from = time_from;
    }

    public String getTime_to() {
        return Time_to;
    }

    public void setTime_to(String time_to) {
        Time_to = time_to;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public String getSeenBy() {
        return seenBy;
    }

    public void setSeenBy(String seenBy) {
        this.seenBy = seenBy;
    }
}
