package com.prashant.stockmarketadviser.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.prashant.stockmarketadviser.adapter.FragmentAdapter;
import com.prashant.stockmarketadviser.databinding.FragmentHomeBinding;
import com.prashant.stockmarketadviser.ui.admin.TipGenActivity;
import com.prashant.stockmarketadviser.util.LocalPreference;

public class HomeFragment extends Fragment {

    private Context mContext;

    FragmentHomeBinding bind;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentHomeBinding.inflate(inflater, container, false);


        bind.adminGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, TipGenActivity.class));
            }
        });

        // Initialize the ViewPager2 and TabLayout
        ViewPager2 viewPager = bind.viewPager;
        TabLayout tabLayout = bind.tabLayout;

        // Create an adapter for the ViewPager
        FragmentAdapter fragmentAdapter = new FragmentAdapter(this);
        viewPager.setAdapter(fragmentAdapter);

        // Connect the TabLayout with the ViewPager
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Trial Tips");
                    } else if (position == 1) {
                        tab.setText("Prime Tips");
                    }
                    // Add more tabs as needed
                });
        tabLayoutMediator.attach();

        if (LocalPreference.getUserType(mContext).equals("admin")){
            bind.adminGen.setVisibility(View.VISIBLE);
        }

        return bind.getRoot();
    }
}
