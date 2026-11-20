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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ApiServices apiServices = ApiClient.getApiServices();

    Button btnSave;
    EditText etNama, etAlamat, etTelp, etUmur, etPendidikan;

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

        etNama = findViewById(R.id.etNama);
        etAlamat = findViewById(R.id.etAlamat);
        etTelp = findViewById(R.id.etTelp);
        etUmur = findViewById(R.id.etUmur);
        etPendidikan = findViewById(R.id.etPendidikan);
        btnSave = findViewById(R.id.btnSave);
        recyclerView = findViewById(R.id.recyclerView);

        adapter = new ItemAdapter(itemList, apiServices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadData(); // Panggil fungsi muat data

        btnSave.setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            String alamat = etAlamat.getText().toString().trim();
            String telp = etTelp.getText().toString().trim();
            String strUmur = etUmur.getText().toString().trim();
            String pendidikan = etPendidikan.getText().toString().trim();

            if (nama.isEmpty() || telp.isEmpty()) {
                Toast.makeText(MainActivity.this, "Nama dan telp harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (strUmur.isEmpty()) {
                Toast.makeText(MainActivity.this, "Umur harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            int umur = Integer.parseInt(strUmur);
            if (umur <= 0) {
                Toast.makeText(MainActivity.this, "Umur tidak boleh 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Alur Retrofit untuk Create (id dibuat otomatis oleh Laravel)
            apiServices.createKaryawan(nama, alamat, telp, umur, pendidikan).enqueue(new Callback<Item>() {
                @Override
                public void onResponse(Call<Item> call, Response<Item> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Item berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        etNama.setText("");
                        etAlamat.setText("");
                        etTelp.setText("");
                        etUmur.setText("");
                        etPendidikan.setText("");
                        loadData(); // Refresh list
                    } else {
                        Toast.makeText(MainActivity.this, "Gagal: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Item> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadData() {
        apiServices.getAllKaryawan().enqueue(new Callback<KaryawanResponse>() {
            @Override
            public void onResponse(Call<KaryawanResponse> call, Response<KaryawanResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Data karyawan ada di dalam objek pembungkus "data"
                    List<Item> dataDariApi = response.body().getData();
                    if (dataDariApi != null) {
                        itemList.clear();
                        itemList.addAll(dataDariApi);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Gagal ambil data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KaryawanResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}