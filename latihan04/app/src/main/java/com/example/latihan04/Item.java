package com.example.latihan04;

public class Item {
    private String kode;
    private String nama;
    private String alamat;

    private String telp;

    private int umur;
    private String pendidikan;

    public Item() {} // Diperlukan oleh Firebase

    public Item(String kode, String nama, String alamat, String telp, int umur, String pendidikan) {
        this.kode = kode;
        this.nama = nama;
        this.alamat = alamat;
        this.telp = telp;
        this.umur = umur;
        this.pendidikan = pendidikan;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
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

    public void setPendidikan(String pendidikan) {this.pendidikan = pendidikan;
    }


}