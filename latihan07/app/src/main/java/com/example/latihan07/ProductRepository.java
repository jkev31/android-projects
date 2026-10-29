package com.example.latihan07;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProductRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

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

    public void addProductWithImage(String name, double price, long stock, Uri imageUri, TransactionCallback callback) {
        String fileName = "products/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> {
                    Product product = new Product(name, price, stock, downloadUri.toString());
                    db.collection("products").add(product)
                            .addOnSuccessListener(doc -> callback.onSuccess("Produk berhasil ditambahkan!"))
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError("Gagal mengunggah gambar: " + e.getMessage()));
    }

    public interface TransactionCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}