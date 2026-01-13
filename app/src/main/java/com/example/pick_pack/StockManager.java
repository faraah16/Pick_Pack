package com.example.pick_pack;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StockManager {

    private static final Map<String, Map<String, Integer>> stock = new HashMap<>();
    private static Context appContext;

    // ⚠️ APPEL OBLIGATOIRE AU DÉMARRAGE
    public static void init(Context context) {
        appContext = context.getApplicationContext();
        loadStock();
    }

    // 🔹 INITIALISATION PAR DÉFAUT
    private static void initStock() {
        addColor("Rouge", 2, 3, 2, 3);
        addColor("Bleu", 1, 2, 2, 2);
        addColor("Vert", 1, 1, 1, 2);
        saveStock();
    }

    private static void addColor(String color, int bois, int plastique, int metal, int carton) {
        Map<String, Integer> materials = new HashMap<>();
        materials.put("Bois", bois);
        materials.put("Plastique", plastique);
        materials.put("Metal", metal);
        materials.put("Carton", carton);
        stock.put(color, materials);
    }

    // 🔹 LECTURE
    public static Map<String, Map<String, Integer>> getStock() {
        return stock;
    }

    public static int getTotalForColor(String color) {
        int total = 0;
        Map<String, Integer> mats = stock.get(color);
        if (mats != null) {
            for (int q : mats.values()) total += q;
        }
        return total;
    }

    public static boolean isOutOfStock(String color) {
        return getTotalForColor(color) == 0;
    }

    // ➕ AJOUT (EMPLOYÉ)
    public static void addQuantity(String color, String material, int quantity) {
        Map<String, Integer> mats = stock.get(color);
        if (mats == null) return;

        int current = mats.get(material);
        mats.put(material, current + quantity);
        saveStock();
    }

    // ➖ RETRAIT (CLIENT / COMMANDE)
    public static boolean removeQuantity(String color, String material, int quantity) {
        Map<String, Integer> mats = stock.get(color);
        if (mats == null) return false;

        int current = mats.get(material);
        if (current < quantity) return false;

        mats.put(material, current - quantity);
        saveStock();
        return true;
    }

    // 💾 SAUVEGARDE
    private static void saveStock() {
        if (appContext == null) return;

        SharedPreferences prefs = appContext.getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        try {
            JSONObject root = new JSONObject();

            for (String color : stock.keySet()) {
                JSONObject mats = new JSONObject(stock.get(color));
                root.put(color, mats);
            }

            editor.putString("stock_data", root.toString());
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔄 CHARGEMENT
    private static void loadStock() {
        if (appContext == null) return;

        SharedPreferences prefs = appContext.getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
        String json = prefs.getString("stock_data", null);

        if (json == null) {
            initStock();
            return;
        }

        try {
            JSONObject root = new JSONObject(json);
            stock.clear();

            Iterator<String> colors = root.keys();
            while (colors.hasNext()) {
                String color = colors.next();
                JSONObject matsJson = root.getJSONObject(color);

                Map<String, Integer> mats = new HashMap<>();
                Iterator<String> matsKeys = matsJson.keys();

                while (matsKeys.hasNext()) {
                    String mat = matsKeys.next();
                    mats.put(mat, matsJson.getInt(mat));
                }

                stock.put(color, mats);
            }
        } catch (Exception e) {
            e.printStackTrace();
            initStock();
        }
    }
}
