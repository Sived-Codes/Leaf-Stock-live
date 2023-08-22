package com.prashant.stockmarketadviser.util;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalPreference {

    private static final String USER_DETAILS = "INC11";
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;


    public static void storeUserType(Context context, String type) {
        sharedPref = context.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("type", type);
        editor.apply();
    }

    public static String getUserType(Context context) {
        sharedPref = context.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
        return sharedPref.getString("type", "");
    }


}
