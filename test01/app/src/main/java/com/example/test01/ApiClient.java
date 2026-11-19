package com.example.test01;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Gunakan 10.0.2.2 jika memakai Emulator Android Studio
    // Ganti dengan IP Wi-Fi laptop (misal: http://192.168.1.10:8000/api/) jika memakai HP fisik
    private static final String BASE_URL = "http://172.16.36.204:8000/api/";
    private static Retrofit retrofit = null;

    public static ApiServices getApiServices() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiServices.class);
    }
}