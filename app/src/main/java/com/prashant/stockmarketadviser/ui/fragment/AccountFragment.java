package com.prashant.stockmarketadviser.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.prashant.stockmarketadviser.databinding.FragmentAccountBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.LocalPreference;
import com.prashant.stockmarketadviser.util.VUtil;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);

        if (LocalPreference.getUserType(mContext).equals("admin")){
            binding.adminMore.setVisibility(View.VISIBLE);
        }

        binding.shimmerLayout.startShimmer();
        setupListener();
        getUserDetail();

        return binding.getRoot();
    }

    private void getUserDetail() {


        Constant.userDB.child(AuthManager.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);


                if (userModel != null) {

                    if (binding == null) {
                        return;
                    }

                    LocalPreference.storeUserType(mContext, userModel.getUserType());

                    if (userModel.getUserType().equals("user")) {
                        binding.userPlan.setText(userModel.getUserPlan());
                    } else {
                        binding.userPlan.setText("Administrator");
                    }
                    binding.userName.setText(userModel.getFullName());

                    binding.userGender.setText(userModel.getGender());
                    binding.userMobile.setText(userModel.getMobile());
                    binding.userMail.setText(userModel.getEmail());
                    binding.userDob.setText(userModel.getDateOfBirth());

                    binding.shimmerLayout.stopShimmer();
                    binding.shimmerLayout.setVisibility(View.GONE);
                    binding.main.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(mContext, "Model is null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void setupListener() {

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                VUtil.showConfirmationDialog(mContext, "Are you sure you want to logout ?", yes -> {
                    CProgressDialog.mShow(mContext);
                    new AuthManager().signOut(mContext);
                }, no -> {

                });
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
