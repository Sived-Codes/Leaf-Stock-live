package com.prashant.stockmarketadviser.activity.auth;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prashant.stockmarketadviser.activity.admin.BaseActivity;
import com.prashant.stockmarketadviser.databinding.ActivityPasswordManagerBinding;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.VUtil;

import java.util.Objects;

public class PasswordManagerActivity extends BaseActivity {

    ActivityPasswordManagerBinding bind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityPasswordManagerBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        String action = getIntent().getStringExtra("actionType");

        if (action != null && action.equals("forgot")) {
            bind.actionBarTitle.setText("Forgot Password");
            bind.recoverPasswordLayout.setVisibility(View.VISIBLE);
        } else {
            bind.changePasswordLayout.setVisibility(View.VISIBLE);

        }

        bind.back.setOnClickListener(view -> finish());

        bind.sendPasswordLinkBtn.setOnClickListener(view -> {
            CProgressDialog.mShow(PasswordManagerActivity.this);
            String email = Objects.requireNonNull(bind.registerEmailEditText.getText()).toString();
            if (email.isEmpty()) {
                VUtil.showWarning(PasswordManagerActivity.this, "Please enter email address !");
            } else {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CProgressDialog.mDismiss();
                        VUtil.showSuccessToast(PasswordManagerActivity.this, "Success! Password Reset Link Sent to Your Email");

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_APP_EMAIL);

                        if (intent.resolveActivity(getPackageManager()) != null) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                }).addOnFailureListener(e -> {
                    CProgressDialog.mDismiss();
                    VUtil.showErrorToast(PasswordManagerActivity.this, e.getMessage());
                });
            }
        });

        bind.changePassword.setOnClickListener(view -> {
            CProgressDialog.mShow(PasswordManagerActivity.this);

            String password =String.valueOf(bind.updateNpassword.getText());

            String cPassword = String.valueOf(bind.updateCpassword.getText());
            String currentPassword = String.valueOf(bind.currentPassword.getText());

            if (currentPassword.isEmpty()) {
                CProgressDialog.mDismiss();
                bind.currentPassword.requestFocus();
                bind.currentPassword.setError("Empty");
            } else if (password.isEmpty()) {
                CProgressDialog.mDismiss();
                bind.updateNpassword.requestFocus();
                bind.updateNpassword.setError("Empty");
            } else if (cPassword.isEmpty()) {
                CProgressDialog.mDismiss();
                bind.updateCpassword.requestFocus();
                bind.updateCpassword.setError("Empty");
            } else if (!password.equals(cPassword)) {
                CProgressDialog.mDismiss();
                VUtil.showWarning(PasswordManagerActivity.this, "Password not matched !");
            } else {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null && user.getEmail() != null) {

                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(password).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    CProgressDialog.mDismiss();
                                    VUtil.showSuccessToast(PasswordManagerActivity.this, "Password has been changed");
                                    finish();
                                }
                            }).addOnFailureListener(e -> {
                                CProgressDialog.mDismiss();
                                VUtil.showErrorToast(PasswordManagerActivity.this, "Current password is invalid !");
                            });
                        }
                    }).addOnFailureListener(e -> {
                        CProgressDialog.mDismiss();
                        VUtil.showErrorToast(PasswordManagerActivity.this, e.getMessage());
                    });

                }


            }


        });
    }
}