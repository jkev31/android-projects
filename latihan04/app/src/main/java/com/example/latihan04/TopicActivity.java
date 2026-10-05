package com.example.latihan04;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

public class TopicActivity extends AppCompatActivity {

    private static final String TAG = "TOPIC_ACTIVITY";
    private EditText etDeviceToken, etTopicName;
    private Button btnGetToken, btnCopyToken, btnSubscribe, btnUnsubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        etDeviceToken = findViewById(R.id.etDeviceToken);
        etTopicName = findViewById(R.id.etTopicName);
        btnGetToken = findViewById(R.id.btnGetToken);
        btnCopyToken = findViewById(R.id.btnCopyToken);
        btnSubscribe = findViewById(R.id.btnSubscribe);
        btnUnsubscribe = findViewById(R.id.btnUnsubscribe);

        btnGetToken.setOnClickListener(v -> fetchDeviceToken());
        btnCopyToken.setOnClickListener(v -> copyTokenToClipboard());
        btnSubscribe.setOnClickListener(v -> subscribeToTopic());
        btnUnsubscribe.setOnClickListener(v -> unsubscribeFromTopic());
    }

    private void fetchDeviceToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Gagal mengambil token FCM", task.getException());
                        Toast.makeText(this, "Gagal mengambil token", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Mendapatkan Token FCM Baru
                    String token = task.getResult();
                    Log.d(TAG, "Current FCM Token: " + token);
                    etDeviceToken.setText(token);
                    Toast.makeText(this, "Token Berhasil Diambil!", Toast.LENGTH_SHORT).show();
                });
    }

    private void copyTokenToClipboard() {
        String token = etDeviceToken.getText().toString().trim();
        if (token.isEmpty() || token.equals("Memuat Token FCM...")) {
            Toast.makeText(this, "Token belum diambil! Klik 'Ambil FCM Token' terlebih dahulu.", Toast.LENGTH_SHORT).show();
            return;
        }

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("FCM Token", token);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "FCM Token berhasil disalin ke Clipboard!", Toast.LENGTH_SHORT).show();
        }
    }

    private void subscribeToTopic() {
        String topic = etTopicName.getText().toString().trim();
        if (topic.isEmpty()) {
            Toast.makeText(this, "Nama topik tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String msg = "Berhasil Subscribe ke topik: " + topic;
                        Log.d(TAG, msg);
                        Toast.makeText(TopicActivity.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        String msg = "Gagal Subscribe ke topik!";
                        Log.e(TAG, msg, task.getException());
                        Toast.makeText(TopicActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void unsubscribeFromTopic() {
        String topic = etTopicName.getText().toString().trim();
        if (topic.isEmpty()) {
            Toast.makeText(this, "Nama topik tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String msg = "Berhasil Unsubscribe dari topik: " + topic;
                        Log.d(TAG, msg);
                        Toast.makeText(TopicActivity.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        String msg = "Gagal Unsubscribe dari topik!";
                        Log.e(TAG, msg, task.getException());
                        Toast.makeText(TopicActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
