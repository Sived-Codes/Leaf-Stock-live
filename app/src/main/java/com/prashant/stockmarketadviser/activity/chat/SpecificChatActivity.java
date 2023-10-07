package com.prashant.stockmarketadviser.activity.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.adapter.ChatAdapter;
import com.prashant.stockmarketadviser.databinding.ActivitySpecificChatBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.firebase.NotificationSender;
import com.prashant.stockmarketadviser.model.ChatListModel;
import com.prashant.stockmarketadviser.model.ChatModel;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.activity.admin.BaseActivity;
import com.prashant.stockmarketadviser.activity.admin.ProfileViewActivity;
import com.prashant.stockmarketadviser.util.VUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SpecificChatActivity extends BaseActivity {
    ActivitySpecificChatBinding bind;

    ChatAdapter chatAdapter;
    ArrayList<ChatModel> chatList =new ArrayList<>();

    String senderRoom, receiverRoom;
    UserModel userModel, senderModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySpecificChatBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());


        userModel = (UserModel) getIntent().getSerializableExtra("userModel");

        senderModel = AuthManager.userChecker(this);

        if (userModel != null && senderModel != null) {

            senderRoom = senderModel.getUserUid() + userModel.getUserUid();
            receiverRoom = userModel.getUserUid() + senderModel.getUserUid();

            bind.actionBarTitle.setText(userModel.getFullName());

            bind.actionBarTitle.setOnClickListener(view -> {
                Intent intent = new Intent(SpecificChatActivity.this, ProfileViewActivity.class);
                intent.putExtra("uid", userModel.getUserUid());
                startActivity(intent);
            });

            Picasso.get().load(userModel.getUserImage()).placeholder(R.drawable.baseline_account_circle_24).into(bind.userImg);

            getChats();

            buttonListeners();

        }


    }

    private void buttonListeners() {
        bind.back.setOnClickListener(view -> finish());

        bind.sendBtn.setOnClickListener(view -> {
            String msg = String.valueOf(bind.msgBox.getText());

            if (msg.equals("")){
                VUtil.showWarning(SpecificChatActivity.this, "Write some think !");
            }else {
                bind.msgBox.setText("");
                ChatModel chatModel = new ChatModel();
                chatModel.setMessage(msg);
                chatModel.setSenderName(senderModel.getFullName());
                chatModel.setReceiverName(userModel.getFullName());
                chatModel.setTimestamp(System.currentTimeMillis());
                chatModel.setSenderUid(senderModel.getUserUid());

                ChatListModel chatListModel =new ChatListModel();

                Constant.chatDB.child(senderRoom).child("message").push().setValue(chatModel).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Constant.chatDB.child(receiverRoom).child("message").push().setValue(chatModel).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){

                                NotificationSender.sendChatNotificationToUser(SpecificChatActivity.this, userModel.getFirebaseToken(), senderModel.getFullName(), msg, "ChatActivity");
                                if (senderModel.getUserType().equals("admin")){
                                    chatListModel.setUserMsg(msg);
                                    chatListModel.setUserName(userModel.getFullName());
                                    chatListModel.setUserMsgTime(System.currentTimeMillis());
                                    chatListModel.setUserUid(userModel.getUserUid());

                                    Constant.adminDB.child("ChatList").child(userModel.getUserUid()).setValue(chatListModel);
                                }else{
                                    chatListModel.setUserMsg(msg);
                                    chatListModel.setUserName(senderModel.getFullName());
                                    chatListModel.setUserMsgTime(System.currentTimeMillis());
                                    chatListModel.setUserUid(senderModel.getUserUid());


                                    Constant.adminDB.child("ChatList").child(senderModel.getUserUid()).setValue(chatListModel);

                                }
                            }
                        }).addOnFailureListener(e -> VUtil.showErrorToast(SpecificChatActivity.this, e.getMessage()));
                    }

                }).addOnFailureListener(e -> VUtil.showErrorToast(SpecificChatActivity.this, e.getMessage()));
            }

        });
    }

    private void getChats() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        bind.chatRecyclerview.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter(SpecificChatActivity.this, chatList);
        bind.chatRecyclerview.setAdapter(chatAdapter);
        VUtil.EmptyViewHandler(Constant.chatDB.child(senderRoom).child("message"), bind.view.empty, bind.progressBar.show);

        Constant.chatDB.child(senderRoom).child("message").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ChatModel message = snapshot1.getValue(ChatModel.class);
                    if (message != null) {
                        chatList.add(message);
                    }
                }

                // Notify the adapter that the data has changed
                chatAdapter.notifyDataSetChanged();

                // Scroll to the last item (latest message)
                if (chatAdapter.getItemCount() > 0) {
                    bind.chatRecyclerview.scrollToPosition(chatAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                VUtil.showErrorToast(SpecificChatActivity.this, error.getMessage());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onStart() {
        super.onStart();
        if (chatAdapter != null) {
            chatAdapter.notifyDataSetChanged();
        }
    }
}