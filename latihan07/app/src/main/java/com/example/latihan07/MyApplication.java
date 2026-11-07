package com.example.latihan07;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Firestore modern sudah otomatis mengaktifkan offline persistence
    }
}