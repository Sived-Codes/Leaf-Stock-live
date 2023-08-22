package com.prashant.stockmarketadviser.ui.dashboard;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.databinding.ActivityDashboardBinding;
import com.prashant.stockmarketadviser.ui.BaseActivity;
import com.prashant.stockmarketadviser.ui.fragment.AccountFragment;
import com.prashant.stockmarketadviser.ui.fragment.FeedFragment;
import com.prashant.stockmarketadviser.ui.fragment.HomeFragment;
import com.prashant.stockmarketadviser.ui.fragment.NotificationFragment;
import com.prashant.stockmarketadviser.ui.fragment.PastPerformanceFragment;
import com.prashant.stockmarketadviser.util.VUtil;

public class DashboardActivity extends BaseActivity {

    ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


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



    @Override
    public void onBackPressed() {
        VUtil.showConfirmationDialog(DashboardActivity.this, "Are you sure want to exit !", view -> {
            finish();
            clearFromRecentTasks();
        }, view -> {

        });
    }

    private void clearFromRecentTasks() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            activityManager.getAppTasks().get(0).finishAndRemoveTask();
        }
    }

}