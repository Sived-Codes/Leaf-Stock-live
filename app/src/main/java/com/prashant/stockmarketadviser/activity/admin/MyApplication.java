package com.prashant.stockmarketadviser.activity.admin;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.util.ExceptionHandler;

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

    }

}
