package com.prashant.stockmarketadviser.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.prashant.stockmarketadviser.model.TipGenValueModel;

public class LocalPreference {

    private static final String USER_DETAILS = "INC11";
    private static final String GENERATED_TIP = "GTP";
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;


    public static void storeUserType(Context context, String type) {
        sharedPref = context.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("type", type);
        editor.apply();
    }

    public static String getUid(Context context) {
        sharedPref = context.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
        return sharedPref.getString("uid", "");
    }


    public static void storeGeneratedTip(Context context, String scripName, String rate, String tip, String firstTarget, String secondTarget, String thirdTarget, String firstProfit, String secondProfit, String thirdProfit) {
        sharedPref = context.getSharedPreferences(GENERATED_TIP, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("tip", tip);
        editor.putString("rate", rate);
        editor.putString("scripName", scripName);
        editor.putString("firstTarget", firstTarget);
        editor.putString("secondTarget", secondTarget);
        editor.putString("thirdTarget", thirdTarget);
        editor.putString("firstProfit", firstProfit);
        editor.putString("secondProfit", secondProfit);
        editor.putString("thirdProfit", thirdProfit);
        editor.apply();
    }

    public static TipGenValueModel getStoredTipData(Context context) {
        sharedPref = context.getSharedPreferences(GENERATED_TIP, Context.MODE_PRIVATE);

        TipGenValueModel model = new TipGenValueModel();
        model.setTip(getNonEmptyString(sharedPref.getString("tip", "")));
        model.setRate(getNonEmptyString(sharedPref.getString("rate", "")));
        model.setScripName(getNonEmptyString(sharedPref.getString("scripName", "")));
        model.setFirstTarget(getNonEmptyString(sharedPref.getString("firstTarget", "")));
        model.setSecondTarget(getNonEmptyString(sharedPref.getString("secondTarget", "")));
        model.setThirdTarget(getNonEmptyString(sharedPref.getString("thirdTarget", "")));
        model.setFirstProfit(getNonEmptyString(sharedPref.getString("firstProfit", "")));
        model.setSecondProfit(getNonEmptyString(sharedPref.getString("secondProfit", "")));
        model.setThirdProfit(getNonEmptyString(sharedPref.getString("thirdProfit", "")));

        return model;
    }


    public static void clearStoredTipData(Context context) {
        sharedPref = context.getSharedPreferences(GENERATED_TIP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.clear();
        editor.apply();

        Toast.makeText(context, "Cleared", Toast.LENGTH_SHORT).show();
    }


    private static String getNonEmptyString(String value) {
        return value != null && !value.isEmpty() ? value : null;
    }


    public static String getUserType(Context context) {
        sharedPref = context.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
        return sharedPref.getString("type", "");
    }


}
