package com.example.test01;

public class Item {
    private int id;
    private String nama;
    private String alamat;
    private String telp;
    private int umur;
    private String pendidikan;

    public Item() {} // Diperlukan oleh Gson

    public Item(int id, String nama, String alamat, String telp, int umur, String pendidikan) {
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.telp = telp;
        this.umur = umur;
        this.pendidikan = pendidikan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTelp() {
        return telp;
    }

    public void setTelp(String telp) {
        this.telp = telp;
    }

    public int getUmur() {
        return umur;
    }

    public void setUmur(int umur) {
        this.umur = umur;
    }

    public String getPendidikan() {
        return pendidikan;
    }

    public void setPendidikan(String pendidikan) {
        this.pendidikan = pendidikan;
    }
}