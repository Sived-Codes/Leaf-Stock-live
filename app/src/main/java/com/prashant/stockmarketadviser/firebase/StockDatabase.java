package com.prashant.stockmarketadviser.firebase;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.prashant.stockmarketadviser.model.NotificationModel;
import com.prashant.stockmarketadviser.model.TipGenValueModel;
import com.prashant.stockmarketadviser.util.AsyncTaskHelper;
import com.prashant.stockmarketadviser.util.VUtil;

public class StockDatabase {

    static String qrCodeImgUrl;

    public static void getTipGenBuyValue(String type, TipGenValueCallback callback) {

        AsyncTaskHelper.runInBackground(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    public static void storeNotification(String title, String desc) {
        String uid = Constant.notificationDB.push().getKey();
        NotificationModel model = new NotificationModel();
        model.setDescription(desc);
        model.setTime(VUtil.getCurrentDateTimeFormatted());
        model.setTitle(title);
        model.setUid(uid);
        if (uid != null) {
            Constant.notificationDB.child(uid).setValue(model);
        }
    }

    public static String getQrUrl(Context context) {

        AsyncTaskHelper.runInBackground(new Runnable() {
            @Override
            public void run() {
                Constant.adminDB.child("QrCode").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        qrCodeImgUrl = snapshot.child("qr_img_url").getValue(String.class);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        VUtil.showErrorToast(context, error.getMessage());
                    }
                });

            }
        });

        return qrCodeImgUrl;
    }

}
