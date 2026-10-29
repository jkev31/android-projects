package com.example.latihan07;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.latihan07.databinding.ItemProductBinding;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList = new ArrayList<>();
    private final OnBuyClickListener buyClickListener;

    public interface OnBuyClickListener {
        void onBuyClick(Product product);
    }

    public ProductAdapter(OnBuyClickListener buyClickListener) {
        this.buyClickListener = buyClickListener;
    }

    public void setProductList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.binding.tvProductName.setText(product.getName());
        holder.binding.tvProductPrice.setText("Rp " + (long) product.getPrice());
        holder.binding.tvProductStock.setText("Stok: " + product.getStock());

        // [DIUBAH] Decode string Base64 ke Bitmap
        Bitmap bitmap = base64ToBitmap(product.getImageBase64());
        if (bitmap != null) {
            holder.binding.imgProduct.setImageBitmap(bitmap);
        } else {
            holder.binding.imgProduct.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.binding.btnBuy.setOnClickListener(v -> buyClickListener.onBuyClick(product));
    }

    // Function pendukung decode Base64 ke Bitmap
    private Bitmap base64ToBitmap(String base64String) {
        if (base64String == null || base64String.isEmpty()) return null;
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        final ItemProductBinding binding;

        ProductViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}