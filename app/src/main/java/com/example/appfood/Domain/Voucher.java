package com.example.appfood.Domain;

import java.io.Serializable;

public class Voucher implements Serializable {
    private String code;

    private String discountType;
    private double value;
    private long expiredAt;
    private boolean used;

    @Override
    public String toString() {
        return "Voucher{" +
                "code='" + code + '\'' +
                ", discountType='" + discountType + '\'' +
                ", value=" + value +
                ", expiredAt=" + expiredAt +
                ", used=" + used +
                '}';
    }

    public Voucher(String code, String discountType, double value, long expiredAt, boolean used) {
        this.code = code;
        this.discountType = discountType;
        this.value = value;
        this.expiredAt = expiredAt;
        this.used = used;
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

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public Voucher() {
    }
}
