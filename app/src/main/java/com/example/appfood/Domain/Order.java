package com.example.appfood.Domain;

import java.util.List;

public class Order {
    public String orderId;
    public String userId;
    public String name;
    public String phone;
    public String address;
    public List<OrderItem> items;
    public int total;
    public int discount;
    public int finalTotal;
    public String voucherCode;
    public String orderTime;
    public String status;

    public Order() {}

    public Order(String orderId, String userId, String name, String phone, String address,
                 List<OrderItem> items, int total, int discount, int finalTotal,
                 String voucherCode, String orderTime, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.items = items;
        this.total = total;
        this.discount = discount;
        this.finalTotal = finalTotal;
        this.voucherCode = voucherCode;
        this.orderTime = orderTime;
        this.status = status;
    }
}

