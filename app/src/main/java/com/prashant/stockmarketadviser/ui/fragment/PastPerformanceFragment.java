package com.prashant.stockmarketadviser.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.prashant.stockmarketadviser.databinding.FragmentPastPerformanceBinding;
import com.prashant.stockmarketadviser.util.LocalPreference;


public class PastPerformanceFragment extends Fragment {

    FragmentPastPerformanceBinding bind;

    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bind = FragmentPastPerformanceBinding.inflate(inflater, container, false);
        if (LocalPreference.getUserType(mContext).equals("admin")){
            bind.adminPastPerformance.setVisibility(View.VISIBLE);
        }
        return bind.getRoot();
    }


}