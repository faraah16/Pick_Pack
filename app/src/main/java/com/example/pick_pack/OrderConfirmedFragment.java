package com.example.pick_pack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class OrderConfirmedFragment extends Fragment {

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View v = inflater.inflate(
                R.layout.fragment_order_confirmed,
                container,
                false
        );
        Button btnFinish = v.findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(view -> {
            requireActivity().finish();
        });

        TextView tvOrderId = v.findViewById(R.id.tvOrderId);
        TextView tvPaymentInfo = v.findViewById(R.id.tvPaymentInfo);

        Bundle args = getArguments();
        if (args == null) return v;

        String orderId = args.getString("orderId", "N/A");
        String paymentMethod = args.getString("paymentMethod", "Cash");
        double total = args.getDouble("total", 0);

        tvOrderId.setText("Order ID : " + orderId);

        if (paymentMethod.equals("Card")) {
            tvPaymentInfo.setText(
                    "Payment successful\nPaid : " +
                            String.format("%.2f DH", total)
            );
        } else {
            tvPaymentInfo.setText(
                    "Cash on delivery\nAmount to pay : " +
                            String.format("%.2f DH", total)
            );
        }

        return v;
    }
}
