package com.prashant.stockmarketadviser.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.prashant.stockmarketadviser.databinding.ActivityLoginBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.ui.BaseActivity;
import com.prashant.stockmarketadviser.ui.admin.AddFeedActivity;
import com.prashant.stockmarketadviser.ui.dashboard.DashboardActivity;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.LocalPreference;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        boolean isLoggedIn = new AuthManager().isUserLoggedIn();
        if (isLoggedIn) {
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
        }


        binding.createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });


        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.loginEmailEd.getText().toString().trim();
                String password = binding.loginPasswordEd.getText().toString();

                if (!email.endsWith("@gmail.com") && !email.endsWith("@yahoo.com") && !email.endsWith("@outlook.com") && !email.endsWith("@hotmail.com")) {
                    binding.loginEmailEd.setError("Invalid email domain");
                    binding.loginEmailEd.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    binding.loginEmailEd.setError("Email address cannot be empty");
                    binding.loginEmailEd.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    binding.loginPasswordEd.setError("Password cannot be empty");
                    binding.loginPasswordEd.requestFocus();
                    return;
                }

                CProgressDialog.mShow(LoginActivity.this);
                new AuthManager().signInUser(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Constant.userDB.child(AuthManager.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {

                                        FirebaseMessaging.getInstance().subscribeToTopic("allUsers")
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(LoginActivity.this, "Topic Subscribed", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(LoginActivity.this, "Subscription failed", Toast.LENGTH_SHORT).show();

                                                });


                                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {

                                                Map<String, Object> token = new HashMap<>();
                                                token.put("firebaseToken",  task.getResult().toString());

                                                Constant.userDB.child( AuthManager.getUid()).updateChildren(token).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()){
                                                            String userType = snapshot.child("userType").getValue(String.class);
                                                            LocalPreference.storeUserType(LoginActivity.this, userType);

                                                            CProgressDialog.mDismiss();
                                                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                        }else{
                                                            CProgressDialog.mDismiss();
                                                            Toast.makeText(LoginActivity.this, "Please try again ! ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            } else {
                                                CProgressDialog.mDismiss();
                                                Log.w("FCM TOKEN", "Fetching FCM registration token failed", task.getException());
                                            }
                                        });


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LoginActivity.this, error + "", Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            CProgressDialog.mDismiss();
                            Toast.makeText(LoginActivity.this, "Failed to login please try again !", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CProgressDialog.mDismiss();
                        Toast.makeText(LoginActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void addFirebaseToken(String uid) {

    }
}