package com.example.latihan04;



import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "channel_firestore_local";
    private static final String CHANNEL_NAME = "Notifikasi Firestore";

    private FirebaseFirestore db;
    public static CollectionReference itemsRef;

    private Button btnSave;
    private EditText etKode, etNama, etAlamat, etTelp, etUmur, etPendidikan;
    private RecyclerView recyclerView;

    private List<Item> itemList = new ArrayList<>();
    private ItemAdapter adapter;

    private boolean isFirstLoad = true;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, "Izin notifikasi ditolak!", Toast.LENGTH_SHORT).show();
                }
            });

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

        createNotificationChannel();
        checkNotificationPermission();

        adapter = new ItemAdapter(itemList, itemsRef);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnSave.setOnClickListener(v -> prosesSimpanItem());

        listenRealtimeChanges();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Kanal Notifikasi Perubahan Data Firestore");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void showLocalNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void prosesSimpanItem() {
        String kode = etKode.getText().toString().trim();
        String nama = etNama.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String telp = etTelp.getText().toString().trim();
        String strUmur = etUmur.getText().toString().trim();
        String pendidikan = etPendidikan.getText().toString().trim();

        if (kode.isEmpty() || strUmur.isEmpty()) {
            Toast.makeText(this, "Kode dan Umur wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        int umur;
        try {
            umur = Integer.parseInt(strUmur);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Umur harus angka valid", Toast.LENGTH_SHORT).show();
            return;
        }

        Item item = new Item(kode, nama, alamat, telp, umur, pendidikan);

        itemsRef.document(kode).set(item)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "Item berhasil disimpan", Toast.LENGTH_SHORT).show();
                    etKode.setText("");
                    etNama.setText("");
                    etAlamat.setText("");
                    etTelp.setText("");
                    etUmur.setText("");
                    etPendidikan.setText("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Gagal simpan: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void listenRealtimeChanges() {
        itemsRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                return;
            }

            if (snapshot != null) {
                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    Item item = dc.getDocument().toObject(Item.class);
                    switch (dc.getType()) {
                        case ADDED:
                            if (!isFirstLoad) {
                                showLocalNotification(
                                        "Data Baru Ditambahkan",
                                        "Item " + item.getKode() + " baru saja dibuat."
                                );
                            }
                            break;
                        case MODIFIED:
                            showLocalNotification(
                                    "Data Diperbarui",
                                    "Item " + item.getKode() + " telah diubah."
                            );
                            break;
                        case REMOVED:
                            showLocalNotification(
                                    "Data Dihapus",
                                    "Item " + item.getKode() + " telah dihapus."
                            );
                            break;

                    }
                }

                itemList.clear();
                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                    Item item = doc.toObject(Item.class);
                    if (item != null) {
                        itemList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();

                isFirstLoad = false;
            }
        });
    }
}