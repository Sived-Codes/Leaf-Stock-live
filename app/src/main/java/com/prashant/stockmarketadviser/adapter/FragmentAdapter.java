package com.prashant.stockmarketadviser.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.prashant.stockmarketadviser.ui.fragment.PrimeFragment;
import com.prashant.stockmarketadviser.ui.fragment.TrialFragment;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(Fragment fragment) {
        super(fragment);
    }

    @Override
    public int getItemCount() {
        return 2; // Number of tabs
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new TrialFragment(); // Replace with your actual fragment class
        } else if (position == 1) {
            return new PrimeFragment(); // Replace with your actual fragment class
        }
        // Add more conditions and fragments as needed
        return null;
    }
}
