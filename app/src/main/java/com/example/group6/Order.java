package com.example.group6;

public class Order {
    private int id;
    private int userId;
    private String orderDate;
    private String status;

    public Order(int id, int userId, String orderDate, String status) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
    }
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
}
