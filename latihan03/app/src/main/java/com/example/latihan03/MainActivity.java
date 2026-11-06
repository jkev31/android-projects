package com.example.latihan03;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ImageView ivPreview;
    private Button btnSelectFile, btnSave, btnDelete;
    private TextView tvInfo;

    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> galleryLauncher;

    private FirebaseFirestore db;
    private DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Inisialisasi Firebase & Firestore
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Dokumen target untuk menyimpan string Base64 gambar
        docRef = db.collection("latihan03_images").document("data_gambar");

        // 2. Inisialisasi UI
        ivPreview = findViewById(R.id.ivPreview);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        tvInfo = findViewById(R.id.tvInfo);

        // 3. Register ActivityResultLauncher Galeri
        setupGalleryLauncher();

        // 4. Event Listener
        btnSelectFile.setOnClickListener(v -> openGallery());
        btnSave.setOnClickListener(v -> simpanBase64KeFirestore());
        btnDelete.setOnClickListener(v -> hapusGambarDariFirestore());

        // 5. Read Realtime Listener Firestore
        listenRealtimeFirestore();
    }

    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            ivPreview.setImageURI(selectedImageUri);
                            btnSave.setEnabled(true);
                            tvInfo.setText("Berkas dipilih: " + selectedImageUri.getLastPathSegment());
                        }
                    }
                }
        );
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    // ENCODE: Convert Uri Gambar ke String Base64
    private String convertUriToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) inputStream.close();

            if (bitmap == null) return null;

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // Kompresi ke format JPEG 70% agar ukuran String < 1 MB (Batas Firestore)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // DECODE: Convert String Base64 ke Bitmap
    private Bitmap base64ToBitmap(String base64String) {
        if (base64String == null || base64String.isEmpty()) return null;
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void simpanBase64KeFirestore() {
        if (selectedImageUri == null) return;

        btnSave.setEnabled(false);
        tvInfo.setText("Mengonversi gambar ke Base64...");

        String base64Image = convertUriToBase64(selectedImageUri);
        if (base64Image == null) {
            Toast.makeText(this, "Gagal mengonversi gambar!", Toast.LENGTH_SHORT).show();
            btnSave.setEnabled(true);
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("imageBase64", base64Image);
        data.put("updatedAt", FieldValue.serverTimestamp());

        docRef.set(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "Berhasil disimpan ke Firestore!", Toast.LENGTH_SHORT).show();
                    tvInfo.setText("Data gambar berhasil disimpan dalam bentuk Base64 ke Cloud Firestore.");
                })
                .addOnFailureListener(e -> {
                    btnSave.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Gagal simpan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void listenRealtimeFirestore() {
        docRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                tvInfo.setText("Error membaca data: " + error.getMessage());
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                String base64String = snapshot.getString("imageBase64");
                Bitmap bitmap = base64ToBitmap(base64String);

                if (bitmap != null) {
                    ivPreview.setImageBitmap(bitmap);
                    btnDelete.setEnabled(true);
                    tvInfo.setText("Status: Data gambar aktif dimuat dari Firestore (Panjang Base64: " + base64String.length() + " karakter)");
                }
            } else {
                ivPreview.setImageResource(0);
                btnDelete.setEnabled(false);
                tvInfo.setText("Belum ada data gambar di Firestore.");
            }
        });
    }

    private void hapusGambarDariFirestore() {
        docRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "Data gambar berhasil dihapus!", Toast.LENGTH_SHORT).show();
                    selectedImageUri = null;
                    btnSave.setEnabled(false);
                    btnDelete.setEnabled(false);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Gagal hapus: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}