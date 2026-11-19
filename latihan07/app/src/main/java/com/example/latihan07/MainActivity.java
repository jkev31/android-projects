package com.example.latihan07;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.latihan07.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ProductViewModel viewModel;
    private ProductAdapter adapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 1. Cek sesi login di cache HP
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            keHalamanLogin();
            return;
        }

        // 2. CEK FIREBASE AUTH CONSOLE: Reload data dari server Auth saat relaunch
        user.reload().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                // Dipemicu jika akun dihapus di tab Authentication -> Users pada Firebase Console
                mAuth.signOut();
                Toast.makeText(MainActivity.this, "Akun Anda tidak lagi terdaftar di Authentication!", Toast.LENGTH_LONG).show();
                keHalamanLogin();
            }
        });

        // 3. CEK FIRESTORE DATABASE: Real-time listener memantau dokumen /users/{uid}
        String uid = user.getUid();
        DocumentReference userRef = db.collection("users").document(uid);
        userRef.addSnapshotListener(this, (snapshot, error) -> {
            if (error != null) return;

            // Dipemicu jika dokumen pengguna di Firestore Database dihapus
            if (snapshot != null && !snapshot.exists()) {
                mAuth.signOut();
                Toast.makeText(MainActivity.this, "Data dokumen pengguna telah dihapus dari Firestore!", Toast.LENGTH_LONG).show();
                keHalamanLogin();
            }
        });

        // 4. Inisialisasi ViewModel & RecyclerView Katalog Produk
        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        setupRecyclerView();

        viewModel.getProducts().observe(this, products -> {
            if (products != null) {
                adapter.setProductList(products);
            }
        });

        binding.fabAddProduct.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddProductActivity.class))
        );

        binding.toolbar.inflateMenu(R.menu.main_menu);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_history) {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                return true;
            }
            return false;
        });
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

    private void keHalamanLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}