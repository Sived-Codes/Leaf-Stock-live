package com.prashant.stockmarketadviser.firebase;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.prashant.stockmarketadviser.model.TipGenValueModel;
import com.prashant.stockmarketadviser.ui.admin.TipGenActivity;
import com.prashant.stockmarketadviser.util.CProgressDialog;

public class StockDatabase {

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

}
