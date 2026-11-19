package com.example.test01;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiServices {
    String BASE_URL = "http://172.16.36.204:8000/api/";

    // 1. READ: Mengambil semua data dari GET /api/karyawan
    @GET("karyawan")
    Call<List<Item>> getAllKaryawan();

    // 2. READ DETAIL: Mengambil 1 data berdasarkan ID/kode dari GET /api/karyawan/{id}
    @GET("karyawan/{id}")
    Call<Item> getKaryawanById(@Path("id") String id);

    // 3. CREATE: Menambah data baru ke POST /api/karyawan
    @FormUrlEncoded
    @POST("karyawan")
    Call<Item> createKaryawan(
            @Field("kode") String kode,
            @Field("nama") String nama,
            @Field("alamat") String alamat,
            @Field("telp") String telp,
            @Field("umur") int umur,
            @Field("pendidikan") String pendidikan
    );

    // 4. UPDATE: Memperbarui data ke PUT /api/karyawan/{id}
    @FormUrlEncoded
    @PUT("karyawan/{id}")
    Call<Item> updateKaryawan(
            @Path("id") String id,
            @Field("nama") String nama,
            @Field("alamat") String alamat,
            @Field("telp") String telp,
            @Field("umur") int umur,
            @Field("pendidikan") String pendidikan
    );

    // 5. DELETE: Menghapus data pada DELETE /api/karyawan/{id}
    @DELETE("karyawan/{id}")
    Call<Void> deleteKaryawan(@Path("id") String id);
}