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

// [+] BARU: Import library Firestore
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

// [DIHAPUS]: Import Realtime Database
// import com.google.firebase.database.DataSnapshot;
// import com.google.firebase.database.DatabaseError;
// import com.google.firebase.database.DatabaseReference;
// import com.google.firebase.database.FirebaseDatabase;
// import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    FirebaseAuth mAuth;


    FirebaseFirestore db;
    DocumentReference userRef;
    CollectionReference itemsRef;

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


        if (user == null) {
            keHalamanLogin();
            return;
        }


        db = FirebaseFirestore.getInstance();


        String uid = user.getUid();
        userRef = db.collection("users").document(uid);
        itemsRef = userRef.collection("items");


        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("email", user.getEmail());
        userRef.set(userProfile, SetOptions.merge());

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

        // Pass CollectionReference itemsRef ke ItemAdapter
        adapter = new ItemAdapter(itemList, itemsRef);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        btnSave.setOnClickListener(v -> {
            user.reload().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    mAuth.signOut();
                    Toast.makeText(MainActivity.this, "Akun Anda sudah tidak terdaftar!", Toast.LENGTH_LONG).show();
                    keHalamanLogin();
                    return;
                }



                prosesSimpanItem();
            });
        });


        itemsRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                android.util.Log.e("DEBUG_FIRESTORE", "Error Firestore: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Gagal muat data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshot != null) {
                itemList.clear();
                android.util.Log.d("DEBUG_FIRESTORE", "Jumlah dokumen ditemukan: " + snapshot.size());
                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                    Item item = doc.toObject(Item.class);
                    if (item != null) {
                        android.util.Log.d("DEBUG_FIRESTORE", "Berhasil muat item: " + item.getNama());
                        itemList.add(item);
                    } else {
                        android.util.Log.e("DEBUG_FIRESTORE", "Gagal konversi dokumen ID: " + doc.getId());
                    }
                }
                adapter.notifyDataSetChanged();
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

        // [DIUBAH]: Simpan item ke Firestore dokumen /users/{UID}/items/{kode}
        itemsRef.document(kode).set(item)
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