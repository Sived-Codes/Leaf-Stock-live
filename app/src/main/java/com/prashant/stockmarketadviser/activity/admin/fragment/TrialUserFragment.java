package com.prashant.stockmarketadviser.activity.admin.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
import com.prashant.stockmarketadviser.databinding.FragmentTrialUserBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.activity.admin.ProfileViewActivity;
import com.prashant.stockmarketadviser.activity.chat.SpecificChatActivity;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.VUtil;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class TrialUserFragment extends Fragment {
    private Context mContext;

    FragmentTrialUserBinding bind;

    private FirebaseRecyclerAdapter<UserModel, MyAdapter.MyHolder> adapter;

    FirebaseRecyclerOptions<UserModel> options;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bind = FragmentTrialUserBinding.inflate(inflater, container, false);
        getTrialUser();
        onSearch();

        bind.refreshBtn.setOnClickListener(view -> AuthManager.getFreeTrialExpireUser(mContext));

        return bind.getRoot();
    }

    private void onSearch() {


        bind.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchText = charSequence.toString().toLowerCase();
                if (!searchText.equals("")){
                    FirebaseRecyclerOptions<UserModel> filteredOptions = new FirebaseRecyclerOptions.Builder<UserModel>()
                            .setQuery(Constant.userDB.orderByChild("fullName").startAt(searchText).endAt(searchText + "\uf8ff"), UserModel.class)
                            .build();

                    adapter.updateOptions(filteredOptions);
                }else{
                    adapter.updateOptions(options);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used in this example
            }
        });
    }


    private void getTrialUser() {
        bind.trialUserRecyclerview.setLayoutManager(new ReverseLinearLayoutManager(mContext));
        VUtil.EmptyViewUserChecker(Constant.userDB, bind.view.empty, bind.progressBar.show, "no");

        options = new FirebaseRecyclerOptions.Builder<UserModel>().setQuery(Constant.userDB.orderByChild("memberShip").equalTo("no"), UserModel.class).build();

        adapter = new FirebaseRecyclerAdapter<UserModel, MyAdapter.MyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyAdapter.MyHolder holder, int position, @NonNull UserModel model) {


                if (holder.itemView != null) {

                    LinearLayout chatBtn = holder.itemView.findViewById(R.id.chatBtn);
                    LinearLayout callBtn = holder.itemView.findViewById(R.id.callBtn);
                    LinearLayout whatsappBtn = holder.itemView.findViewById(R.id.whatsAppBtn);


                    TextView name = holder.itemView.findViewById(R.id.userName);
                    TextView mobile = holder.itemView.findViewById(R.id.userMobile);
                    TextView email = holder.itemView.findViewById(R.id.userEmail);
                    SwitchCompat userStatusChanger = holder.itemView.findViewById(R.id.block_user_btn);
                    CircleImageView imageView = holder.itemView.findViewById(R.id.userImg);
                    Picasso.get().load(model.getUserImage()).placeholder(R.drawable.baseline_account_circle_24).into(imageView);


                    name.setText(model.getFullName());
                    mobile.setText(model.getMobile());
                    email.setText(model.getEmail());


                    whatsappBtn.setOnClickListener(view -> VUtil.openWhatsAppNumber(mContext, model.getMobile()));

                    chatBtn.setOnClickListener(view -> {
                        Intent intent = new Intent(mContext, SpecificChatActivity.class);
                        intent.putExtra("userModel", model);
                        startActivity(intent);
                    });

                    callBtn.setOnClickListener(view -> VUtil.openDialer(mContext, model.getMobile()));

                    if (model.getUserStatus().equals("active")) {
                        userStatusChanger.setChecked(true);
                    }

                    userStatusChanger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            handleUserStatusChange(isChecked);
                        }

                        private void handleUserStatusChange(boolean isChecked) {
                            CProgressDialog.mShow(mContext);
                            Map<String, Object> updates = new HashMap<>();
                            String newStatus = isChecked ? "active" : "inactive";
                            updates.put("userStatus", newStatus);

                            Constant.userDB.child(model.getUserUid()).updateChildren(updates).addOnCompleteListener(task -> {
                                CProgressDialog.mDismiss();
                                VUtil.showSuccessToast(mContext, "Status Updated");
                            }).addOnFailureListener(e -> {
                                CProgressDialog.mDismiss();
                                VUtil.showErrorToast(mContext, e.getMessage());
                            });

                        }
                    });

                    holder.itemView.setOnClickListener(view -> {
                        Intent intent = new Intent(mContext, ProfileViewActivity.class);
                        intent.putExtra("uid", model.getUserUid());
                        startActivity(intent);
                    });


                }
            }

            @NonNull
            @Override
            public MyAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_member_layout, parent, false);
                return new MyAdapter.MyHolder(itemView);
            }
        };
        bind.trialUserRecyclerview.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onPause() {
        super.onPause();

        getTrialUser();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }

    }
}