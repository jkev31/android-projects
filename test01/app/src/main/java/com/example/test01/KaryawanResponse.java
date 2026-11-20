package com.example.test01;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KaryawanResponse {
    @SerializedName("data")
    private List<Item> data;

    public List<Item> getData() {
        return data;
    }

    public void setData(List<Item> data) {
        this.data = data;
    }
}