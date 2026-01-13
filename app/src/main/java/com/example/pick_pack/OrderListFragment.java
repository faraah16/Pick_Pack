package com.example.pick_pack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class OrderListFragment extends Fragment {

    private static final String ARG_TYPE = "type";

    public static OrderListFragment newInstance(String type) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View v = inflater.inflate(R.layout.fragment_order_list, container, false);

        TextView tvTitle = v.findViewById(R.id.tvTitle);
        LinearLayout containerList = v.findViewById(R.id.order_list_container);

        String deliveryType = getArguments() != null
                ? getArguments().getString(ARG_TYPE)
                : "";

        tvTitle.setText("Orders - " + deliveryType);

        FirebaseFirestore.getInstance()
                .collection("orders")
                .whereEqualTo("deliveryType", deliveryType)
                .whereIn("status", Arrays.asList("pending", "preparing"))
                .get()
                .addOnSuccessListener(snapshot -> {

                    containerList.removeAllViews();

                    if (snapshot.isEmpty()) {
                        TextView empty = new TextView(getContext());
                        empty.setText("No orders found");
                        containerList.addView(empty);
                        return;
                    }

                    snapshot.forEach(doc -> {
                        String orderId = doc.getId();
                        String status = doc.getString("status");

                        TextView tv = new TextView(getContext());
                        tv.setText(
                                "Order ID: " + orderId +
                                        "\nStatus: " + status
                        );
                        tv.setPadding(0, 16, 0, 16);
                        tv.setTextSize(16);

                        tv.setOnClickListener(v1 ->
                                requireActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(
                                                R.id.employee_fragment_container,
                                                TrackOrderFragment.newInstance(
                                                        orderId,
                                                        true // employee
                                                )
                                        )
                                        .addToBackStack(null)
                                        .commit()
                        );

                        containerList.addView(tv);
                    });
                });

        return v;
    }


}
