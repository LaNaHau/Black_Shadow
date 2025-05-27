package com.example.appfood.Domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private String userId;
    private String userName;
    private String phone;
    private String address;
    private List<Foods> foodList;
    private double itemTotal;
    private double tax;
    private double deliveryFee;
    private double totalAmount;
    private long orderTime;  // timestamp
    private String status;
    private long paymentConfirmTime;
    private double finalTotal;

    public Order() {}
    public Order(String orderId, String userId, String userName, String phone, String address,
                 ArrayList<Foods> foodList, double itemTotal, double tax, double deliveryFee, double totalAmount, long orderTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.phone = phone;
        this.address = address;
        this.foodList = foodList;
        this.itemTotal = itemTotal;
        this.tax = tax;
        this.deliveryFee = deliveryFee;
        this.totalAmount = totalAmount;
        this.orderTime = orderTime;
    }
    public Order(String orderId, String userId, String userName, String phone, String address,
                 List<Foods> foodList, double itemTotal, double tax, double deliveryFee,
                 double totalAmount, long orderTime, String status, long paymentConfirmTime, double finalTotal) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.phone = phone;
        this.address = address;
        this.foodList = foodList;
        this.itemTotal = itemTotal;
        this.tax = tax;
        this.deliveryFee = deliveryFee;
        this.totalAmount = totalAmount;
        this.orderTime = orderTime;
        this.status = status;
        this.paymentConfirmTime = paymentConfirmTime;
        this.finalTotal = finalTotal;
    }



    /**
     * Tính lại tổng tiền sau thuế và phí giao hàng
     */
    public void recalculateFinalTotal() {
        this.finalTotal = this.itemTotal + this.tax + this.deliveryFee;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public List<Foods> getFoodList() {
        return foodList;
    }

    public void setFoodList(List<Foods> foodList) {
        this.foodList = foodList;
    }

    public double getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(double itemTotal) {
        this.itemTotal = itemTotal;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getPaymentConfirmTime() {
        return paymentConfirmTime;
    }

    public void setPaymentConfirmTime(long paymentConfirmTime) {
        this.paymentConfirmTime = paymentConfirmTime;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(double finalTotal) {
        this.finalTotal = finalTotal;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", foodList=" + foodList +
                ", itemTotal=" + itemTotal +
                ", tax=" + tax +
                ", deliveryFee=" + deliveryFee +
                ", totalAmount=" + totalAmount +
                ", orderTime=" + orderTime +
                ", status='" + status + '\'' +
                ", paymentConfirmTime=" + paymentConfirmTime +
                ", finalTotal=" + finalTotal +
                '}';
    }
}
