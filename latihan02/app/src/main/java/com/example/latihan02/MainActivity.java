package com.example.latihan02;

import static android.app.ProgressDialog.show;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FirestoreDemo";

    private EditText etProductName, etProductPrice, etProductCategory;
    private Button btnSave, btnSetWithld, btnFetch;
    private TextView tvResult;

    // Inisialisasi Instance Firestore
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // 1. Inisialisasi komponen UI & Firestore
        db = FirebaseFirestore.getInstance();

        etProductName = findViewById(R.id.etProductName);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductCategory = findViewById(R.id.etProductCategory);
        btnSave = findViewById(R.id.btnSave);
        btnSetWithld=findViewById(R.id.btnSetWithld);
        btnFetch = findViewById(R.id.btnFetch);
        tvResult = findViewById(R.id.tvResult);

        // Listener Tombol
        btnSave.setOnClickListener(v -> addDataAutold());
        btnSetWithld.setOnClickListener(v -> setDataCustomld());
        btnFetch.setOnClickListener(v -> queryAndFilterData());
    }

    private void addDataAutold() {
        String name = etProductName.getText().toString();
        double price = Double.parseDouble(etProductPrice.getText().toString());
        String category = etProductCategory.getText().toString();

        Product product = new Product(name, price, category);

        db.collection("products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText
                    (MainActivity.this, "Berhasil! ID: " + documentReference.getId(),
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Gagal menambah dokumen", e);

                });
    }

    private void setDataCustomld() {
        String name = etProductName.getText().toString();
        double price = Double.parseDouble(etProductPrice.getText().toString());
        String category = etProductCategory.getText().toString();

        Map<String, Object> productMap = new HashMap<>();
        productMap.put("name", name);
        productMap.put("price", price);
        productMap.put("category", category);

        db.collection("products").document("PROD-001")
                .set(productMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "Data PROD-001 disimpan",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Gagal Menyimpan",e));
    }

    // --- C. UPDATE : Memperbarui field tertentu tanpa menimpa seluruh dokumen ---
    private void updateSingleField(String docld, double newPrice) {
        db.collection("products").document(docld)
                .update("price", newPrice)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Harga berhasil diperbarui"))
                .addOnFailureListener(e -> Log.e(TAG, "Gagal update", e));

    }

    // --- D. DELETE : Menghapus dokumen atau field tertentu ---
    private void deleteDocument(String docld) {
    // Menghapus seluruh dokumen
        db.collection("products").document(docld)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Dokumen terhapus"));

    // Contoh Menghapus Field 'category' saja dari dokumen:
    /*
    db.collection("products").document(docld)
    .update("category", FieldValue.delete());

    */

    }

    // --- E. READ, FILTERING & SORTING DATA ---
    private void queryAndFilterData() {

        // Kueri: Ambil kategori 'elektronik', urutkan harga dari termurah, maksimal 5 data
        db.collection("products")
                .orderBy("price", Query.Direction.ASCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        StringBuilder builder = new StringBuilder();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            product.setId(document.getId());

                            builder.append("ID: ").append(product.getId())
                                    .append("\nNama: ").append(product.getName())
                                    .append("\nHarga: Rp").append(product.getPrice())
                                    .append("\n-------------------\n");
                        }
                        tvResult.setText(builder.toString());
                    } else {
                        Log.w(TAG, "Error mendapatkan dokumen.", task.getException());
                    }
                });
    }


}