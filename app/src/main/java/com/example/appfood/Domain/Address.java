package com.example.appfood.Domain;

import java.io.Serializable;

public class Address implements Serializable {
    private String id;
    private String name;
    private String phone;
    private String fullAddress;
    private boolean isDefault;

    public Address() {} // Firebase cần

    public Address(String id, String name, String phone, String fullAddress, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.fullAddress = fullAddress;
        this.isDefault = isDefault;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public Address(String name, String phone, String fullAddress, boolean isDefault) {
        this.name = name;
        this.phone = phone;
        this.fullAddress = fullAddress;
        this.isDefault = isDefault;
    }
}
