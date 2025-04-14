package com.example.appfood.Domain;

public class User {
    private String UserName;
    private String FullName;
    private String Password;
    private String Email;

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

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public User() {
    }

    public User(String userName, String fullName, String password, String email) {
        UserName = userName;
        FullName = fullName;
        Password = password;
        Email = email;
    }
}
