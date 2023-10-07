package com.prashant.stockmarketadviser.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.model.ChatModel;
import com.prashant.stockmarketadviser.util.VUtil;

import java.util.ArrayList;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<ChatModel> messagesArrayList;

    int ITEM_SEND = 1;
    int ITEM_RECEIVE = 2;

    public ChatAdapter(Context context, ArrayList<ChatModel> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {

            View view = LayoutInflater.from(context).inflate(R.layout.cl_sender_chat_layout, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.cl_receiver_chat_layout, parent, false);
            return new RecieverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ChatModel messages = messagesArrayList.get(position);

        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            viewHolder.textViewmessaage.setText(messages.getMessage());
            viewHolder.timeofmessage.setText(VUtil.timeStampToFormatTime(messages.getTimestamp()));

        } else {
            RecieverViewHolder viewHolder = (RecieverViewHolder) holder;
            viewHolder.textViewmessaage.setText(messages.getMessage());
            viewHolder.timeofmessage.setText(VUtil.timeStampToFormatTime(messages.getTimestamp()));

        }


    }

    @Override
    public int getItemViewType(int position) {
        ChatModel messages = messagesArrayList.get(position);
        if (Objects.requireNonNull(AuthManager.getUid()).equals(messages.getSenderUid())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }


    static class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView textViewmessaage;
        TextView timeofmessage;
        RelativeLayout chat;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmessaage = itemView.findViewById(R.id.sendermessage);
            timeofmessage = itemView.findViewById(R.id.timeofmessage);
            chat = itemView.findViewById(R.id.layoutformessage);

        }
    }

    static class RecieverViewHolder extends RecyclerView.ViewHolder {

        TextView textViewmessaage;
        TextView timeofmessage;

        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmessaage = itemView.findViewById(R.id.sendermessage);
            timeofmessage = itemView.findViewById(R.id.timeofmessage);
        }
    }
}
