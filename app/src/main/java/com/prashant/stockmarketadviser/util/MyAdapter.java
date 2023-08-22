package com.prashant.stockmarketadviser.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter<T> extends RecyclerView.Adapter<MyAdapter.MyHolder> {
    List<T> objectList;
    OnBindInterface binder;
    int layout;


    public MyAdapter(List<T> objectList, OnBindInterface binder, int layout) {
        this.objectList = objectList;
        this.binder = binder;
        this.layout = layout;

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        binder.onBindHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

    public interface OnBindInterface{
        void onBindHolder(MyHolder holder, int position);
    }
}
