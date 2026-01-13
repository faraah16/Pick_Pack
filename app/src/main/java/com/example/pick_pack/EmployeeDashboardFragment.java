package com.example.pick_pack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;



public class EmployeeDashboardFragment extends Fragment {

    public EmployeeDashboardFragment() {
        // constructeur vide obligatoire
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        // 1️⃣ ON INFLATE LE LAYOUT
        View v = inflater.inflate(
                R.layout.fragment_employee_dashboard,
                container,
                false
        );

        v.findViewById(R.id.btnBack).setOnClickListener(view -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.employee_fragment_container, new EmployeeMenuFragment())
                    .commit();
        });

        v.findViewById(R.id.btnExpress)
                .setOnClickListener(b ->
                        requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(
                                        R.id.employee_fragment_container,
                                        OrderListFragment.newInstance("EXPRESS")
                                )
                                .addToBackStack(null)
                                .commit()
                );

        v.findViewById(R.id.btnNormal)
                .setOnClickListener(b ->
                        requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(
                                        R.id.employee_fragment_container,
                                        OrderListFragment.newInstance("NORMAL")
                                )
                                .addToBackStack(null)
                                .commit()
                );

        v.findViewById(R.id.btnPoint)
                .setOnClickListener(b ->
                        requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(
                                        R.id.employee_fragment_container,
                                        OrderListFragment.newInstance("POINT")
                                )
                                .addToBackStack(null)
                                .commit()
                );


        // 3️⃣ ON RETOURNE LA VUE
        return v;
    }
}
