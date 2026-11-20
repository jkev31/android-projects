package com.example.test01;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiServices {

    // 1. READ: Mengambil semua data dari GET /api/karyawans
    @GET("karyawans")
    Call<KaryawanResponse> getAllKaryawan();

    // 2. READ DETAIL: Mengambil 1 data berdasarkan id dari GET /api/karyawans/{id}
    @GET("karyawans/{id}")
    Call<Item> getKaryawanById(@Path("id") int id);

    // 3. CREATE: Menambah data baru ke POST /api/karyawans (id dibuat otomatis oleh database)
    @FormUrlEncoded
    @POST("karyawans")
    Call<Item> createKaryawan(
            @Field("nama") String nama,
            @Field("alamat") String alamat,
            @Field("telp") String telp,
            @Field("umur") int umur,
            @Field("pendidikan") String pendidikan
    );

    // 4. UPDATE: Memperbarui data ke PUT /api/karyawans/{id}
    @FormUrlEncoded
    @PUT("karyawans/{id}")
    Call<Item> updateKaryawan(
            @Path("id") int id,
            @Field("nama") String nama,
            @Field("alamat") String alamat,
            @Field("telp") String telp,
            @Field("umur") int umur,
            @Field("pendidikan") String pendidikan
    );

    // 5. DELETE: Menghapus data pada DELETE /api/karyawans/{id}
    @DELETE("karyawans/{id}")
    Call<Void> deleteKaryawan(@Path("id") int id);
}