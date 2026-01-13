package com.example.pick_pack;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderManager {

    private static final Map<String, TrackedOrder> orders = new HashMap<>();

    // ✅ PREFS (au bon endroit)
    private static final String PREFS_NAME = "orders_storage";
    private static final String KEY_ORDERS = "orders_map";

    // =========================
    // MODELE
    // =========================
    public static class TrackedOrder {
        public String orderId;
        public String delivery;   // Express / Normal / Point Relais
        public double total;
        public String status;     // Pending / Preparing / Delivered
        public List<OrderItem> items;

        public TrackedOrder(String orderId,
                            String delivery,
                            double total,
                            String status,
                            List<OrderItem> items) {
            this.orderId = orderId;
            this.delivery = delivery;
            this.total = total;
            this.status = status;
            this.items = items;
        }
    }

    // =========================
    // ✅ SAVE / LOAD PREFS (correct)
    // =========================
    public static void saveOrdersToPrefs(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = gson.toJson(orders);

        prefs.edit().putString(KEY_ORDERS, json).apply();
    }

    public static void loadOrdersFromPrefs(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String json = prefs.getString(KEY_ORDERS, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, TrackedOrder>>() {}.getType();
            Map<String, TrackedOrder> savedOrders = gson.fromJson(json, type);

            orders.clear();
            if (savedOrders != null) {
                orders.putAll(savedOrders);
            }
        }
    }

    // =========================
    // SAVE ORDER (NE PAS CASSER)
    // =========================
    public static void saveOrder(String orderId, String delivery, double total, List<OrderItem> items) {
        orders.put(orderId,
                new TrackedOrder(orderId, delivery, total, "Pending", items)
        );
    }

    // SAVE ORDER AVEC STATUS (optionnel)
    public static void saveOrder(String orderId, String delivery, double total, String status, List<OrderItem> items) {
        orders.put(orderId, new TrackedOrder(orderId, delivery, total, status, items));
    }

    // GET ORDER
    public static TrackedOrder getOrder(String orderId) {
        return orders.get(orderId);
    }

    // UPDATE STATUS
    public static void updateStatus(String orderId, String newStatus) {
        TrackedOrder order = orders.get(orderId);
        if (order != null) {
            order.status = newStatus;
        }
    }

    // ======================================================
    // POUR EMPLOYEE DASHBOARD
    // ======================================================
    public static List<TrackedOrder> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public static List<TrackedOrder> getOrdersByDelivery(String deliveryType) {
        List<TrackedOrder> result = new ArrayList<>();

        for (TrackedOrder o : orders.values()) {

            // ❌ NE PLUS AFFICHER LES COMMANDES LIVRÉES CÔTÉ EMPLOYÉ
            if (o != null && "Delivered".equalsIgnoreCase(o.status)) {
                continue; // on saute cette commande
            }

            if (o != null && o.delivery != null
                    && o.delivery.equalsIgnoreCase(deliveryType)) {
                result.add(o);
            }
        }

        return result;
    }


    public static class DeliveryStats {
        public int total;
        public int express;
        public int normal;
        public int pointRelais;

        public int getExpressPercent() { return total == 0 ? 0 : (express * 100 / total); }
        public int getNormalPercent()  { return total == 0 ? 0 : (normal  * 100 / total); }
        public int getPointPercent()   { return total == 0 ? 0 : (pointRelais * 100 / total); }
    }







    public static void removeOrder(String orderId) {
        orders.remove(orderId);
    }


    public static DeliveryStats getDeliveryStats() {
        DeliveryStats s = new DeliveryStats();
        s.total = orders.size();

        for (TrackedOrder o : orders.values()) {
            if (o == null || o.delivery == null) continue;

            String d = o.delivery.trim().toLowerCase();
            if (d.equals("express")) s.express++;
            else if (d.equals("normal")) s.normal++;
            else if (d.equals("point relais") || d.equals("point") || d.equals("pointrelai") || d.equals("point relai"))
                s.pointRelais++;
        }
        return s;
    }

    public static void updateOrderStatus(
            Context context,
            String orderId,
            String newStatus
    ) {
        TrackedOrder o = orders.get(orderId);
        if (o != null) {
            o.status = newStatus;
            saveOrdersToPrefs(context);
        }
    }

}
