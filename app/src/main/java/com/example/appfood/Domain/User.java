package com.example.appfood.Domain;

public class User {
    private String UserName;
    private String FullName;
    private String userId;
    private String Email;
    private String address;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User(String phone, String address, String email, String userId, String fullName, String userName) {
        Phone = phone;
        this.address = address;
        Email = email;
        this.userId = userId;
        FullName = fullName;
        UserName = userName;
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
