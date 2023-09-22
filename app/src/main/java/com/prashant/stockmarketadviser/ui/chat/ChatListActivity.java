package com.prashant.stockmarketadviser.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.adapter.MyAdapter;
import com.prashant.stockmarketadviser.adapter.ReverseLinearLayoutManager;
import com.prashant.stockmarketadviser.databinding.ActivityChatListBinding;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.ChatListModel;
import com.prashant.stockmarketadviser.model.ChatListModel;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.ui.admin.ManageUserActivity;
import com.prashant.stockmarketadviser.ui.admin.ProfileViewActivity;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.VUtil;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListActivity extends AppCompatActivity {

    ActivityChatListBinding bind;

    private FirebaseRecyclerAdapter<ChatListModel, MyAdapter.MyHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityChatListBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());


        getChatList();

        bind.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bind.addChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatListActivity.this, ManageUserActivity.class));
            }
        });
    }


    private void getChatList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatListActivity.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        bind.chatListRecyclerview.setLayoutManager(layoutManager);
        VUtil.EmptyViewHandler(Constant.adminDB.child("ChatList"), bind.view.empty, bind.progressBar.show);

        FirebaseRecyclerOptions<ChatListModel> options = new FirebaseRecyclerOptions.Builder<ChatListModel>().setQuery(Constant.adminDB.child("ChatList").orderByChild("userMsgTime"), ChatListModel.class).build();

        adapter = new FirebaseRecyclerAdapter<ChatListModel, MyAdapter.MyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyAdapter.MyHolder holder, int position, @NonNull ChatListModel model) {

                if (holder.itemView != null) {
                    TextView name = holder.itemView.findViewById(R.id.userName);
                    TextView msg = holder.itemView.findViewById(R.id.userMsg);
                    TextView typeAndTime = holder.itemView.findViewById(R.id.userMsgTime);
                    CircleImageView imageView = holder.itemView.findViewById(R.id.userImg);
                    name.setText(model.getUserName());
                    msg.setText(model.getUserMsg());
                    typeAndTime.setText(VUtil.timeStampToFormatTime(model.getUserMsgTime()));

                    Constant.userDB.child(model.getUserUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            Picasso.get().load(userModel.getUserImage()).placeholder(R.drawable.baseline_account_circle_24).into(imageView);

                            holder.itemView.setOnClickListener(view -> {
                                if (snapshot.exists()) {
                                    UserModel memberModel = snapshot.getValue(UserModel.class);
                                    Intent intent = new Intent(ChatListActivity.this, SpecificChatActivity.class);
                                    intent.putExtra("userModel", memberModel);
                                    startActivity(intent);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            VUtil.showErrorToast(ChatListActivity.this, error.getMessage());
                        }
                    });


                }
            }

            @NonNull
            @Override
            public MyAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_chat_list_item_layout, parent, false);
                return new MyAdapter.MyHolder(itemView);
            }
        };
        bind.chatListRecyclerview.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }

    }
}