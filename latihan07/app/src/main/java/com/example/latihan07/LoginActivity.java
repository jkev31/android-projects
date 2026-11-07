package com.example.latihan07;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.latihan07.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {
            navigateToMain();
        }

        binding.btnLogin.setOnClickListener(v -> handleAuth(true));
        binding.btnRegister.setOnClickListener(v -> handleAuth(false));
    }

    private void handleAuth(boolean isLogin) {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan password wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        if (isLogin) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> createOrUpdateUserDoc(email))
                    .addOnFailureListener(e -> {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Login gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> createOrUpdateUserDoc(email))
                    .addOnFailureListener(e -> {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Registrasi gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Membuat / memperbarui dokumen /users/{uid} di Firestore
    private void createOrUpdateUserDoc(String email) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);

        db.collection("users").document(user.getUid())
                .set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> navigateToMain())
                .addOnFailureListener(e -> navigateToMain());
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}