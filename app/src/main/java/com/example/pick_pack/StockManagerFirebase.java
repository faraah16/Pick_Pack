package com.example.pick_pack;

import com.google.firebase.firestore.FirebaseFirestore;

public class StockManagerFirebase {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void decreaseStock(
            String stockId,
            String material,
            int quantity,
            StockCallback callback
    ) {
        db.collection("stock")
                .document(stockId)
                .get()
                .addOnSuccessListener(doc -> {
                    Long current = doc.getLong(material);
                    if (current == null || current < quantity) {
                        callback.onError("Stock insuffisant");
                        return;
                    }

                    doc.getReference()
                            .update(material, current - quantity)
                            .addOnSuccessListener(v -> callback.onSuccess());
                })
                .addOnFailureListener(e ->
                        callback.onError("Firebase error"));
    }

    public interface StockCallback {
        void onSuccess();
        void onError(String msg);
    }
}
