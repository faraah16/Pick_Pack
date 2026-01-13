package com.example.pick_pack;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pick_pack.Adapter.CartAdapter;
import com.example.pick_pack.Model.CartItem;
import com.example.pick_pack.ui.CartManager;
import com.google.firebase.firestore.FirebaseFirestore;

public class CartFragment extends Fragment {

    private CartAdapter adapter;
    private TextView tvTotal;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View v = inflater.inflate(R.layout.fragment_cart, container, false);
        Button btnBack = v.findViewById(R.id.btnBack); // ✅ ICI

        btnBack.setOnClickListener(view -> {
            requireActivity().finish();
        });
        v.findViewById(R.id.btnBack).setOnClickListener(view ->
                requireActivity().onBackPressed()
        );


        // ================= RecyclerView =================
        RecyclerView rv = v.findViewById(R.id.rvCart);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CartAdapter(CartManager.getCart());
        rv.setAdapter(adapter);

        // ================= TOTAL =================
        tvTotal = v.findViewById(R.id.tvTotal);
        updateTotal();

        // écouter changements + / -
        CartManager.setOnChangeListener(this::updateTotal);



        // ================= BUY =================
        Button btnBuy = v.findViewById(R.id.btnBuy);
        btnBuy.setOnClickListener(x -> {

            if (CartManager.getCart().isEmpty()) {
                Toast.makeText(
                        getContext(),
                        "Cart is empty",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            checkNextItemStock(db, 0);
        });



        // ================= SWIPE =================
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
                ) {
                    @Override
                    public boolean onMove(
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            @NonNull RecyclerView.ViewHolder target
                    ) {
                        return false;
                    }

                    @Override
                    public void onSwiped(
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            int direction
                    ) {

                        int pos = viewHolder.getAdapterPosition();

                        // ❌ SUPPRIMER
                        if (direction == ItemTouchHelper.LEFT) {
                            CartManager.remove(pos);
                            adapter.notifyItemRemoved(pos);
                            updateTotal();
                        }

                        // ✏️ MODIFIER
                        if (direction == ItemTouchHelper.RIGHT) {
                            showEditDialog(pos);
                            adapter.notifyItemChanged(pos);
                        }
                    }
                }
        );

        helper.attachToRecyclerView(rv);

        return v;
    }

    // =================================================
    // 🔧 EDIT DIALOG (SWIPE DROITE)
    // =================================================
    private void showEditDialog(int position) {

        CartItem item = CartManager.getCart().get(position);

        View v = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_edit_cart, null);

        Spinner spMaterial = v.findViewById(R.id.spMaterial);
        Spinner spSize = v.findViewById(R.id.spSize);
        EditText etQty = v.findViewById(R.id.etQuantity);

        // valeurs actuelles
        etQty.setText(String.valueOf(item.getQuantity()));

        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"S", "M", "L"}
        );
        spSize.setAdapter(sizeAdapter);

        ArrayAdapter<String> materialAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Bois", "Carton", "Métal", "Plastique"}
        );
        spMaterial.setAdapter(materialAdapter);

        spSize.setSelection(sizeAdapter.getPosition(item.getSize()));
        spMaterial.setSelection(materialAdapter.getPosition(item.getMaterial()));

        new AlertDialog.Builder(getContext())
                .setTitle("Modify item")
                .setView(v)
                .setPositiveButton("Save", (d, w) -> {

                    int newQty = Integer.parseInt(etQty.getText().toString());
                    String newMat = spMaterial.getSelectedItem().toString();
                    String newSize = spSize.getSelectedItem().toString();

                    item.setQuantity(newQty);
                    item.setMaterial(newMat);
                    item.setSize(newSize);

                    adapter.notifyItemChanged(position);
                    updateTotal();
                })
                .setNegativeButton("Cancel", (d, w) ->
                        adapter.notifyItemChanged(position)
                )
                .show();
    }

    private void checkNextItemStock(FirebaseFirestore db, int index) {

        if (index >= CartManager.getCart().size()) {
            // ✅ TOUT EST OK → CHECKOUT
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.cart_fragment_container,
                            new CheckoutFragment()
                    )
                    .addToBackStack(null)
                    .commit();
            return;
        }

        CartItem item = CartManager.getCart().get(index);

        String stockId;
        if (item.getName().equalsIgnoreCase("Red Cube")) {
            stockId = "cube_rouge";
        } else if (item.getName().equalsIgnoreCase("Green Cube")) {
            stockId = "cube_vert";
        } else {
            stockId = "cube_bleu";
        }

        String rawMat = item.getMaterial().toLowerCase();

        final String mat;
        if (rawMat.equals("métal")) {
            mat = "metal";
        } else {
            mat = rawMat;
        }


        db.collection("stock")
                .document(stockId)
                .get()
                .addOnSuccessListener(doc -> {

                    Long currentObj = doc.getLong(mat);

                    if (currentObj == null || currentObj < item.getQuantity()) {
                        Toast.makeText(
                                getContext(),
                                item.getName() + " " + item.getMaterial() + " rupture de stock",
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    // 🔁 vérifier l’item suivant
                    checkNextItemStock(db, index + 1);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Stock check failed",
                                Toast.LENGTH_SHORT
                        ).show()
                );

    }


    // =================================================
    // TOTAL
    // =================================================
    private void updateTotal() {
        tvTotal.setText(
                "Total à payer : " +
                        String.format("%.2f DH", CartManager.getTotal())
        );
    }
}
