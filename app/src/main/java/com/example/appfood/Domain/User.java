package com.example.appfood.Domain;

public class User {
    private String UserName;
    private String FullName;
    private String userId;
    private String Email;

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public User(String userName, String fullName, String userId, String email, String phone) {
        UserName = userName;
        FullName = fullName;
        userId = userId;
        Email = email;
        Phone = phone;
    }

    private String Phone;



    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getuserId() {
        return userId;
    }

    public void setuserId(String userId) {
        userId = userId;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public User() {
    }

    public User(String userName, String fullName, String userId, String email) {
        UserName = userName;
        FullName = fullName;
        userId = userId;
        Email = email;
    }
}
