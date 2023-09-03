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
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.ui.admin.TipGenActivity;
import com.prashant.stockmarketadviser.ui.chat.ChatListActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Context mContext;

    FragmentHomeBinding bind;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentHomeBinding.inflate(inflater, container, false);


        if (AuthManager.isAdmin()) {
            bind.adminGen.setVisibility(View.VISIBLE);
        }


        bind.adminGen.setOnClickListener(view -> startActivity(new Intent(mContext, TipGenActivity.class)));

        bind.chatBtn.setOnClickListener(view -> startActivity(new Intent(mContext, ChatListActivity.class)));

        ViewPager2 viewPager = bind.viewPager;
        TabLayout tabLayout = bind.tabLayout;

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new TrialFragment());
        fragmentList.add(new PrimeFragment());

        List<String> tabNames = new ArrayList<>();
        tabNames.add("Trial Scrip");
        tabNames.add("Prime Scrip");

        FragmentAdapter adapter = new FragmentAdapter(this, fragmentList, tabNames); // 'this' is your fragment or activity

        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(adapter.getTabName(position))).attach();

        return bind.getRoot();
    }
}
