package com.example.latihan01;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    DatabaseReference dbRef;

    EditText etKode, etNama, etSatuan, etHarga;

    Button btnSave;

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
        String databaseUrl = "https://latihan01-70d0f-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference dbRef =database.getReference().child("items");


        etKode = findViewById(R.id.etKode) ;

        etNama = findViewById(R.id.etNama) ;
        etSatuan = findViewById(R.id.etSatuan) ;
        etHarga = findViewById(R.id.etHarga) ;
        btnSave = findViewById(R.id.btnSave) ;

        recyclerView = findViewById(R.id. recyclerView) ;
        adapter = new ItemAdapter(itemList,dbRef);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnSave.setOnClickListener(v -> {
            String kode = etKode.getText().toString();
            String nama = etNama.getText().toString();
            String satuan = etSatuan.getText().toString();
            int harga = Integer.parseInt(etHarga.getText().toString());


            if (harga <= 0) {
                Toast.makeText(MainActivity.this, "Harga tidak boleh 0", Toast.LENGTH_SHORT).show();
                return;
            }

            Item item = new Item(kode, nama, satuan, harga);

            // Simpan ke Firebase dengan key = kode
            dbRef.child(kode).setValue(item)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(MainActivity.this, "Item berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(MainActivity.this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        // Read data dari Firebase
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Item item = data.getValue(Item.class);
                    itemList.add(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });



    }



}