package com.colab.app;

public class HomeModel {
    private String uid;

    public HomeModel(){}

    public HomeModel(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
