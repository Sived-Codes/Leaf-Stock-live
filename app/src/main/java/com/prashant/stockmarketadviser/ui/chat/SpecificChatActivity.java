package com.prashant.stockmarketadviser.ui.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.databinding.ActivityAddFeedBinding;
import com.prashant.stockmarketadviser.databinding.ActivitySpecificChatBinding;

public class SpecificChatActivity extends AppCompatActivity {
    ActivitySpecificChatBinding bind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySpecificChatBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
    }
}