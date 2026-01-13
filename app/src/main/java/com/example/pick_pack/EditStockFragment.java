package com.example.pick_pack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditStockFragment extends Fragment {

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View v = inflater.inflate(R.layout.fragment_edit_stock, container, false);
        v.findViewById(R.id.btnBack).setOnClickListener(view -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.employee_fragment_container, new StockMenuFragment())
                    .commit();
        });

        Spinner spColor = v.findViewById(R.id.spinnerColor);
        Spinner spMaterial = v.findViewById(R.id.spinnerMaterial);
        EditText etQty = v.findViewById(R.id.etQuantity);
        Button btnSave = v.findViewById(R.id.btnSave);

        // 🔹 Spinner Couleur
        spColor.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("Rouge", "Bleu", "Vert")
        ));

        // 🔹 Spinner Matériau
        spMaterial.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("Bois", "Plastique", "Metal", "Carton")
        ));

        btnSave.setOnClickListener(vw -> {

            String qtyText = etQty.getText().toString().trim();
            if (qtyText.isEmpty()) {
                Toast.makeText(
                        getContext(),
                        "Entrer une quantité",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            int qtyToAdd = Integer.parseInt(qtyText);
            if (qtyToAdd <= 0) {
                Toast.makeText(
                        getContext(),
                        "Quantité invalide",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            String color = spColor.getSelectedItem().toString().toLowerCase();
            String material = spMaterial.getSelectedItem().toString().toLowerCase();

            String docId = "cube_" + color; // ex: cube_rouge

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("stock")
                    .document(docId)
                    .get()
                    .addOnSuccessListener(doc -> {

                        long currentQty = 0;
                        if (doc.exists() && doc.contains(material)) {
                            Long q = doc.getLong(material);
                            if (q != null) currentQty = q;
                        }

                        Map<String, Object> update = new HashMap<>();
                        update.put(material, currentQty + qtyToAdd);

                        db.collection("stock")
                                .document(docId)
                                .set(update, SetOptions.merge())
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(
                                            getContext(),
                                            "Stock mis à jour ✅",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    etQty.setText("");
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(
                                                getContext(),
                                                "Erreur mise à jour stock ❌",
                                                Toast.LENGTH_LONG
                                        ).show()
                                );
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(
                                    getContext(),
                                    "Erreur lecture stock ❌",
                                    Toast.LENGTH_LONG
                            ).show()
                    );
        });

        return v;
    }
}
