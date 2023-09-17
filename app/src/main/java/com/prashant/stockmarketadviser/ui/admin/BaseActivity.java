package com.prashant.stockmarketadviser.ui.admin;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.ui.dashboard.DashboardActivity;
import com.prashant.stockmarketadviser.util.NetworkReceiver;

public class BaseActivity extends AppCompatActivity {

    private final NetworkReceiver mNetworkReceiver = new NetworkReceiver();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        UserModel model = AuthManager.userChecker(this);

        if (model!=null){
            if (model.getUserPlanType().equals("free")){
                Intent intent = new Intent(BaseActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (model.getUserPlanType().equals("paid") && model.getPaymentStatus().equals("pending")) {
                Intent intent = new Intent(BaseActivity.this, PaymentPendingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mNetworkReceiver);
    }

}
