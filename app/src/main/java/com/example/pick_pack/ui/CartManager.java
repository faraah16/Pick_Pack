package com.example.pick_pack.ui;

import android.widget.Toast;

import com.example.pick_pack.CheckoutFragment;
import com.example.pick_pack.Model.CartItem;
import com.example.pick_pack.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CartManager {

    private static final List<CartItem> cart = new ArrayList<>();

    public static void addItem(CartItem item) {
        for (CartItem c : cart) {
            if (c.getName().equals(item.getName())
                    && c.getMaterial().equals(item.getMaterial())
                    && c.getSize().equals(item.getSize())) {

                c.setQuantity(c.getQuantity() + item.getQuantity());
                return;
            }
        }
        cart.add(item);
    }

    public static List<CartItem> getCart() {
        return cart;
    }

    public static void remove(int position) {
        if (position >= 0 && position < cart.size()) {
            cart.remove(position);
        }
    }

    public static double getTotal() {
        double total = 0;
        for (CartItem item : cart) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    public static void clear() {
        cart.clear();
    }
    private static Runnable onChange;

    public static void setOnChangeListener(Runnable r) {
        onChange = r;
    }



    public static void notifyChange() {
        if (onChange != null) onChange.run();
    }






}

