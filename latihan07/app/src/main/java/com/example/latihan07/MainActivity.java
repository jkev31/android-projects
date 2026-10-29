package com.example.latihan07;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.latihan07.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ProductViewModel viewModel;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        setupRecyclerView();

        viewModel.getProducts().observe(this, products -> adapter.setProductList(products));

        binding.fabAddProduct.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddProductActivity.class))
        );
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(product -> {
            viewModel.buyProduct(product.getId(), 1, new ProductRepository.TransactionCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(MainActivity.this, "Gagal: " + error, Toast.LENGTH_LONG).show();
                }
            });
        });

        binding.rvProducts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvProducts.setAdapter(adapter);
    }
}