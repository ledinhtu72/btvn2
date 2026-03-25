package com.example.group6;

public class Product {
    private int id;
    private String name;
    private double price;
    private int categoryId;
    private String description;
    public Product(int id, String name, double price, int categoryId, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
        this.description = description;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getCategoryId() { return categoryId; }
    public String getDescription() { return description; }
}
