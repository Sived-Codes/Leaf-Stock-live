package com.prashant.stockmarketadviser.adapter;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class FragmentAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragments;
    private final List<String> tabNames;

    public FragmentAdapter(Fragment fragment, List<Fragment> fragments, List<String> tabNames) {
        super(fragment);
        this.fragments = fragments;
        this.tabNames = tabNames;
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    @Override
    public Fragment createFragment(int position) {
        if (position >= 0 && position < fragments.size()) {
            return fragments.get(position);
        } else {
            return fragments.get(0);
        }
    }


    public String getTabName(int position) {
        if (position >= 0 && position < tabNames.size()) {
            return tabNames.get(position);
        }
        return "";
    }
}
