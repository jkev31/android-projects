package com.example.latihan04;

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

import com.google.firebase.firestore.CollectionReference;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> itemList;
    CollectionReference itemsRef;




    public ItemAdapter(List<Item> itemList, CollectionReference itemsRef) {
        this.itemList = itemList;
        this.itemsRef = itemsRef;
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
        holder.text1.setText(item.getKode());
        holder.text2.setText("Nama: " + item.getNama());
        holder.text3.setText("Telp: " + "+62" + item.getTelp());
        holder.text4.setText("Umur: " + String.valueOf(item.getUmur()));
        holder.text5.setText("Pendidikan: " + item.getPendidikan());
        holder.text6.setText("Alamat: " + "Jl. " + item.getAlamat());




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
                        item.setNama(etEditNama.getText().toString());
                        item.setAlamat(etEditAlamat.getText().toString());
                        item.setTelp(etEditTelp.getText().toString());
                        String strUmur = etEditUmur.getText().toString().trim();
                        if (strUmur.isEmpty()) {
                            Toast.makeText(context, "Umur wajib diisi!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        item.setUmur(Integer.parseInt(strUmur));
                        item.setPendidikan(etEditPendidikan.getText().toString());


                        //Memperbarui dokumen item di Firestore
                        itemsRef.document(item.getKode()).update(
                                        "nama", item.getNama(),
                                        "alamat", item.getAlamat(),
                                        "telp", item.getTelp(),
                                        "umur", item.getUmur(),
                                        "pendidikan", item.getPendidikan()
                                )
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(context, "Item diupdate", Toast.LENGTH_SHORT).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Gagal update: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
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
                        // Menghapus dokumen item dari Firestore
                        itemsRef.document(item.getKode()).delete()
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(v.getContext(), "Item dihapus", Toast.LENGTH_SHORT).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(v.getContext(), "Gagal hapus: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
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