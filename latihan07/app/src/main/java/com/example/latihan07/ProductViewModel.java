package com.example.latihan07;

import android.content.Context;
import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class ProductViewModel extends ViewModel {

    private final ProductRepository repository;
    private final LiveData<List<Product>> productsLiveData;

    public ProductViewModel() {
        repository = new ProductRepository();
        productsLiveData = repository.getProductsLiveData();
    }

    public LiveData<List<Product>> getProducts() {
        return productsLiveData;
    }

    public void buyProduct(String productId, int quantity, ProductRepository.TransactionCallback callback) {
        repository.buyProduct(productId, quantity, callback);
    }

    public void addProduct(Context context, String name, double price, long stock, Uri imageUri, ProductRepository.TransactionCallback callback) {
        repository.addProductWithBase64(context, name, price, stock, imageUri, callback);
    }
}