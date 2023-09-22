package com.prashant.stockmarketadviser.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prashant.stockmarketadviser.R;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class VUtil {

    private static long lastToastTime = 0;
    private static final long TOAST_COOLDOWN_MS = 3000; // 2 seconds

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

    public static String getDateAndDay() {
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(currentDate);
    }

    public static String timeStampToFormatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a dd MMM yyyy", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    public static void EmptyViewHandler(DatabaseReference databaseReference, View emptyView, ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);

                if (dataSnapshot.exists()) {
                    emptyView.setVisibility(View.GONE);

                } else {
                    emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public static void EmptyViewUserChecker(DatabaseReference databaseReference, View emptyView, ProgressBar progressBar, String a) {
        progressBar.setVisibility(View.VISIBLE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);

                boolean hasMembershipYes = false; // Flag to track if any user has "membership" as "yes"

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String membershipValue = userSnapshot.child("memberShip").getValue(String.class);

                    if (a.equals(membershipValue)) {
                        hasMembershipYes = true;
                        break;
                    }
                }

                if (!hasMembershipYes) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    public static void copyText(Context context, EditText editText) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastToastTime < TOAST_COOLDOWN_MS) {
            return;
        }

        String textToCopy = editText.getText().toString();

        if (!textToCopy.isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", textToCopy);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            lastToastTime = currentTime; // Update last toast time
        } else {
            Toast.makeText(context, "Text is empty", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getAppName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        return packageManager.getApplicationLabel(applicationInfo).toString();
    }

    public static void showSuccessToast(Context context, String message) {
        Toasty.success(context, message).show();
    }

    public static void showErrorToast(Context context, String message) {
        Toasty.error(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showWarning(Context context, String message) {
        Toasty.warning(context, message, Toast.LENGTH_LONG).show();
    }

    public static String getDeviceName() {
        String model = android.os.Build.MODEL;
        String manufacturer = android.os.Build.MANUFACTURER;

        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        // Get the Android ID

        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getDeviceVersion() {
        // Get the Android version as a string
        return Build.VERSION.RELEASE;
    }

    public static String getRandomDp() {
        String[] dpUrls = {"https://firebasestorage.googleapis.com/v0/b/trade-master-edbae.appspot.com/o/avatar%2Fcat.png?alt=media&token=c0e7386b-8769-458d-b07b-85c62a10e487", "https://firebasestorage.googleapis.com/v0/b/trade-master-edbae.appspot.com/o/avatar%2Fdove.png?alt=media&token=632a4376-054c-43d7-8be5-01f6fb8d9bee", "https://firebasestorage.googleapis.com/v0/b/trade-master-edbae.appspot.com/o/avatar%2Feagle.png?alt=media&token=4f7e5267-6b8b-42ec-9d98-06e620b3792b", "https://firebasestorage.googleapis.com/v0/b/trade-master-edbae.appspot.com/o/avatar%2Ffox.png?alt=media&token=2089bb38-8b52-4c88-8d71-2d3fbc0f6f72", "https://firebasestorage.googleapis.com/v0/b/trade-master-edbae.appspot.com/o/avatar%2Fmacaw.png?alt=media&token=dbfec04e-45ff-4541-a631-0a00f0d81344", "https://firebasestorage.googleapis.com/v0/b/trade-master-edbae.appspot.com/o/avatar%2Fpanda.png?alt=media&token=4925adf9-51ef-4030-a9fd-436c1f04a3d9", "https://firebasestorage.googleapis.com/v0/b/trade-master-edbae.appspot.com/o/avatar%2Fpenguin.png?alt=media&token=f43f0ad1-7d79-409e-88b6-7d04b6abee45"};
        SecureRandom secureRandom = new SecureRandom();
        int randomIndex = secureRandom.nextInt(dpUrls.length);
        return dpUrls[randomIndex];
    }

    public static void clearAppDataAndRestart(Context context) {
        // Clear app data
        clearAppData(context);

        // Restart the app
        restartApp(context);
    }

    private static void clearAppData(Context context) {
        try {
            // Clear app data (including SharedPreferences)
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear " + context.getPackageName());
        } catch (Exception e) {
            Log.d("TAG", "exception: " + e.getMessage());
        }
    }

    private static void restartApp(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        }
    }

    public static void openDialer(Context context, String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            // Create an intent to open the dialer with the phone number
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + phoneNumber));

            // Start the dialer activity
            context.startActivity(dialIntent);
        }
    }

    public static void openWhatsAppNumber(Context context, String phoneNumber) {
        try {
            // Create a Uri with the "smsto" scheme and the WhatsApp phone number
            Uri uri = Uri.parse("smsto:" +"91"+ phoneNumber);

            // Create an Intent with ACTION_SENDTO to open the WhatsApp chat
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);

            // Set the package to "com.whatsapp" to ensure it opens in WhatsApp
            intent.setPackage("com.whatsapp");

            // Start the activity
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any exceptions that may occur when trying to open WhatsApp
        }
    }
}
