package com.example.latihan02;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;

// [+] BARU: Import library Firestore
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

// [DIHAPUS]: Import Realtime Database
// import com.google.firebase.database.DatabaseReference;
// import com.google.firebase.database.FirebaseDatabase;
// import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password minimal 6 karakter!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Logika Register Firebase
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            if (mAuth.getCurrentUser() != null) {
                                String uid = mAuth.getCurrentUser().getUid();

                                // [+] BARU: Inisialisasi instance Firestore
                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                Map<String, Object> profileData = new HashMap<>();
                                profileData.put("email", email);

                                // [DIUBAH]: Menggunakan FieldValue.serverTimestamp() dari Firestore
                                profileData.put("createdAt", FieldValue.serverTimestamp());

                                // [DIUBAH]: Simpan data profil ke dokumen /users/{UID} di Firestore
                                db.collection("users").document(uid)
                                        .set(profileData, SetOptions.merge());
                            }

                            Toast.makeText(RegisterActivity.this, "Registrasi Berhasil! Silakan Login", Toast.LENGTH_LONG).show();
                            // Tutup RegisterActivity dan kembali ke LoginActivity
                            finish();
                        } else {
                            // Jika akun sudah ada atau format email salah, error akan ditampilkan di sini
                            Toast.makeText(RegisterActivity.this, "Registrasi Gagal: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}