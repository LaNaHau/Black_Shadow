package com.example.appfood.Domain;

public class OrderQRCodePayload {
    public String orderId;
    public int finalTotal;
    public long expiryTime; // millis
    public String verificationCode;

    public OrderQRCodePayload(String orderId, int finalTotal, long expiryTime, String verificationCode) {
        this.orderId = orderId;
        this.finalTotal = finalTotal;
        this.expiryTime = expiryTime;
        this.verificationCode = verificationCode;
    }

    public String toQRContent() {
        return "orderId=" + orderId +
                ";finalTotal=" + finalTotal +
                ";expiryTime=" + expiryTime +
                ";verify=" + verificationCode;
    }
}
