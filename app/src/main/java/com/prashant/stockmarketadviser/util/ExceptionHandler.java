package com.prashant.stockmarketadviser.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.CrashModel;
import com.prashant.stockmarketadviser.model.UserModel;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "ExceptionHandler";
    private final Thread.UncaughtExceptionHandler defaultExceptionHandler;

    public ExceptionHandler() {
        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread t, @NonNull Throwable e) {
        // Log the exception
        Log.e(TAG, "Uncaught exception occurred in thread " + t.getName(), e);

        // Upload the exception information to the Firebase Realtime Database
        uploadExceptionToDatabase(e);

        // Pass the exception to the default uncaught exception handler (system behavior)
        if (defaultExceptionHandler != null) {
            defaultExceptionHandler.uncaughtException(t, e);
        }
    }

    private void uploadExceptionToDatabase(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();

        String uid = Constant.crashDB.push().getKey();

        UserModel userModel = AuthManager.getUserModel();

        CrashModel model = new CrashModel();
        model.setDeviceName(VUtil.getDeviceName() + " Version " + VUtil.getDeviceVersion());
        model.setMessage(stackTrace);
        model.setUserUid(AuthManager.getUid());
        model.setTimestamp(VUtil.getCurrentDateTimeFormatted());
        model.setUserMobile(userModel.getMobile());
        model.setUserName(userModel.getFullName());
        if (uid != null) {
            Constant.crashDB.child(uid).setValue(model);
        }
    }
}
