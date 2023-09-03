package com.prashant.stockmarketadviser.ui.admin;

import android.app.Application;

import com.prashant.stockmarketadviser.util.ExceptionHandler;

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

    }

}