package com.prashant.stockmarketadviser.activity.admin;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.prashant.stockmarketadviser.activity.admin.fragment.PrimeUserFragment;
import com.prashant.stockmarketadviser.activity.admin.fragment.TrialUserFragment;
import com.prashant.stockmarketadviser.adapter.MyPagerAdapter;
import com.prashant.stockmarketadviser.databinding.ActivityManageUserBinding;

public class ManageUserActivity extends BaseActivity {
    ActivityManageUserBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityManageUserBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());


        ViewPager2 viewPager = bind.viewPager;
        TabLayout tabLayout = bind.tabLayout;


        bind.back.setOnClickListener(view -> finish());


        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Lifecycle lifecycle = getLifecycle();

            MyPagerAdapter pagerAdapter = new MyPagerAdapter(fragmentManager, lifecycle);
            pagerAdapter.addFragment(new TrialUserFragment(), "Trial Users");
            pagerAdapter.addFragment(new PrimeUserFragment(), "Prime Users");

            viewPager.setAdapter(pagerAdapter);

            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(pagerAdapter.getPageTitle(position))).attach();
        }catch (Exception e){
            Log.e("YourTag", "An error occurred", e);

        }




    }


}