package com.prashant.stockmarketadviser.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.databinding.ActivityProfileViewBinding;
import com.prashant.stockmarketadviser.databinding.ActivityRegistrationBinding;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.VUtil;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfileViewActivity extends AppCompatActivity {

    ActivityProfileViewBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityProfileViewBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        String uid = getIntent().getStringExtra("uid");


        bind.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (uid != null && !uid.equals("")) {
            getUserDetail(uid);
        } else {
            VUtil.showErrorToast(ProfileViewActivity.this, "User UID is not available !");
        }

    }

    private void getUserDetail(String uid) {
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

                    bind.callBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            VUtil.openDialer(ProfileViewActivity.this, userModel.getMobile());
                        }
                    });

                    bind.subscriptionChanger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            handleUserStatusChange(isChecked);
                        }

                        private void handleUserStatusChange(boolean isChecked) {
                            CProgressDialog.mShow(ProfileViewActivity.this);
                            Map<String, Object> updates = new HashMap<>();
                            String newStatus = isChecked ? "yes" : "no";
                            updates.put("memberShip", newStatus);

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

    }
}