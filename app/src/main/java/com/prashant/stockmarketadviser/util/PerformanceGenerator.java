package com.prashant.stockmarketadviser.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RelativeLayout;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PerformanceGenerator {

    private static final String TAG = "PerformanceGenerator";

    public static void generatePerformance(Context context, RelativeLayout performanceView) {


        Bitmap bitmap = Bitmap.createBitmap(performanceView.getWidth(), performanceView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        performanceView.draw(canvas);

        String fileName = "stock_" + getCurrentDateTime() + ".png";

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + VUtil.getAppName(context));

        ContentResolver contentResolver = context.getContentResolver();
        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        try {

            if (imageUri == null) {
                VUtil.showErrorToast(context, "Image not found please regenerate");
                return;
            }

            OutputStream outputStream = contentResolver.openOutputStream(imageUri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            if (outputStream != null) {
                outputStream.close();
            }
            VUtil.showSuccessToast(context, "Image saved successfully");


        } catch (IOException e) {
            Log.d("TAG", "exception: " + e.getMessage());
            VUtil.showErrorToast(context, "Failed to save image");

        }
    }

    public static void uploadToFirebase(Context context, Uri imageUri, OnUploadCompleteListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference imageRef = storageRef.child("stock_images/" + imageUri.getLastPathSegment());

        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();
            Log.d(TAG, "Image uploaded successfully. Download URL: " + downloadUrl);
            listener.onUploadSuccess(downloadUrl);
        }).addOnFailureListener(e -> {
            CProgressDialog.mDismiss();
            Log.e(TAG, "Failed to get download URL: " + e.getMessage());
            listener.onUploadFailed();
        })).addOnFailureListener(e -> {
            CProgressDialog.mDismiss();
            Log.e(TAG, "Failed to upload image: " + e.getMessage());
            listener.onUploadFailed();
        });
    }

    public interface OnUploadCompleteListener {
        void onUploadSuccess(String downloadUrl);

        void onUploadFailed();
    }

    private static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
