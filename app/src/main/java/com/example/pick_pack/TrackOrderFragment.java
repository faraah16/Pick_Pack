package com.example.pick_pack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pick_pack.ui.CommanderRobotActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class TrackOrderFragment extends Fragment {

    private EditText etOrderId;
    private TextView tvResult;
    private Button btnSearch, btnClose, btnCommanderRobot;
    private Button btnChangeStatus, btnPending, btnPreparing, btnShipped;
    private LinearLayout statusButtonsContainer;

    private boolean isEmployee = false;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View v = inflater.inflate(R.layout.fragment_track_order, container, false);

        // ================= UI =================
        etOrderId = v.findViewById(R.id.etOrderId);
        tvResult = v.findViewById(R.id.tvResult);
        btnSearch = v.findViewById(R.id.btnSearch);
        btnClose = v.findViewById(R.id.btnClose);
        btnCommanderRobot = v.findViewById(R.id.btnCommanderRobot);

        btnChangeStatus = v.findViewById(R.id.btnChangeStatus);
        btnPending = v.findViewById(R.id.btnPending);
        btnPreparing = v.findViewById(R.id.btnPreparing);
        btnShipped = v.findViewById(R.id.btnShipped); // ⚠️ garde l'id XML si besoin
        statusButtonsContainer = v.findViewById(R.id.statusButtonsContainer);

        // ================= ROLE =================
        if (getArguments() != null) {
            isEmployee = getArguments().getBoolean("isEmployee", false);
            String passedId = getArguments().getString("orderId");
            if (!TextUtils.isEmpty(passedId)) {
                etOrderId.setText(passedId);
            }
        }

        // ================= VISIBILITÉ =================
        if (!isEmployee) {
            btnCommanderRobot.setVisibility(View.GONE);
            btnChangeStatus.setVisibility(View.GONE);
            statusButtonsContainer.setVisibility(View.GONE);
        }

        // ================= ACTIONS =================
        btnSearch.setOnClickListener(v1 -> searchOrder());

        btnClose.setOnClickListener(v1 ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        btnCommanderRobot.setOnClickListener(v1 -> {
            String id = etOrderId.getText().toString().trim();
            if (id.isEmpty()) return;

            Intent i = new Intent(getActivity(), CommanderRobotActivity.class);
            i.putExtra("orderId", id);
            startActivity(i);
        });

        btnChangeStatus.setOnClickListener(v1 ->
                statusButtonsContainer.setVisibility(
                        statusButtonsContainer.getVisibility() == View.GONE
                                ? View.VISIBLE
                                : View.GONE
                )
        );

        // ✅ STATUTS CORRECTS
        btnPending.setOnClickListener(v1 -> applyStatus("pending"));
        btnPreparing.setOnClickListener(v1 -> applyStatus("preparing"));
        btnShipped.setOnClickListener(v1 -> applyStatus("shipped"));

        if (getArguments() != null && getArguments().containsKey("orderId")) {
            searchOrder();
        }

        return v;
    }

    // =================================================
    // 🔍 RECHERCHE COMMANDE
    // =================================================
    private void searchOrder() {
        String orderId = etOrderId.getText().toString().trim();

        if (orderId.isEmpty()) {
            Toast.makeText(getContext(), "Enter Order ID", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        tvResult.setText("Order not found");
                        return;
                    }

                    String status = doc.getString("status");
                    String payment = doc.getString("paymentMethod");
                    Double finalTotal = doc.getDouble("finalTotal");
                    Double deliveryPrice = doc.getDouble("deliveryPrice");

                    StringBuilder result = new StringBuilder();

                    result.append("Order ID: ").append(orderId).append("\n");
                    result.append("Status: ").append(status).append("\n");
                    result.append("Payment: ").append(payment).append("\n");
                    result.append("Delivery price: ").append(deliveryPrice).append(" DH\n");
                    result.append("Total: ").append(finalTotal).append(" DH\n\n");

                    List<Map<String, Object>> items =
                            (List<Map<String, Object>>) doc.get("items");

                    if (items != null) {
                        result.append("Items:\n");
                        for (Map<String, Object> item : items) {
                            result.append("• ")
                                    .append(item.get("name"))
                                    .append(" (")
                                    .append(item.get("material"))
                                    .append(") x")
                                    .append(item.get("quantity"))
                                    .append("\n");
                        }
                    }

                    tvResult.setText(result.toString());
                });
    }

    public static TrackOrderFragment newInstance(String orderId, boolean isEmployee) {
        TrackOrderFragment f = new TrackOrderFragment();
        Bundle b = new Bundle();
        b.putString("orderId", orderId);
        b.putBoolean("isEmployee", isEmployee);
        f.setArguments(b);
        return f;
    }


    // =================================================
    // 🔄 STATUT
    // =================================================
    private void applyStatus(String newStatus) {

        String orderId = etOrderId.getText().toString().trim();
        if (orderId.isEmpty()) return;

        updateResultTextLocal(newStatus);
        statusButtonsContainer.setVisibility(View.GONE);

        FirebaseFirestore.getInstance()
                .collection("orders")
                .document(orderId)
                .update("status", newStatus);
    }


    // =================================================
    // 🔄 UI IMMÉDIATE
    // =================================================
    private void updateResultTextLocal(String newStatus) {

        String icon;
        String label;

        switch (newStatus) {
            case "pending":
                icon = "🕒";
                label = "Pending";
                break;

            case "preparing":
                icon = "⚙️";
                label = "Preparing";
                break;

            case "shipped":
                icon = "📦";
                label = "Shipped";
                break;

            default:
                icon = "❓";
                label = newStatus;
        }

        String current = tvResult.getText().toString();

        tvResult.setText(
                current.replaceAll(
                        "Status:.*",
                        "Status: " + icon + " " + label
                )
        );
    }

}
