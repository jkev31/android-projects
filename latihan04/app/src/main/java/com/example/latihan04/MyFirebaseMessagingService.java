package com.example.latihan04;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM_SERVICE";
    private static final String CHANNEL_ID = "channel_fcm_lecture";
    private static final String CHANNEL_NAME = "Notifikasi Perkuliahan";



    //Alur method ini:
    //mengirim token terbaru ke server backend
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM Registration Token: " + token);

        // Kirimkan token baru ke server backend Anda
        sendTokenToBackendServer(token);
    }


    //Alur method ini:
    //memeriksa notifikasi -> mengambil title & body dari notifikasi
    //memeriksa data payload -> mengambil custom title & body dari data payload
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Pesan diterima dari: " + remoteMessage.getFrom());

        String title = "Notifikasi Baru";
        String body = "Anda menerima pesan baru.";

        // 1. Memeriksa Notification Payload (Judul & Isi Notifikasi)
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        // 2. Memeriksa Data Payload (Kunci Kustom Klien)
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            Log.d(TAG, "Isi Data Payload: " + data.toString());

            if (data.containsKey("custom_title")) {
                title = data.get("custom_title");
            }
            if (data.containsKey("custom_body")) {
                body = data.get("custom_body");
            }
        }

        // 3. Tampilkan Notifikasi di System Tray HP
        sendNotification(title, body, remoteMessage.getData());
    }


    //Alur method ini:
    //menyisipkan payload ke intent -> User klik notifikasi -> data dibaca oleh MainActivity
    //membuat notifikasi -> menampilkan notifikasi di system tray hp
    private void sendNotification(String title, String body, Map<String, String> dataMap) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Memasukkan data payload ke dalam Intent Extras agar bisa dibaca saat notifikasi diklik
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }

        // PendingIntent untuk membuka MainActivity saat notifikasi ditekan
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Wajib membuat Notification Channel untuk Android 8.0 (Oreo / API 26) ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Kanal untuk Notifikasi FCM Perkuliahan");

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
        }
    }

    private void sendTokenToBackendServer(String token) {
        // Logika untuk mengirimkan token ke server backend Anda (jika ada)
        Log.i(TAG, "Sinkronisasi token ke server: " + token);
    }
}
