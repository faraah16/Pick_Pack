package com.example.pick_pack;

import android.content.Context;
import android.content.SharedPreferences;

public class ClientInfoManager {

    private static final String PREF_NAME = "client_info";

    // 🔹 Supprimer toutes les infos client sauvegardées
    public static void clearClientInfo(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
