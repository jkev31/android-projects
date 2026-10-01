package com.example.latihan02;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference userRef, itemsRef;
    Button btnLogout, btnSave;
    EditText etKode, etNama, etSatuan, etHarga;
    RecyclerView recyclerView;
    List<Item> itemList = new ArrayList<>();
    ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // 1. Cek apakah user sedang login
        if (user == null) {
            keHalamanLogin();
            return;
        }

        String databaseUrl = "https://latihan01-70d0f-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);

        // 2. PATH DATA PER USER: /users/{UID}/items
        String uid = user.getUid();
        userRef = database.getReference().child("users").child(uid);
        itemsRef = userRef.child("items");

        // 3. BUAT / UPDATE PROFIL OTOMATIS (Jika belum dibuat saat registrasi)
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("email", user.getEmail());
        userRef.child("profile").updateChildren(userProfile);

        // Inisialisasi View
        btnLogout = findViewById(R.id.btnLogout);
        etKode = findViewById(R.id.etKode);
        etNama = findViewById(R.id.etNama);
        etSatuan = findViewById(R.id.etSatuan);
        etHarga = findViewById(R.id.etHarga);
        btnSave = findViewById(R.id.btnSave);
        recyclerView = findViewById(R.id.recyclerView);

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            keHalamanLogin();
        });

        // Pass itemsRef ke adapter
        adapter = new ItemAdapter(itemList, itemsRef);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 4. SIMPAN DATA (Dengan Proteksi Reload Cek Akun Terhapus)
        btnSave.setOnClickListener(v -> {
            // Cek status keaktifan akun dari Firebase Server
            user.reload().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    // Jika akun sudah dihapus di Firebase Auth Console
                    mAuth.signOut();
                    Toast.makeText(MainActivity.this, "Akun Anda sudah tidak terdaftar!", Toast.LENGTH_LONG).show();
                    keHalamanLogin();
                    return;
                }

                // Jika akun valid, proses simpan data
                prosesSimpanItem();
            });
        });

        // 5. READ DATA (Khusus punya user ini saja)
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Item item = data.getValue(Item.class);
                    if (item != null) {
                        itemList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this, "Gagal muat data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void prosesSimpanItem() {
        String kode = etKode.getText().toString().trim();
        String nama = etNama.getText().toString().trim();
        String satuan = etSatuan.getText().toString().trim();
        String strHarga = etHarga.getText().toString().trim();

        if (kode.isEmpty()) {
            Toast.makeText(MainActivity.this, "Kode harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (strHarga.isEmpty()) {
            Toast.makeText(MainActivity.this, "Harga harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        int harga = Integer.parseInt(strHarga);
        if (harga <= 0) {
            Toast.makeText(MainActivity.this, "Harga tidak boleh 0", Toast.LENGTH_SHORT).show();
            return;
        }

        Item item = new Item(kode, nama, satuan, harga);

        // Simpan ke /users/{UID}/items/{kode}
        itemsRef.child(kode).setValue(item)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "Item berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    etKode.setText("");
                    etNama.setText("");
                    etSatuan.setText("");
                    etHarga.setText("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void keHalamanLogin() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}