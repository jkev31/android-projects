package com.example.test01;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Ganti x dengan angka terakhir IP Wi-Fi laptop (cek lewat ipconfig).
    // Emulator Android Studio, pakai http://10.0.2.2:8000/api/
    private static final String BASE_URL = "http://192.168.101.4:8000/api/";
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