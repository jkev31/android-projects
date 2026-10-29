package com.example.latihan07;

import com.google.firebase.firestore.DocumentId;

public class Product {

    @DocumentId
    private String id;
    private String name;
    private double price;
    private long stock;
    private String imageBase64; // [DIUBAH] Menyimpan teks Base64 gambar

    public Product() {}

    public Product(String name, double price, long stock, String imageBase64) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imageBase64 = imageBase64;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public long getStock() { return stock; }
    public void setStock(long stock) { this.stock = stock; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
}