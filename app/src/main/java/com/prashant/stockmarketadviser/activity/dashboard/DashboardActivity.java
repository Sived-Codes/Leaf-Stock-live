package com.prashant.stockmarketadviser.activity.dashboard;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.internal.Sleeper;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.activity.admin.BaseActivity;
import com.prashant.stockmarketadviser.activity.fragment.AccountFragment;
import com.prashant.stockmarketadviser.activity.fragment.FeedFragment;
import com.prashant.stockmarketadviser.activity.fragment.HomeFragment;
import com.prashant.stockmarketadviser.activity.fragment.NotificationFragment;
import com.prashant.stockmarketadviser.activity.fragment.PastPerformanceFragment;
import com.prashant.stockmarketadviser.databinding.ActivityDashboardBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.firebase.UpdateChecker;
import com.prashant.stockmarketadviser.util.AsyncTaskHelper;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.VUtil;

import java.util.Objects;

public class DashboardActivity extends BaseActivity {

    ActivityDashboardBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
         String myDeviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        UpdateChecker.checkForUpdates(this);

         if (auth!=null){

             AsyncTaskHelper.runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     Constant.userDB.child(Objects.requireNonNull(auth.getUid())).addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                             String deviceId = snapshot.child("deviceId").getValue(String.class);

                             try {
                                 if (deviceId !=null && myDeviceId !=null){
                                     if (!deviceId.equals(myDeviceId)){
                                         AuthManager.signOut(DashboardActivity.this);
                                     }
                                 }
                             }catch (Exception e){

                             }


                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError error) {

                         }
                     });

                 }
             });

         }
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == binding.bottomNavigation.getMenu().findItem(R.id.menu_home_fragment).getItemId()) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == binding.bottomNavigation.getMenu().findItem(R.id.menu_performance_fragment).getItemId()) {
                selectedFragment = new PastPerformanceFragment();
            } else if (item.getItemId() == binding.bottomNavigation.getMenu().findItem(R.id.menu_feed_fragment).getItemId()) {
                selectedFragment = new FeedFragment();
            } else if (item.getItemId() == binding.bottomNavigation.getMenu().findItem(R.id.menu_notification_fragment).getItemId()) {
                selectedFragment = new NotificationFragment();
            } else if (item.getItemId() == binding.bottomNavigation.getMenu().findItem(R.id.menu_account_fragment).getItemId()) {
                selectedFragment = new AccountFragment();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

            if (currentFragment != null && selectedFragment != null && currentFragment.getClass().equals(selectedFragment.getClass())) {
                return true;
            }

            FragmentTransaction transaction = fragmentManager.beginTransaction();

            if (selectedFragment != null) {
                transaction.replace(R.id.fragment_container, selectedFragment);
            } else {
                transaction.replace(R.id.fragment_container, new HomeFragment());
            }
            transaction.commitAllowingStateLoss();


            return true;
        });

        binding.bottomNavigation.setSelectedItemId(R.id.menu_home_fragment);
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        binding.bottomNavigation.setVisibility(View.VISIBLE);

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            VUtil.showConfirmationDialog(DashboardActivity.this, "Are you sure want to exit !", view -> {
                finish();
                clearFromRecentTasks();
            }, view -> {

            });
        }

    }

    private void clearFromRecentTasks() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            activityManager.getAppTasks().get(0).finishAndRemoveTask();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CProgressDialog.mDismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UpdateChecker.UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                // Handle update failure or cancellation
                Log.d("TAG", "onActivityResult: update failure or cancellation");
            }
        }

    }
}