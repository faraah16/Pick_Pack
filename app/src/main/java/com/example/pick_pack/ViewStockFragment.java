package com.example.pick_pack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class ViewStockFragment extends Fragment {

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View v = inflater.inflate(R.layout.fragment_view_stock, container, false);
        v.findViewById(R.id.btnBack).setOnClickListener(view -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.employee_fragment_container, new StockMenuFragment())
                    .commit();
        });
        TextView tvStock = v.findViewById(R.id.tvStock);

        FirebaseFirestore.getInstance()
                .collection("stock")
                .get()
                .addOnSuccessListener(snapshot -> {

                    StringBuilder sb = new StringBuilder();

                    snapshot.forEach(doc -> {

                        int total = 0;

                        // 🔢 Calcul du total du cube
                        for (Object value : doc.getData().values()) {
                            if (value instanceof Number) {
                                total += ((Number) value).intValue();
                            }
                        }

                        sb.append(
                                        doc.getId()
                                                .replace("cube_", "Cube ")
                                )
                                .append(" (Total : ")
                                .append(total)
                                .append(")")
                                .append("\n");

                        // 📦 Détail par matériau
                        for (Map.Entry<String, Object> entry : doc.getData().entrySet()) {
                            sb.append("- ")
                                    .append(capitalize(entry.getKey()))
                                    .append(" : ")
                                    .append(entry.getValue())
                                    .append("\n");
                        }

                        sb.append("\n");
                    });

                    tvStock.setText(sb.toString());
                })
                .addOnFailureListener(e ->
                        tvStock.setText("Erreur de chargement du stock ❌")
                );

        return v;
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
