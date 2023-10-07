package com.prashant.stockmarketadviser.activity.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.adapter.MyAdapter;
import com.prashant.stockmarketadviser.adapter.ReverseLinearLayoutManager;
import com.prashant.stockmarketadviser.databinding.FragmentAccountBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.firebase.StockDatabase;
import com.prashant.stockmarketadviser.model.PlanModel;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.activity.admin.ManageUserActivity;
import com.prashant.stockmarketadviser.activity.admin.PaymentPageActivity;
import com.prashant.stockmarketadviser.activity.auth.PasswordManagerActivity;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.LocalPreference;
import com.prashant.stockmarketadviser.util.MyDialog;
import com.prashant.stockmarketadviser.util.VUtil;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AccountFragment extends Fragment {
    FirebaseRecyclerAdapter<PlanModel, MyAdapter.MyHolder> adapter;
    MyDialog planDialog;
    private FragmentAccountBinding binding;
    private Context mContext;

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView qrCodeImage;
    Uri selectedImageUri;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);

        planDialog = new MyDialog(mContext, R.layout.cl_membership_dialog);
        getPlanList();

        if (AuthManager.isAdmin()) {
            binding.moreBtn.setVisibility(View.VISIBLE);
        }

        AuthManager.userChecker(mContext);

        setupListener();
        getUserDetail();

        return binding.getRoot();
    }

    private void getPlanList() {
        RecyclerView recyclerView = planDialog.getView().findViewById(R.id.memberShipRecyclerview);

        VUtil.EmptyViewHandler(Constant.planDB, planDialog.getView().findViewById(R.id.view), planDialog.getView().findViewById(R.id.progressBar));

        recyclerView.setLayoutManager(new ReverseLinearLayoutManager(mContext));

        FirebaseRecyclerOptions<PlanModel> options = new FirebaseRecyclerOptions.Builder<PlanModel>().setQuery(Constant.planDB, PlanModel.class).build();
        adapter = new FirebaseRecyclerAdapter<PlanModel, MyAdapter.MyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyAdapter.MyHolder holder, int position, @NonNull PlanModel model) {

                TextView plan = holder.itemView.findViewById(R.id.plan_name);
                TextView price = holder.itemView.findViewById(R.id.plan_price_and_validity);
                RelativeLayout action = holder.itemView.findViewById(R.id.adminPlanActionLayout);
                ImageView deleteBtn = holder.itemView.findViewById(R.id.deleteBtn);

                price.setText(model.getPrice());
                plan.setText(model.getPlan());

                if (AuthManager.isAdmin()) {
                    action.setVisibility(View.VISIBLE);
                }

                deleteBtn.setOnClickListener(view -> {
                    CProgressDialog.mShow(mContext);
                    Constant.planDB.child(model.getUid()).removeValue().addOnSuccessListener(unused -> {
                        CProgressDialog.mDismiss();
                        VUtil.showSuccessToast(mContext, "Deleted");
                    });
                });
            }

            @NonNull
            @Override
            public MyAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_membership_item, parent, false);
                return new MyAdapter.MyHolder(itemView);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void getUserDetail() {

        Constant.userDB.child(Objects.requireNonNull(AuthManager.getUid())).addValueEventListener(new ValueEventListener() {
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
                        binding.userPlan.setText(R.string.administrator);
                    }

                    binding.userName.setText(userModel.getFullName());
                    Picasso.get().load(userModel.getUserImage()).placeholder(R.drawable.baseline_account_circle_24).into(binding.userImg);

                    binding.userMobile.setText(userModel.getMobile());
                    binding.userMail.setText(userModel.getEmail());
                    binding.userRegDate.setText("Reg : " + userModel.getRegistrationDate());

                    binding.mainAccountLayout.setVisibility(View.VISIBLE);
                    binding.progressBar.show.setVisibility(View.GONE);

                    binding.userImg.setOnClickListener(view -> {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("userImage", VUtil.getRandomDp());
                        Constant.userDB.child(userModel.getUserUid()).updateChildren(updates);
                    });

                    binding.mainAccountLayout.setVisibility(View.VISIBLE);

                } else {
                    VUtil.showWarning(mContext, "Model is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                VUtil.showErrorToast(mContext, error.getMessage());

            }
        });

    }

    private void setupListener() {

        binding.subscriptionBtn.setOnClickListener(view -> startActivity(new Intent(mContext, PaymentPageActivity.class)));

        binding.logoutBtn.setOnClickListener(view -> VUtil.showConfirmationDialog(mContext, "Are you sure you want to logout ?", yes -> {
            CProgressDialog.mShow(mContext);
            AuthManager.signOut(mContext);
        }, no -> {

        }));

        binding.changePassword.setOnClickListener(view -> startActivity(new Intent(mContext, PasswordManagerActivity.class)));


        binding.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDialog moreDialog = new MyDialog(mContext, R.layout.cl_admin_action_dialog);

                MaterialCardView updatePlanBtn = moreDialog.getView().findViewById(R.id.updatePlanBtn);
                MaterialCardView manageUserBtn = moreDialog.getView().findViewById(R.id.manageUserBtn);
                MaterialCardView appCrashBtn = moreDialog.getView().findViewById(R.id.appCrashBtn);
                MaterialCardView paymentQrBtn = moreDialog.getView().findViewById(R.id.paymentQrBtn);

                updatePlanBtn.setOnClickListener(view1 -> updatePlanDialog());
                manageUserBtn.setOnClickListener(view12 -> {
                    moreDialog.dismiss();
                    startActivity(new Intent(mContext, ManageUserActivity.class));
                });

                appCrashBtn.setOnClickListener(view12 -> {
                });
                paymentQrBtn.setOnClickListener(view13 -> showPaymentQrDialog());
                moreDialog.show();

            }

            private void showPaymentQrDialog() {
                MyDialog dialog = new MyDialog(mContext, R.layout.cl_update_qr_dialog);
                qrCodeImage = dialog.getView().findViewById(R.id.qr_code_image);
                MaterialButton uploadQrBtn = dialog.getView().findViewById(R.id.uploadQrBtn);

                String qrImgUrl = StockDatabase.getQrUrl(mContext);

                if (qrImgUrl!=null){
                    Picasso.get().load(qrImgUrl).placeholder(R.drawable.image_placeholder_2).into(qrCodeImage);
                }
                qrCodeImage.setOnClickListener(view -> {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                });

                uploadQrBtn.setOnClickListener(view -> {
                    CProgressDialog.mShow(mContext);
                    if (selectedImageUri==null){
                        CProgressDialog.mDismiss();
                        VUtil.showWarning(mContext, "Image URI is null");
                    }else{
                        uploadImageToFirebaseStorage(selectedImageUri);
                    }
                });

                dialog.show();
            }

            private void updatePlanDialog() {
                MyDialog dialog = new MyDialog(mContext, R.layout.cl_plan_layout);
                EditText plan = dialog.getView().findViewById(R.id.plan_name_ed);
                EditText price = dialog.getView().findViewById(R.id.plan_price_ed);
                EditText validity = dialog.getView().findViewById(R.id.plan_validity_ed);
                MaterialButton addPlanBtn = dialog.getView().findViewById(R.id.addPlanBtn);
                TextView showPlanBtn = dialog.getView().findViewById(R.id.show_plan_btn);

                showPlanBtn.setOnClickListener(view -> planDialog.show());
                addPlanBtn.setOnClickListener(view -> {
                    CProgressDialog.mShow(mContext);
                    String nameTxt = plan.getText().toString();
                    String priceTxt = price.getText().toString();
                    String validityTxt = validity.getText().toString();

                    if (nameTxt.isEmpty()) {
                        plan.requestFocus();
                        plan.setText(R.string.error);
                        CProgressDialog.mDismiss();
                    } else if (priceTxt.isEmpty()) {
                        price.requestFocus();
                        price.setText(R.string.error);
                        CProgressDialog.mDismiss();
                    } else if (validityTxt.isEmpty()) {
                        validity.requestFocus();
                        validity.setText(R.string.error);
                        CProgressDialog.mDismiss();
                    } else {
                        String uid = Constant.planDB.push().getKey();
                        PlanModel model = new PlanModel();
                        model.setUid(uid);
                        model.setPlan(nameTxt);
                        model.setPrice(priceTxt + "₹ For " + validityTxt);
                        if (uid != null) {
                            Constant.planDB.child(uid).setValue(model).addOnCompleteListener(task -> {
                                VUtil.showSuccessToast(mContext, "Plan successfully updated");
                                CProgressDialog.mDismiss();
                            }).addOnFailureListener(e -> {
                                VUtil.showErrorToast(mContext, e.getMessage());
                                CProgressDialog.mDismiss();
                            });
                        }
                    }
                });


                dialog.show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                qrCodeImage.setImageURI(selectedImageUri);
            }
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        if (imageUri == null) {
            CProgressDialog.mDismiss();
            VUtil.showWarning(mContext, "Image is null");
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("Qr Payment/" + UUID.randomUUID().toString());

        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            HashMap<String, Object> map =new HashMap<>();
            map.put("qr_img_url",imageUrl);
            Constant.adminDB.child("QrCode").updateChildren(map).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    CProgressDialog.mDismiss();
                    VUtil.showSuccessToast(mContext, "QR Code uploaded");
                }
            }).addOnFailureListener(e -> {
                CProgressDialog.mDismiss();
                VUtil.showErrorToast(mContext, e.getMessage());
            });
        })).addOnFailureListener(e -> {
            CProgressDialog.mDismiss();

            VUtil.showErrorToast(mContext, e.getMessage());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
