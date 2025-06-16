package com.example.appfood.Domain;

public class OrderItem {
    public String itemId;
    public String name;
    public int price;
    public int quantity;

    public OrderItem() {}

    public OrderItem(String itemId, String name, int price, int quantity) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}
