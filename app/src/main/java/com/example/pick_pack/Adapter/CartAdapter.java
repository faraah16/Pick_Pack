package com.example.pick_pack.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pick_pack.Model.CartItem;
import com.example.pick_pack.ui.CartManager;
import com.example.pick_pack.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cart;

    public CartAdapter(List<CartItem> cart) {
        this.cart = cart;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder h, int position) {

        CartItem item = cart.get(position);

        h.tvName.setText(item.getName());
        h.tvDetails.setText(item.getSize() + " | " + item.getMaterial());
        h.tvQty.setText(String.valueOf(item.getQuantity()));
        h.tvPrice.setText(item.getTotalPrice() + " DH");

        // IMAGE DYNAMIQUE
        h.imgCube.setImageResource(
                getImageFor(item.getName(), item.getMaterial())
        );

        // ➕
        h.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            CartManager.notifyChange();
        });

        // ➖
        h.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
                CartManager.notifyChange();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cart.size();
    }

    // ================= IMAGE LOGIC =================
    private int getImageFor(String cubeName, String material) {

        material = material.trim();

        if (cubeName.equalsIgnoreCase("Red Cube")) {
            switch (material) {
                case "Bois": return R.drawable.r_cube_bois;
                case "Métal": return R.drawable.r_cube_metal;
                case "Plastique": return R.drawable.r_cube_plastique;
                default: return R.drawable.r_cube_carton;
            }
        }

        if (cubeName.equalsIgnoreCase("Green Cube")) {
            switch (material) {
                case "Bois": return R.drawable.g_cube_bois;
                case "Métal": return R.drawable.g_cube_metal;
                case "Plastique": return R.drawable.g_cube_plastique;
                default: return R.drawable.g_cube_carton;
            }
        }

        // Blue Cube
        switch (material) {
            case "Bois": return R.drawable.b_cube_bois;
            case "Métal": return R.drawable.b_cube_metal;
            case "Plastique": return R.drawable.b_cube_plastique;
            default: return R.drawable.b_cube_carton;
        }
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCube;
        TextView tvName, tvDetails, tvQty, tvPrice;
        Button btnPlus, btnMinus;

        CartViewHolder(View v) {
            super(v);
            imgCube = v.findViewById(R.id.imgCartCube);
            tvName = v.findViewById(R.id.tvName);
            tvDetails = v.findViewById(R.id.tvDetails);
            tvQty = v.findViewById(R.id.tvQty);
            tvPrice = v.findViewById(R.id.tvPrice);
            btnPlus = v.findViewById(R.id.btnPlus);
            btnMinus = v.findViewById(R.id.btnMinus);
        }
    }
}
