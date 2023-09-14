package com.prashant.stockmarketadviser.ui.admin;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.prashant.stockmarketadviser.databinding.ActivityAppCrashBinding;

public class AppCrashActivity extends AppCompatActivity {

    ActivityAppCrashBinding bind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityAppCrashBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        bind.back.setOnClickListener(view -> finish());

    }
}