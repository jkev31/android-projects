package com.example.latihan07;

import com.google.firebase.firestore.DocumentId;

public class Product {

    @DocumentId
    private String id;
    private String name;
    private double price;
    private long stock;
    private String imageUrl;

    public Product() {}

    public Product(String name, double price, long stock, String imageUrl) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public long getStock() { return stock; }
    public String getImageUrl() { return imageUrl; }
}