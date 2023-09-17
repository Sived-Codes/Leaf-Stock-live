package com.prashant.stockmarketadviser.ui.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.databinding.ActivityPaymentPageBinding;
import com.prashant.stockmarketadviser.databinding.ActivityPaymentPendingBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.ui.dashboard.DashboardActivity;

public class PaymentPendingActivity extends BaseActivity {

    ActivityPaymentPendingBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityPaymentPendingBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        UserModel model = AuthManager.userChecker(this);

        if (model!=null){
            if (model.getUserPlanType().equals("free")){
               Intent intent = new Intent(PaymentPendingActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        bind.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthManager.signOut(PaymentPendingActivity.this);
            }
        });

        bind.makePaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PaymentPendingActivity.this, PaymentPageActivity.class));
            }
        });
    }
}