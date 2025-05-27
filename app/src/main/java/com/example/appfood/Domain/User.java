package com.example.appfood.Domain;

public class User {
    private String userName;
    private String fullName;
    private String userId;
    private String email;
    private String phone;
    private String address;
    private String avatar;

    public User() {
        // Firebase requires empty constructor
    }

    public User(String userName, String fullName, String userId, String email, String phone) {
        this.userName = userName;
        this.fullName = fullName;
        this.userId = userId;
        this.email = email;
        this.phone = phone;
    }

    // Getter & Setter
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
