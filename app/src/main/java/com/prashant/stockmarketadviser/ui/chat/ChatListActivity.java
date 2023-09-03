package com.prashant.stockmarketadviser.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.prashant.stockmarketadviser.databinding.ActivityChatListBinding;
import com.prashant.stockmarketadviser.ui.admin.ManageUserActivity;

public class ChatListActivity extends AppCompatActivity {

    ActivityChatListBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityChatListBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        bind.addChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatListActivity.this,  ManageUserActivity.class));
            }
        });
    }
}