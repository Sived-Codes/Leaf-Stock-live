package com.prashant.stockmarketadviser.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.adapter.FragmentAdapter;
import com.prashant.stockmarketadviser.adapter.MyAdapter;
import com.prashant.stockmarketadviser.adapter.ReverseLinearLayoutManager;
import com.prashant.stockmarketadviser.databinding.FragmentHomeBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.activity.admin.TipGenActivity;
import com.prashant.stockmarketadviser.activity.chat.ChatListActivity;
import com.prashant.stockmarketadviser.activity.chat.SpecificChatActivity;
import com.prashant.stockmarketadviser.util.MyDialog;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Context mContext;

    FragmentHomeBinding bind;


    FirebaseRecyclerAdapter<UserModel, MyAdapter.MyHolder> adapter;
    MyDialog adminListDialog;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bind = FragmentHomeBinding.inflate(inflater, container, false);

        if (AuthManager.isAdmin()) {
            bind.adminGen.setVisibility(View.VISIBLE);
        }


        adminListDialog = new MyDialog(mContext, R.layout.cl_admin_list_dialog);

        getAdminList();

        bind.adminGen.setOnClickListener(view -> startActivity(new Intent(mContext, TipGenActivity.class)));

        bind.supportBtn.setOnClickListener(view -> {
            if (AuthManager.isAdmin()) {
                startActivity(new Intent(mContext, ChatListActivity.class));
            } else {
                adminListDialog.show();
            }
        });

        ViewPager2 viewPager = bind.viewPager;
        TabLayout tabLayout = bind.tabLayout;

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new TrialFragment());
        fragmentList.add(new PrimeFragment());

        List<String> tabNames = new ArrayList<>();
        tabNames.add("Trial Scrip");
        tabNames.add("Prime Scrip");

        FragmentAdapter adapter = new FragmentAdapter(this, fragmentList, tabNames); // 'this' is your fragment or activity

        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(adapter.getTabName(position))).attach();

        return bind.getRoot();
    }


    private void getAdminList() {
        RecyclerView recyclerView = adminListDialog.getView().findViewById(R.id.adminListRecyclerview);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        FirebaseRecyclerOptions<UserModel> options = new FirebaseRecyclerOptions.Builder<UserModel>().setQuery(Constant.userDB.orderByChild("userType").equalTo("admin"), UserModel.class).build();
        adapter = new FirebaseRecyclerAdapter<UserModel, MyAdapter.MyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyAdapter.MyHolder holder, int position, @NonNull UserModel model) {

               TextView channelName=  holder.itemView.findViewById(R.id.userName);
                channelName.setText(generateChannelName(position));

                holder.itemView.setOnClickListener(view -> {

                    Intent intent = new Intent(mContext, SpecificChatActivity.class);
                    intent.putExtra("userModel", model);
                    startActivity(intent);
                    adminListDialog.dismiss();
                });
            }

            @NonNull
            @Override
            public MyAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_chat_list_item_layout, parent, false);
                return new MyAdapter.MyHolder(itemView);
            }
        };
        recyclerView.setAdapter(adapter);

    }

    private String generateChannelName(int position) {
        return "Support Care " + (position + 1);
    }


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
