package com.example.latihan07;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.latihan07.databinding.ActivityHistoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ActivityHistoryBinding binding;
    private HistoryAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        setupRecyclerView();
        loadOrderHistory();
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter();
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvHistory.setAdapter(adapter);
    }

    private void loadOrderHistory() {
        String currentUserId = mAuth.getUid();
        if (currentUserId == null) {
            Toast.makeText(this, "Pengguna belum login!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query Firestore (tanpa .orderBy() agar tidak memicu error FAILED_PRECONDITION)
        db.collection("orders")
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(HistoryActivity.this, "Gagal memuat riwayat: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        List<Order> orders = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            Order order = doc.toObject(Order.class);
                            order.setId(doc.getId());
                            orders.add(order);
                        }

                        // Mengurutkan riwayat secara lokal di Java (terbaru di atas)
                        Collections.sort(orders, (o1, o2) -> {
                            if (o1.getTimestamp() == null || o2.getTimestamp() == null) return 0;
                            return o2.getTimestamp().compareTo(o1.getTimestamp());
                        });

                        adapter.setOrderList(orders);
                    }
                });
    }
}