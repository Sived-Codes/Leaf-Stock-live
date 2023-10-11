package com.prashant.stockmarketadviser.activity.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.databinding.ActivityProfileViewBinding;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.VUtil;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfileViewActivity extends BaseActivity {

    ActivityProfileViewBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityProfileViewBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        String uid = getIntent().getStringExtra("uid");


        bind.back.setOnClickListener(view -> finish());

        if (uid != null && !uid.equals("")) {
            getUserDetail(uid);
        } else {
            VUtil.showErrorToast(ProfileViewActivity.this, "User UID is not available !");
        }

    }

    private void getUserDetail(String uid) {

        try {
            Constant.userDB.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel userModel = snapshot.getValue(UserModel.class);


                    if (userModel != null) {

                        if (bind == null) {
                            return;
                        }
                        bind.mainAccountLayout.setVisibility(View.VISIBLE);
                        bind.progressBar.show.setVisibility(View.GONE);


                        bind.disableFromServer.setChecked(true);


                        bind.userName.setText(userModel.getFullName());
                        Picasso.get().load(userModel.getUserImage()).placeholder(R.drawable.baseline_account_circle_24).into(bind.userImg);
                        bind.userMobile.setText(userModel.getMobile());
                        bind.userPlan.setText(userModel.getUserPlan());
                        bind.userMail.setText(userModel.getEmail());
                        bind.userDevice.setText(userModel.getDeviceName());
                        bind.userRegistrationDate.setText(userModel.getRegistrationDate());


                        if (userModel.getMemberShip().equals("yes")) {
                            bind.subscriptionChanger.setChecked(true);
                        }

                        bind.callBtn.setOnClickListener(view -> VUtil.openDialer(ProfileViewActivity.this, userModel.getMobile()));

                        bind.subscriptionChanger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                handleUserStatusChange(isChecked);
                            }

                            private void handleUserStatusChange(boolean isChecked) {
                                CProgressDialog.mShow(ProfileViewActivity.this);
                                Map<String, Object> updates = new HashMap<>();
                                String newStatus = isChecked ? "yes" : "no";
                                String planType = isChecked ? "paid" : "free";
                                String paymentStatus = isChecked ? "completed" : "pending";
                                updates.put("memberShip", newStatus);
                                updates.put("userPlanType", planType);
                                updates.put("paymentStatus", paymentStatus);

                                Constant.userDB.child(uid).updateChildren(updates).addOnCompleteListener(task -> {
                                    CProgressDialog.mDismiss();
                                    VUtil.showSuccessToast(ProfileViewActivity.this, "User Membership has been updated successfully.");
                                }).addOnFailureListener(e -> {
                                    CProgressDialog.mDismiss();
                                    VUtil.showErrorToast(ProfileViewActivity.this, e.getMessage());
                                });

                            }
                        });


                    } else {
                        VUtil.showWarning(ProfileViewActivity.this, "Model is null");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    VUtil.showErrorToast(ProfileViewActivity.this, error.getMessage());

                }
            });

        }catch (Exception e){
            Log.e("YourTag", "An error occurred", e);

        }


    }
}