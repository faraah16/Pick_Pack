package com.example.pick_pack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class StockMenuFragment extends Fragment {

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View v = inflater.inflate(R.layout.fragment_stock_menu, container, false);

        Button btnViewStock = v.findViewById(R.id.btnViewStock);
        Button btnEditStock = v.findViewById(R.id.btnEditStock);
        Button btnBack = v.findViewById(R.id.btnBack);

        btnViewStock.setOnClickListener(view -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.employee_fragment_container, new ViewStockFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnEditStock.setOnClickListener(view -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.employee_fragment_container, new EditStockFragment())
                    .addToBackStack(null)
                    .commit();
        });
        btnBack.setOnClickListener(view ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );



        return v;
    }
}
