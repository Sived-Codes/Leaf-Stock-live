package com.prashant.stockmarketadviser.ui.auth;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.databinding.ActivityLoginBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.ui.admin.BaseActivity;
import com.prashant.stockmarketadviser.ui.admin.PaymentPageActivity;
import com.prashant.stockmarketadviser.ui.admin.PrivacyPolicyActivity;
import com.prashant.stockmarketadviser.ui.dashboard.DashboardActivity;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.LocalPreference;
import com.prashant.stockmarketadviser.util.VUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
            VUtil.showWarning(LoginActivity.this, "Coming soon !");
        });

        binding.privacyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, PrivacyPolicyActivity.class));
            }
        });

        binding.loginPasswordEd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_remove_red_eye_24, 0);

        binding.loginPasswordEd.setOnTouchListener(new View.OnTouchListener() {
            boolean isPasswordVisible = false;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Drawable drawableEnd = binding.loginPasswordEd.getCompoundDrawables()[2];
                    if (motionEvent.getRawX() >= (binding.loginPasswordEd.getRight() - drawableEnd.getBounds().width())) {
                        // Drawable end has been clicked
                        isPasswordVisible = !isPasswordVisible;

                        if (isPasswordVisible) {
                            // Show password
                            binding.loginPasswordEd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            drawableEnd = ContextCompat.getDrawable(view.getContext(), R.drawable.baseline_visibility_off_24);
                        } else {
                            // Hide password
                            binding.loginPasswordEd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            drawableEnd = ContextCompat.getDrawable(view.getContext(), R.drawable.baseline_remove_red_eye_24);
                        }

                        binding.loginPasswordEd.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableEnd, null);
                        binding.loginEmailEd.clearFocus(); // Remove focus from email EditText

                        return true;
                    }
                }
                return false;
            }
        });


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