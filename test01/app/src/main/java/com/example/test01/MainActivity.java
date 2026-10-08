package com.example.test01;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    static CollectionReference itemsRef;

    Button btnSave;
    EditText etKode, etNama, etAlamat, etTelp, etUmur, etPendidikan;

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

        db = FirebaseFirestore.getInstance();

        itemsRef = db.collection("items");

        etKode = findViewById(R.id.etKode);
        etNama = findViewById(R.id.etNama);
        etAlamat = findViewById(R.id.etAlamat);
        etTelp = findViewById(R.id.etTelp);
        etUmur = findViewById(R.id.etUmur);
        etPendidikan = findViewById(R.id.etPendidikan);
        btnSave = findViewById(R.id.btnSave);
        recyclerView = findViewById(R.id.recyclerView);

        adapter = new ItemAdapter(itemList, itemsRef);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnSave.setOnClickListener(v -> {
            String kode = etKode.getText().toString().trim();
            String nama = etNama.getText().toString().trim();
            String alamat = etAlamat.getText().toString().trim();
            String telp = etTelp.getText().toString().trim();
            String strUmur = etUmur.getText().toString().trim();
            String pendidikan = etPendidikan.getText().toString().trim();

            if (kode.isEmpty()) {
                Toast.makeText(MainActivity.this, "Kode harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (strUmur.isEmpty()) {
                Toast.makeText(MainActivity.this, "Umur harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            int umur = Integer.parseInt(strUmur.trim());
            if (umur <= 0) {
                Toast.makeText(MainActivity.this, "Umur tidak boleh 0", Toast.LENGTH_SHORT).show();
                return;
            }

            Item item = new Item(kode, nama, alamat, telp, umur, pendidikan);

            itemsRef.document(kode).set(item)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MainActivity.this, "Item berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        etKode.setText("");
                        etNama.setText("");
                        etAlamat.setText("");
                        etTelp.setText("");
                        etUmur.setText("");
                        etPendidikan.setText("");
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(MainActivity.this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
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
                    Item item = doc.toObject(Item.class); // Konversi dokumen ke objek Item, perlu public item() {} di class Item
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
}