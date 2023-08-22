package com.prashant.stockmarketadviser.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.prashant.stockmarketadviser.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class VUtil {

    public static void showConfirmationDialog(Context context, String message, View.OnClickListener yesClickListener, View.OnClickListener noClickListener) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_Rounded);
        View alertView = LayoutInflater.from(context).inflate(R.layout.cl_alert, null);
        builder.setView(alertView);
        builder.setCancelable(false);

        TextView text = alertView.findViewById(R.id.alert_text);
        Button yesButton = alertView.findViewById(R.id.yes_btn);
        Button noButton = alertView.findViewById(R.id.no_btn);

        text.setText(message);

        AlertDialog dialog = builder.create();
        dialog.show();

        yesButton.setOnClickListener(view -> {
            if (yesClickListener != null) {
                yesClickListener.onClick(view);
            }
            dialog.dismiss();
        });

        noButton.setOnClickListener(view -> {
            if (noClickListener != null) {
                noClickListener.onClick(view);
            }
            dialog.dismiss();
        });
    }

    public static String getCurrentDateTimeFormatted() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a - dd MMM yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
}
