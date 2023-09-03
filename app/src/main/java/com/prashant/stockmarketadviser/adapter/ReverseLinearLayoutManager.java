package com.prashant.stockmarketadviser.adapter;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class ReverseLinearLayoutManager extends LinearLayoutManager {
    public ReverseLinearLayoutManager(Context context) {
        super(context);
        setReverseLayout(true);
        setStackFromEnd(true);
    }
}
