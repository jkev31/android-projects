package com.example.latihan07;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // 1. Mengambil Realtime List Produk dari Firestore
    public LiveData<List<Product>> getProductsLiveData() {
        MutableLiveData<List<Product>> liveData = new MutableLiveData<>();
        db.collection("products").addSnapshotListener((value, error) -> {
            if (error != null || value == null) return;
            List<Product> list = new ArrayList<>();
            for (QueryDocumentSnapshot doc : value) {
                Product product = doc.toObject(Product.class);
                product.setId(doc.getId());
                list.add(product);
            }
            liveData.setValue(list);
        });
        return liveData;
    }

    // 2. Transaksi Beli (Pemotongan Stok Atomik)
    public void buyProduct(String productId, int quantity, TransactionCallback callback) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            callback.onError("Pengguna belum login!");
            return;
        }

        DocumentReference productRef = db.collection("products").document(productId);
        DocumentReference orderRef = db.collection("orders").document();

        db.runTransaction(transaction -> {
                    DocumentSnapshot snapshot = transaction.get(productRef);
                    Long currentStock = snapshot.getLong("stock");
                    if (currentStock == null || currentStock < quantity) {
                        throw new FirebaseFirestoreException(
                                "Stok barang tidak mencukupi!",
                                FirebaseFirestoreException.Code.ABORTED
                        );
                    }

                    transaction.update(productRef, "stock", currentStock - quantity);

                    Map<String, Object> order = new HashMap<>();
                    order.put("userId", userId);
                    order.put("productId", productId);
                    order.put("quantity", quantity);
                    order.put("timestamp", FieldValue.serverTimestamp());
                    transaction.set(orderRef, order);

                    return null;
                }).addOnSuccessListener(aVoid -> callback.onSuccess("Pembelian Berhasil!"))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // 3. Konversi Uri Gambar ke Base64 String
    private String convertUriToBase64(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) inputStream.close();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // Kompres gambar ke format JPEG (kualitas 70%) agar ukuran < 1MB (Batas Firestore)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 4. Simpan Produk dengan Gambar Base64 ke Firestore
    public void addProductWithBase64(Context context, String name, double price, long stock, Uri imageUri, TransactionCallback callback) {
        String base64Image = convertUriToBase64(context, imageUri);

        if (base64Image == null) {
            callback.onError("Gagal mengolah gambar!");
            return;
        }

        Product product = new Product(name, price, stock, base64Image);
        db.collection("products").add(product)
                .addOnSuccessListener(doc -> callback.onSuccess("Produk berhasil ditambahkan!"))
                .addOnFailureListener(e -> callback.onError("Gagal menyimpan produk: " + e.getMessage()));
    }

    public interface TransactionCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}