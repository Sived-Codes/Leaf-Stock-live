package com.prashant.stockmarketadviser.ui.admin;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.prashant.stockmarketadviser.adapter.MyPagerAdapter;
import com.prashant.stockmarketadviser.databinding.ActivityManageUserBinding;
import com.prashant.stockmarketadviser.ui.admin.fragment.PrimeUserFragment;
import com.prashant.stockmarketadviser.ui.admin.fragment.TrialUserFragment;

public class ManageUserActivity extends AppCompatActivity {
    ActivityManageUserBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityManageUserBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());


        ViewPager2 viewPager = bind.viewPager;
        TabLayout tabLayout = bind.tabLayout;


        bind.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        Lifecycle lifecycle = getLifecycle();

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(fragmentManager, lifecycle);
        pagerAdapter.addFragment(new TrialUserFragment(), "Trial Users");
        pagerAdapter.addFragment(new PrimeUserFragment(), "Prime Users");

        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(pagerAdapter.getPageTitle(position))).attach();



    }


}