package com.example.latihan04;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    private TextView tvPayloadContent;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Izin Notifikasi Diberikan", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Izin Notifikasi Ditolak!", Toast.LENGTH_LONG).show();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPayloadContent = findViewById(R.id.tvPayloadContent);

        checkNotificationPermission();

        handleNotificationIntentData();

        Button btnGoToTopic = findViewById(R.id.btnGoToTopic);
        btnGoToTopic.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TopicActivity.class);
            startActivity(intent);
        });

    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void handleNotificationIntentData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("=== DATA PAYLOAD DITERIMA ===\n\n");

            // Iterasi seluruh key-value di dalam Intent Extras
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Intent Extra Key: " + key + " | Value: " + value);

                // Mengabaikan kunci internal milik Firebase (seperti google.message_id, gcm.n.e, dll)
                if (!key.startsWith("google.") && !key.startsWith("gcm.")) {
                    sb.append(key).append(": ").append(value).append("\n");
                }
            }

            tvPayloadContent.setText(sb.toString());
        }
    }
}