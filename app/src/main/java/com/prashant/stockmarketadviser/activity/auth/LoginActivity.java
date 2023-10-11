package com.prashant.stockmarketadviser.activity.auth;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.prashant.stockmarketadviser.databinding.ActivityLoginBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.activity.admin.BaseActivity;
import com.prashant.stockmarketadviser.activity.admin.PrivacyPolicyActivity;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.VUtil;

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        if (AuthManager.isUserLoggedIn()) {
            AuthManager.handleUserLogin(this);
        }else{
            CProgressDialog.mDismiss();
            binding.loginLayout.setVisibility(View.VISIBLE);
        }

        binding.forgotBtn.setOnClickListener(view -> {
            Intent intent =new Intent(LoginActivity.this, PasswordManagerActivity.class);
            intent.putExtra("actionType","forgot");
            startActivity(intent);
        });

        binding.privacyBtn.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, PrivacyPolicyActivity.class)));


        binding.createAccountBtn.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));

        binding.loginBtn.setOnClickListener(view -> {
            String email = binding.loginEmailEd.getText().toString().trim();
            String password = binding.loginPasswordEd.getText().toString();

            if (email.isEmpty()) {
                binding.loginEmailEd.setError("Please enter email !");
                binding.loginEmailEd.requestFocus();
                return;
            }


            if (!email.endsWith("@gmail.com") && !email.endsWith("@yahoo.com") && !email.endsWith("@outlook.com") && !email.endsWith("@hotmail.com")) {
                binding.loginEmailEd.setError("Invalid email");
                binding.loginEmailEd.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                binding.loginPasswordEd.setError("Password cannot be empty");
                binding.loginPasswordEd.requestFocus();
                return;
            }

            try {
                AuthManager.userLogin(email, password , LoginActivity.this);

            }catch (Exception e){
                VUtil.showErrorToast(LoginActivity.this, e.getMessage());
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CProgressDialog.mDismiss();
    }
}