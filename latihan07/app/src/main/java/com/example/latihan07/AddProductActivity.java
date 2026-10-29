package com.example.latihan07;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.latihan07.databinding.ActivityAddProductBinding;

public class AddProductActivity extends AppCompatActivity {

    private ActivityAddProductBinding binding;
    private ProductViewModel viewModel;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    binding.imgPreview.setImageURI(selectedImageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        binding.btnSelectImage.setOnClickListener(v -> openGallery());
        binding.btnSaveProduct.setOnClickListener(v -> saveProduct());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void saveProduct() {
        String name = binding.etName.getText().toString().trim();
        String priceStr = binding.etPrice.getText().toString().trim();
        String stockStr = binding.etStock.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Lengkapi semua data dan pilih gambar!", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.pbUpload.setVisibility(View.VISIBLE);
        binding.btnSaveProduct.setEnabled(false);

        double price = Double.parseDouble(priceStr);
        long stock = Long.parseLong(stockStr);

        try {
            price = Double.parseDouble(priceStr);
            stock = Long.parseLong(stockStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format harga atau stok tidak valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi Tugas Mandiri 2: Harga dan stok tidak boleh <= 0
        if (price <= 0) {
            Toast.makeText(this, "Harga tidak boleh 0 atau bertanda minus!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (stock <= 0) {
            Toast.makeText(this, "Stok tidak boleh 0 atau bertanda minus!", Toast.LENGTH_SHORT).show();
            return;
        }

        // [DIUBAH] Melewatkan Context (this) ke ViewModel
        viewModel.addProduct(AddProductActivity.this, name, price, stock, selectedImageUri, new ProductRepository.TransactionCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(AddProductActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                binding.pbUpload.setVisibility(View.GONE);
                binding.btnSaveProduct.setEnabled(true);
                Toast.makeText(AddProductActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}