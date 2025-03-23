package com.library.model;

public class ActivityRecord {
    private String date;
    private String activity;
    private String user;
    
    public ActivityRecord() {
    }
    
    public ActivityRecord(String date, String activity, String user) {
        this.date = date;
        this.activity = activity;
        this.user = user;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getActivity() {
        return activity;
    }
    
    public void setActivity(String activity) {
        this.activity = activity;
    }
    
    public String getUser() {
        return user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
}