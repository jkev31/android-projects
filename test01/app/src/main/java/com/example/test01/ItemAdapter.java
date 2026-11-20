package com.example.test01;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> itemList;
    private ApiServices apiServices;

    public ItemAdapter(List<Item> itemList, ApiServices apiServices) {
        this.itemList = itemList;
        this.apiServices = apiServices;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.text1.setText("ID: " + item.getId());
        holder.text2.setText(item.getNama());
        holder.text3.setText(item.getTelp());
        holder.text4.setText("Umur: " + String.valueOf(item.getUmur()));
        holder.text5.setText(item.getPendidikan());
        holder.text6.setText(item.getAlamat());

        // Klik untuk Edit
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.dialog_edit_item, null);

            EditText etEditNama = dialogView.findViewById(R.id.etEditNama);
            EditText etEditAlamat = dialogView.findViewById(R.id.etEditAlamat);
            EditText etEditTelp = dialogView.findViewById(R.id.etEditTelp);
            EditText etEditUmur = dialogView.findViewById(R.id.etEditUmur);
            EditText etEditPendidikan = dialogView.findViewById(R.id.etEditPendidikan);

            // Isi data lama
            etEditNama.setText(item.getNama());
            etEditAlamat.setText(item.getAlamat());
            etEditTelp.setText(item.getTelp());
            etEditUmur.setText(String.valueOf(item.getUmur()));
            etEditPendidikan.setText(item.getPendidikan());

            new AlertDialog.Builder(context)
                    .setTitle("Edit Item")
                    .setView(dialogView)
                    .setPositiveButton("Simpan", (dialog, which) -> {
                        String nama = etEditNama.getText().toString();
                        String alamat = etEditAlamat.getText().toString();
                        String telp = etEditTelp.getText().toString();
                        int umur = Integer.parseInt(etEditUmur.getText().toString());
                        String pendidikan = etEditPendidikan.getText().toString();

                        // Alur Retrofit untuk Update (memakai id dari database)
                        apiServices.updateKaryawan(item.getId(), nama, alamat, telp, umur, pendidikan).enqueue(new Callback<Item>() {
                            @Override
                            public void onResponse(Call<Item> call, Response<Item> response) {
                                if (response.isSuccessful()) {
                                    item.setNama(nama);
                                    item.setAlamat(alamat);
                                    item.setTelp(telp);
                                    item.setUmur(umur);
                                    item.setPendidikan(pendidikan);
                                    notifyItemChanged(holder.getAdapterPosition());
                                    Toast.makeText(context, "Item diupdate", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Gagal update: " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Item> call, Throwable t) {
                                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

        // Long klik untuk Delete
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Konfirmasi Hapus")
                    .setMessage("Yakin ingin menghapus item ini?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        // Alur Retrofit untuk Delete (memakai id dari database)
                        apiServices.deleteKaryawan(item.getId()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    int currentPosition = holder.getAdapterPosition();
                                    itemList.remove(currentPosition);
                                    notifyItemRemoved(currentPosition);
                                    Toast.makeText(v.getContext(), "Item dihapus", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(v.getContext(), "Gagal hapus: " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(v.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Batal", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2, text3, text4, text5, text6;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.tvNama);
            text2 = itemView.findViewById(R.id.tvDetail1);
            text3 = itemView.findViewById(R.id.tvDetail2);
            text4 = itemView.findViewById(R.id.tvDetail3);
            text5 = itemView.findViewById(R.id.tvDetail4);
            text6 = itemView.findViewById(R.id.tvDetail5);
        }
    }
}