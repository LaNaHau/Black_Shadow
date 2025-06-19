package com.example.appfood.Domain;

import java.io.Serializable;

public class Voucher implements Serializable {
    private String code;

    private String discountType;
    private double value;
    private long expiredAt;
    private String name;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Voucher(String code, String discountType, double value, long expiredAt, String name, String type) {
        this.code = code;
        this.discountType = discountType;
        this.value = value;
        this.expiredAt = expiredAt;
        this.name = name;
        this.type = type;
    }


    public String getCode() {


        
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(long expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Voucher() {
    }
}
