package com.example.pick_pack;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pick_pack.ui.CommanderRobotActivity;

public class EmployeeMenuFragment extends Fragment {

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View v = inflater.inflate(R.layout.fragment_employee_menu, container, false);
        v.findViewById(R.id.btnBack).setOnClickListener(view -> {
            requireActivity().finish();
        });

        Button btnStock = v.findViewById(R.id.btnStock);
        Button btnRobot = v.findViewById(R.id.btnRobot);
        Button btnCheckOrders = v.findViewById(R.id.btnCheckOrders);

        btnStock.setOnClickListener(view -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.employee_fragment_container, new StockMenuFragment())
                    .addToBackStack(null)
                    .commit();
        });


        btnRobot.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), CommanderRobotActivity.class);
            startActivity(intent);
        });


        btnCheckOrders.setOnClickListener(view -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.employee_fragment_container, new EmployeeDashboardFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return v;
    }
}
