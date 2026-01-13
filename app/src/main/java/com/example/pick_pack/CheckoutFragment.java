package com.example.pick_pack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pick_pack.Model.CartItem;
import com.example.pick_pack.ui.CartManager;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class CheckoutFragment extends Fragment {

    private TextView tvCheckoutTotal;
    private EditText etCardNumber;
    private RadioGroup rgPayment;
    private RadioGroup rgDelivery;

    private static final double DELIVERY_NORMAL = 10.0;
    private static final double DELIVERY_EXPRESS = 25.0;
    private static final double DELIVERY_POINT = 5.0;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View v = inflater.inflate(R.layout.fragment_checkout, container, false);

        v.findViewById(R.id.btnBack).setOnClickListener(view -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });

        rgPayment = v.findViewById(R.id.rgPayment);
        rgDelivery = v.findViewById(R.id.rgDelivery); // ✅ OBLIGATOIRE
        etCardNumber = v.findViewById(R.id.etCardNumber);
        tvCheckoutTotal = v.findViewById(R.id.tvCheckoutTotal);

        tvCheckoutTotal.setText(
                "Total à payer : " +
                        String.format("%.2f DH", CartManager.getTotal())
        );

        rgPayment.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCard) {
                etCardNumber.setVisibility(View.VISIBLE);
            } else {
                etCardNumber.setVisibility(View.GONE);
            }
        });

        Button btnConfirm = v.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(v1 -> confirmOrder());

        return v;
    }


    // ================= TOTAL =================
    private void updateTotal() {
        tvCheckoutTotal.setText(
                "Total à payer : " +
                        String.format("%.2f DH", CartManager.getTotal())
        );
    }

    // ================= CONFIRM ORDER =================
    private void confirmOrder() {

        // ================= PAYMENT =================
        int paymentId = rgPayment.getCheckedRadioButtonId();
        String paymentMethod;

        if (paymentId == R.id.rbCard) {
            paymentMethod = "Card";

            String card = etCardNumber.getText().toString().trim();
            if (card.length() != 24 || !card.matches("\\d+")) {
                Toast.makeText(
                        getContext(),
                        "Card number must contain exactly 24 digits",
                        Toast.LENGTH_LONG
                ).show();
                return;
            }

        } else if (paymentId == R.id.rbCash) {
            paymentMethod = "Cash";
        } else {
            Toast.makeText(
                    getContext(),
                    "Select payment method",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // ================= CART CHECK =================
        if (CartManager.getCart().isEmpty()) {
            Toast.makeText(
                    getContext(),
                    "Cart is empty",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
// ================= DELIVERY =================
        int deliveryId = rgDelivery.getCheckedRadioButtonId();
        double deliveryPrice;
        String deliveryType;

        if (deliveryId == R.id.rbNormal) {
            deliveryPrice = 10;
            deliveryType = "NORMAL";
        } else if (deliveryId == R.id.rbExpress) {
            deliveryPrice = 25;
            deliveryType = "EXPRESS";
        } else if (deliveryId == R.id.rbPoint) {
            deliveryPrice = 5;
            deliveryType = "POINT";
        } else {
            Toast.makeText(
                    getContext(),
                    "Select delivery type",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }



        // ================= TOTAL =================
        double cartTotal = CartManager.getTotal();
        double finalTotal = cartTotal + deliveryPrice;

        // ================= ORDER ID =================
        String orderId = "ORD-" + System.currentTimeMillis();

        // ================= FIREBASE =================
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> order = new HashMap<>();
        order.put("orderId", orderId);
        order.put("cartTotal", cartTotal);
        order.put("deliveryPrice", deliveryPrice);
        order.put("finalTotal", finalTotal);
        order.put("paymentMethod", paymentMethod);
        order.put("status", "pending");
        order.put("deliveryType", deliveryType);// Normal / Express / Point
        order.put("timestamp", System.currentTimeMillis());
        List<Map<String, Object>> items = new ArrayList<>();
        for (CartItem item : CartManager.getCart()) {
            Map<String, Object> it = new HashMap<>();
            it.put("name", item.getName());
            it.put("size", item.getSize());
            it.put("material", item.getMaterial());
            it.put("quantity", item.getQuantity());
            it.put("price", item.getPrice());
            items.add(it);
        }
        order.put("items", items);

        // ================= SAVE ORDER =================
        db.collection("orders")
                .document(orderId)
                .set(order)
                .addOnSuccessListener(aVoid -> {

                    // 🔻 DECREMENT STOCK
                    for (CartItem item : CartManager.getCart()) {

                        String stockDocId = getStockDocId(item.getName());
                        String materialField = normalizeMaterial(item.getMaterial());


                        db.collection("stock")
                                .document(stockDocId)
                                .update(
                                        materialField,
                                        FieldValue.increment(-item.getQuantity())
                                );
                    }

                    // 🧹 CLEAR CART
                    CartManager.clear();

                    // ================= OPEN CONFIRM FRAGMENT =================
                    OrderConfirmedFragment fragment = new OrderConfirmedFragment();

                    Bundle b = new Bundle();
                    b.putString("orderId", orderId);
                    b.putString("paymentMethod", paymentMethod);
                    b.putDouble("total", finalTotal);
                    fragment.setArguments(b);

                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(
                                    R.id.cart_fragment_container,
                                    fragment
                            )
                            .commit();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Order save failed",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    // ================= UTILS =================
    private String getStockDocId(String cubeName) {

        if (cubeName.equalsIgnoreCase("Red Cube")) {
            return "cube_rouge";
        }

        if (cubeName.equalsIgnoreCase("Green Cube")) {
            return "cube_vert";
        }

        return "cube_bleu";
    }
    private String normalizeMaterial(String material) {

        material = material.trim().toLowerCase();

        switch (material) {
            case "bois":
                return "bois";
            case "carton":
                return "carton";
            case "plastique":
                return "plastique";
            case "métal":
            case "metal":
                return "metal";
            default:
                throw new IllegalArgumentException("Unknown material: " + material);
        }
    }




}
