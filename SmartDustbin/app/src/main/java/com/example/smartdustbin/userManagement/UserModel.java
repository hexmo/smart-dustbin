package com.example.smartdustbin.userManagement;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String userId;
    private String fullName;
    private String phoneNumber;

    public UserModel() {
    }

    public UserModel(String userId, String fullName, String phoneNumber) {
        this.userId = userId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    //Getters

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }


    //Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
