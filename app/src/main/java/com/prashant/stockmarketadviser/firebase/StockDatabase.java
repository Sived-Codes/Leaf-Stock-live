package com.prashant.stockmarketadviser.firebase;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.prashant.stockmarketadviser.model.NotificationModel;
import com.prashant.stockmarketadviser.model.TipGenValueModel;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.util.LocalPreference;
import com.prashant.stockmarketadviser.util.VUtil;

public class StockDatabase {

    static String memberShip;

    public static void getTipGenBuyValue(String type, TipGenValueCallback callback) {
        Constant.tipGenDB.child(type).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    TipGenValueModel buyValueModel = snapshot.getValue(TipGenValueModel.class);
                    callback.onTipGenValueFetched(buyValueModel);
                } else {
                    callback.onFailure("Data not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public static void storeNotification(String title, String desc) {
        String uid = Constant.notificationDB.push().getKey();
        NotificationModel model = new NotificationModel();
        model.setDescription(desc);
        model.setTime(VUtil.getCurrentDateTimeFormatted());
        model.setTitle(title);
        model.setUid(uid);
        Constant.notificationDB.child(uid).setValue(model);
    }




}
