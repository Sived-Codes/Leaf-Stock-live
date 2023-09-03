package com.prashant.stockmarketadviser.util;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.prashant.stockmarketadviser.R;

public class NetworkReceiver extends BroadcastReceiver {
    private static AlertDialog alertDialog;

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            boolean isOnline = isOnline(context);
            Log.e("net", "Online Connect Internet: " + isOnline);

            if (isOnline) {
                dismissDialog();
            } else {
                showDialog(context);
                Log.e("net", "Connectivity Failure !!! ");
            }
        } catch (NullPointerException e) {
            Log.e("Tag", "Error message", e);
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            Log.e("Tag", "Error message", e);
            return false;
        }
    }

    private void showDialog(Context context) {
        if (alertDialog == null || !alertDialog.isShowing()) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_Rounded);
            LayoutInflater inflater1 = LayoutInflater.from(context);
            View team = inflater1.inflate(R.layout.cl_network_error, null);
            builder.setView(team);
            builder.setCancelable(false);

            MaterialButton retryBtn = team.findViewById(R.id.retry);
            alertDialog = builder.create();

            retryBtn.setOnClickListener(v -> {
                alertDialog.dismiss();
                if (isOnline(context)) {
                    Toast.makeText(context, "Internet Connection is available", Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.show();
                }
            });

            try {
                alertDialog.show();
            }catch (Exception e){
                Log.d("TAG", "showDialog: "+ e);
            }

        }
    }

    private void dismissDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null; // Reset the dialog instance
        }
    }
}
