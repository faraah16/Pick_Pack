package com.example.pick_pack.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pick_pack.Model.CartItem;
import com.example.pick_pack.Model.CubeItem;
import com.example.pick_pack.R;
import com.example.pick_pack.ui.CartManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CubeAdapter extends RecyclerView.Adapter<CubeAdapter.CubeViewHolder> {

    private List<CubeItem> cubes;

    public CubeAdapter(List<CubeItem> cubes) {
        this.cubes = cubes;
    }

    @NonNull
    @Override
    public CubeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cube_card, parent, false);
        return new CubeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CubeViewHolder h, int position) {

        CubeItem cube = cubes.get(position);

        h.tvName.setText(cube.getName());
        h.tvDescription.setText(cube.getDescription());
        h.imgCube.setImageResource(cube.getImageResId());

        h.quantity = 1;
        h.selectedSize = "S";
        h.tvQuantity.setText("1");

        if (cube.isOutOfStock()) {
            h.btnAddToCart.setEnabled(false);
            h.btnAddToCart.setAlpha(0.5f);
        } else {
            h.btnAddToCart.setEnabled(true);
            h.btnAddToCart.setAlpha(1f);
        }

        updatePrice(h, cube);
        h.spMaterial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                String material = parent.getItemAtPosition(pos).toString();

                String stockId;
                if (cube.getName().equalsIgnoreCase("Red Cube")) {
                    stockId = "cube_rouge";
                } else if (cube.getName().equalsIgnoreCase("Green Cube")) {
                    stockId = "cube_vert";
                } else {
                    stockId = "cube_bleu";
                }

                String matTmp = material.toLowerCase();
                if (matTmp.equals("métal")) matTmp = "metal";

                final String matFinal = matTmp; // ✅ FINAL

                FirebaseFirestore.getInstance()
                        .collection("stock")
                        .document(stockId)
                        .get()
                        .addOnSuccessListener(doc -> {

                            Long available = doc.getLong(matFinal); // ✅ PLUS ROUGE

                            if (available == null || available <= 0) {
                                h.btnAddToCart.setEnabled(false);
                                h.btnAddToCart.setAlpha(0.5f);
                            } else {
                                h.btnAddToCart.setEnabled(true);
                                h.btnAddToCart.setAlpha(1f);
                            }
                        });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        // ===== SIZE =====
        h.btnSizeS.setOnClickListener(v -> {
            h.selectedSize = "S";
            updatePrice(h, cube);
        });

        h.btnSizeM.setOnClickListener(v -> {
            h.selectedSize = "M";
            updatePrice(h, cube);
        });

        h.btnSizeL.setOnClickListener(v -> {
            h.selectedSize = "L";
            updatePrice(h, cube);
        });

        // ===== + =====
        h.btnPlus.setOnClickListener(v -> {
            h.quantity++;
            h.tvQuantity.setText(String.valueOf(h.quantity));
            updatePrice(h, cube);
        });

        // ===== - =====
        h.btnMinus.setOnClickListener(v -> {
            if (h.quantity > 1) {
                h.quantity--;
                h.tvQuantity.setText(String.valueOf(h.quantity));
                updatePrice(h, cube);
            }
        });

        // ===== ADD TO CART (SEULE PARTIE MODIFIÉE) =====
        h.btnAddToCart.setOnClickListener(v -> {

            String material = h.spMaterial.getSelectedItem().toString();
            String size = h.selectedSize;
            int quantity = h.quantity;

            // 🔹 stock document
            String stockId;
            if (cube.getName().equalsIgnoreCase("Red Cube")) {
                stockId = "cube_rouge";
            } else if (cube.getName().equalsIgnoreCase("Green Cube")) {
                stockId = "cube_vert";
            } else {
                stockId = "cube_bleu";
            }

            // 🔹 normalize material
            String rawMat = material.toLowerCase();

            final String mat;
            if (rawMat.equals("métal")) {
                mat = "metal";
            } else {
                mat = rawMat;
            }


            FirebaseFirestore.getInstance()
                    .collection("stock")
                    .document(stockId)
                    .get()
                    .addOnSuccessListener(doc -> {

                        Long available = doc.getLong(mat);

                        if (available == null || available < quantity) {
                            h.btnAddToCart.setEnabled(false);
                            h.btnAddToCart.setAlpha(0.5f);

                            Toast.makeText(
                                    v.getContext(),
                                    cube.getName() + " " + material + " rupture de stock",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        // ===== STOCK OK → AJOUT PANIER =====
                        double unitPrice = cube.getBasePrice();

                        if (size.equals("M")) unitPrice += 5;
                        if (size.equals("L")) unitPrice += 10;
                        if (material.equals("Bois")) unitPrice += 5;
                        if (material.equals("Metal") || material.equals("Métal")) unitPrice += 10;
                        if (material.equals("Plastique")) unitPrice += 3;

                        int imageRes = getImageFor(cube.getName(), material);

                        CartItem item = new CartItem(
                                cube.getName(),
                                size,
                                material,
                                quantity,
                                unitPrice,
                                imageRes
                        );

                        CartManager.addItem(item);

                        Toast.makeText(
                                v.getContext(),
                                "Added to cart",
                                Toast.LENGTH_SHORT
                        ).show();
                    });
        });
    }





    private int getImageFor(String cubeName, String material) {

        if (cubeName.equalsIgnoreCase("Red Cube")) {
            switch (material) {
                case "Bois":
                    return R.drawable.r_cube_bois;
                case "Metal":
                case "Métal":
                    return R.drawable.r_cube_metal;
                case "Plastique":
                    return R.drawable.r_cube_plastique;
                default:
                    return R.drawable.r_cube_carton;
            }
        }

        if (cubeName.equalsIgnoreCase("Green Cube")) {
            switch (material) {
                case "Bois":
                    return R.drawable.g_cube_bois;
                case "Metal":
                case "Métal":
                    return R.drawable.g_cube_metal;
                case "Plastique":
                    return R.drawable.g_cube_plastique;
                default:
                    return R.drawable.g_cube_carton;
            }
        }

        // Blue Cube
        switch (material) {
            case "Bois":
                return R.drawable.b_cube_bois;
            case "Metal":
            case "Métal":
                return R.drawable.b_cube_metal;
            case "Plastique":
                return R.drawable.b_cube_plastique;
            default:
                return R.drawable.b_cube_carton;
        }
    }


    private void updatePrice(CubeViewHolder h, CubeItem cube) {

        double price = cube.getBasePrice();

        if (h.selectedSize.equals("M")) price += 5;
        if (h.selectedSize.equals("L")) price += 10;

        String material = h.spMaterial.getSelectedItem().toString();
        if (material.equals("Bois")) price += 3;
        if (material.equals("Plastique")) price += 1;
        if (material.equals("Metal") || material.equals("Métal")) price += 8;

        double total = price * h.quantity;
        h.tvPrice.setText(String.format("%.2f DH", total));
    }

    @Override
    public int getItemCount() {
        return cubes.size();
    }

    static class CubeViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCube;
        TextView tvName, tvDescription, tvQuantity, tvPrice;
        Button btnSizeS, btnSizeM, btnSizeL, btnPlus, btnMinus, btnAddToCart;
        Spinner spMaterial;

        int quantity = 1;
        String selectedSize = "S";

        CubeViewHolder(@NonNull View v) {
            super(v);

            imgCube = v.findViewById(R.id.imgCube);
            tvName = v.findViewById(R.id.tvName);
            tvDescription = v.findViewById(R.id.tvDescription);
            tvQuantity = v.findViewById(R.id.tvQuantity);
            tvPrice = v.findViewById(R.id.tvPrice);

            btnSizeS = v.findViewById(R.id.btnSizeS);
            btnSizeM = v.findViewById(R.id.btnSizeM);
            btnSizeL = v.findViewById(R.id.btnSizeL);

            btnPlus = v.findViewById(R.id.btnPlus);
            btnMinus = v.findViewById(R.id.btnMinus);
            btnAddToCart = v.findViewById(R.id.btnAddToCart);

            spMaterial = v.findViewById(R.id.spMaterial);
        }
    }
}
