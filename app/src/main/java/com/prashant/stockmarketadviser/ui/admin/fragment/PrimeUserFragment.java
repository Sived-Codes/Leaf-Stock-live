package com.prashant.stockmarketadviser.ui.admin.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.adapter.MyAdapter;
import com.prashant.stockmarketadviser.adapter.ReverseLinearLayoutManager;
import com.prashant.stockmarketadviser.databinding.FragmentPrimeUserBinding;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.ui.chat.SpecificChatActivity;
import com.prashant.stockmarketadviser.util.VUtil;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class PrimeUserFragment extends Fragment {
    private Context mContext;

    FragmentPrimeUserBinding bind;

    private FirebaseRecyclerAdapter<UserModel, MyAdapter.MyHolder> adapter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bind = FragmentPrimeUserBinding.inflate(inflater, container, false);


        getPrimeUser();
        return bind.getRoot();
    }

    private void getPrimeUser() {
        bind.primeUserRecyclerview.setLayoutManager(new ReverseLinearLayoutManager(mContext));
        VUtil.EmptyViewUserChecker(Constant.userDB, bind.view.empty, bind.progressBar.show, "yes");

        FirebaseRecyclerOptions<UserModel> options = new FirebaseRecyclerOptions.Builder<UserModel>().setQuery(Constant.userDB.orderByChild("memberShip").equalTo("yes"), UserModel.class).build();

        adapter = new FirebaseRecyclerAdapter<UserModel, MyAdapter.MyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyAdapter.MyHolder holder, int position, @NonNull UserModel model) {

                if (holder.itemView != null) {
                    LinearLayout chatBtn = holder.itemView.findViewById(R.id.chatBtn);

                    TextView name = holder.itemView.findViewById(R.id.userName);
                    TextView mobile = holder.itemView.findViewById(R.id.userMobile);
                    TextView email = holder.itemView.findViewById(R.id.userEmail);
                    SwitchCompat userStatusChanger = holder.itemView.findViewById(R.id.block_user_btn);
                    CircleImageView imageView = holder.itemView.findViewById(R.id.userImg);
                    Picasso.get().load(model.getUserImage()).placeholder(R.drawable.baseline_account_circle_24).into(imageView);

                    name.setText(model.getFullName());
                    mobile.setText(model.getMobile());
                    email.setText(model.getEmail());


                    if (model.getUserStatus().equals("active")) {
                        userStatusChanger.setChecked(true);
                    }


                    chatBtn.setOnClickListener(view -> startActivity(new Intent(mContext, SpecificChatActivity.class)));
                }
            }

            @NonNull
            @Override
            public MyAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_member_layout, parent, false);
                return new MyAdapter.MyHolder(itemView);
            }
        };
        bind.primeUserRecyclerview.setAdapter(adapter);

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