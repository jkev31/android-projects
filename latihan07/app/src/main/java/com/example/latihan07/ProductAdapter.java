package com.example.latihan07;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
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

        Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .into(holder.binding.imgProduct);

        holder.binding.btnBuy.setOnClickListener(v -> buyClickListener.onBuyClick(product));
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