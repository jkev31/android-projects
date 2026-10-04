package com.example.latihan03;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LatihanCloudStorage";

    // Deklarasi variabel untuk elemen UI
    private ImageView ivPreview;
    private ProgressBar progressBar;
    private TextView tvProgress, tvInfo;
    private Button btnSelectFile, btnUpload, btnGetMetadata, btnDelete;

    // Deklarasi variabel untuk Firebase Storage
    private FirebaseStorage storage;
    private StorageReference storageRef; //storageRef = root folder
    private StorageReference uploadedFileRef; //uploadedFileRef = menyimpan referensi image

    private Uri selectedImageUri; //menyimpan address local dari gambar yang dipilih
    private ActivityResultLauncher<Intent> galleryLauncher; //menangani callback hasil pemilihan gambar dari aplikasi galeri secara asynchronous

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inisialisasi Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // inisialisasi elemen UI
        ivPreview = findViewById(R.id.ivPreview);
        progressBar = findViewById(R.id.progressBar);
        tvProgress = findViewById(R.id.tvProgress);
        tvInfo = findViewById(R.id.tvInfo);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnUpload = findViewById(R.id.btnUpload);
        btnGetMetadata = findViewById(R.id.btnGetMetadata);
        btnDelete = findViewById(R.id.btnDelete);

        // inisialisasi launcher untuk memilih gambar dari galeri
        setupGalleryLauncher();

        // event listener button
        btnSelectFile.setOnClickListener(v -> openGallery());
        btnUpload.setOnClickListener(v -> uploadFileToStorage());
        btnGetMetadata.setOnClickListener(v -> fetchFileMetadata());
        btnDelete.setOnClickListener(v -> deleteFileFromStorage());
    }



    //Alur method ini:
    //pengguna pilih gambar -> result OK -> method mengambil Uri ->
    // ditampilkan sbg preview pada ivPreview -> mengaktifkan btnUpload -> update teks pada tvInfo
    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            // Tampilkan preview lokal
                            ivPreview.setImageURI(selectedImageUri);
                            btnUpload.setEnabled(true);
                            tvInfo.setText("Berkas dipilih: " + selectedImageUri.getLastPathSegment());
                        }
                    }
                }
        );
    }


    //Alur method ini:
    //membuat intent dengan aksi action_pick dan tipenya image/
    //dilaunch menggunakan galleryLauncher.launch(intent)
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }



    //Alur method ini:
    //menampilkan ProgressBar & tvProgress ke screen -> generate nama file unik berbasis UUID dalam subfolder image/
    //menyisipkan custom metadata berisi "uploaded_by" dengan nilai "Android User"
    //memulai upload dengan method UploadTask uploadTask = uploadedFileRef.putFile(selectedImageUri, metadata)
    //menghitung persentase progres realtime dengan addOnProgressListener
    //jika upload berhasil, panggil fetchDownloadUrl() untuk mendapatkan URL download
    private void uploadFileToStorage() {
        if (selectedImageUri == null) return;

        // Tampilkan Progress Bar
        progressBar.setVisibility(View.VISIBLE);
        tvProgress.setVisibility(View.VISIBLE);
        btnUpload.setEnabled(false);

        // Buat nama berkas unik dalam folder "images/"
        String fileName = "images/" + UUID.randomUUID().toString() + ".jpg";
        uploadedFileRef = storageRef.child(fileName);

        // Buat Metadata Tambahan
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .setCustomMetadata("uploaded_by", "Android User")
                .build();

        // Mulai Upload File
        UploadTask uploadTask = uploadedFileRef.putFile(selectedImageUri, metadata);

        // Pantau Progres Upload
        uploadTask.addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            int currentProgress = (int) progress;
            progressBar.setProgress(currentProgress);
            tvProgress.setText("Progress: " + currentProgress + "%");
        }).addOnSuccessListener(taskSnapshot -> {
            // Upload Sukses
            progressBar.setVisibility(View.GONE);
            tvProgress.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Unggah Berhasil!", Toast.LENGTH_SHORT).show();

            // Ambil Download URL
            fetchDownloadUrl();

            // Aktifkan tombol metadata dan delete
            btnGetMetadata.setEnabled(true);
            btnDelete.setEnabled(true);
        }).addOnFailureListener(e -> {
            // Upload Gagal
            progressBar.setVisibility(View.GONE);
            tvProgress.setVisibility(View.GONE);
            btnUpload.setEnabled(true);
            Log.e(TAG, "Gagal mengunggah berkas", e);
            Toast.makeText(MainActivity.this, "Gagal Unggah: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }




    //Alur method ini:
    //memanggil download URL -> menampilkan URL address ke tvInfo -> memuat gambar ke ImageView menggunakan Glide
    private void fetchDownloadUrl() {
        if (uploadedFileRef == null) return;

        uploadedFileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();
            tvInfo.setText("Download URL:\n" + downloadUrl);

            // Tampilkan gambar dari Cloud Storage menggunakan Glide
            Glide.with(MainActivity.this)
                    .load(downloadUrl)
                    .centerCrop()
                    .into(ivPreview);
        }).addOnFailureListener(e -> Log.e(TAG, "Gagal mendapatkan URL download", e));
    }




    //Alur method ini:
    //memanggil metadata -> mengambil properti berkas -> menampilkan informasi berkas ke tvInfo
    private void fetchFileMetadata() {
        if (uploadedFileRef == null) return;

        uploadedFileRef.getMetadata().addOnSuccessListener(metadata -> {
            String info = "=== METADATA BERKAS ===" +
                    "\nNama File: " + metadata.getName() +
                    "\nUkuran: " + (metadata.getSizeBytes() / 1024) + " KB" +
                    "\nType: " + metadata.getContentType() +
                    "\nDibuat: " + metadata.getCreationTimeMillis() +
                    "\nUploader: " + metadata.getCustomMetadata("uploaded_by");
            tvInfo.setText(info);
        }).addOnFailureListener(e -> Toast.makeText(this, "Gagal membaca metadata", Toast.LENGTH_SHORT).show());
    }



    //Alur method ini:
    //memanggil fungsi delete -> jika berhasil, menampilkan pesan sukses -> reset UI State
    private void deleteFileFromStorage() {
        if (uploadedFileRef == null) return;

        uploadedFileRef.delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(MainActivity.this, "Berkas di Cloud Storage terhapus!", Toast.LENGTH_SHORT).show();

            // Reset UI State
            ivPreview.setImageResource(0);
            tvInfo.setText("Berkas telah dihapus dari cloud.");
            btnGetMetadata.setEnabled(false);
            btnDelete.setEnabled(false);
            uploadedFileRef = null;
            selectedImageUri = null;
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Gagal menghapus berkas", e);
            Toast.makeText(MainActivity.this, "Gagal menghapus berkas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}