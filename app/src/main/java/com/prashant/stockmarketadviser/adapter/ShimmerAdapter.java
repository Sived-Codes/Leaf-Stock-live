package com.prashant.stockmarketadviser.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShimmerAdapter extends RecyclerView.Adapter<ShimmerAdapter.ShimmerViewHolder> {

    private int shimmerItemCount = 10; // Number of shimmer items to display
    private int layoutResourceId; // Layout resource for shimmer item

    public ShimmerAdapter(int layoutResourceId) {
        this.layoutResourceId = layoutResourceId;
    }

    @NonNull
    @Override
    public ShimmerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layoutResourceId, parent, false);
        return new ShimmerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShimmerViewHolder holder, int position) {
        // No need to bind actual data for shimmer effect
    }

    @Override
    public int getItemCount() {
        return shimmerItemCount;
    }

    static class ShimmerViewHolder extends RecyclerView.ViewHolder {
        ShimmerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
