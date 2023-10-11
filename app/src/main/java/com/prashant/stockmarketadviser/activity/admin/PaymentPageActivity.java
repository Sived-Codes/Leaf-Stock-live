package com.prashant.stockmarketadviser.activity.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.adapter.MyAdapter;
import com.prashant.stockmarketadviser.adapter.ReverseLinearLayoutManager;
import com.prashant.stockmarketadviser.databinding.ActivityPaymentPageBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.firebase.StockDatabase;
import com.prashant.stockmarketadviser.model.PlanModel;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.MyDialog;
import com.prashant.stockmarketadviser.util.VUtil;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PaymentPageActivity extends BaseActivity {

    ActivityPaymentPageBinding bind;
    FirebaseRecyclerAdapter<PlanModel, MyAdapter.MyHolder> adapter;
    MyDialog planDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityPaymentPageBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());


        showQrCode();

        planDialog = new MyDialog(PaymentPageActivity.this, R.layout.cl_membership_dialog);

        getPlanList();

        bind.changeSubscriptionPlanBtn.setOnClickListener(view -> planDialog.show());


        bind.paymentQrImage.setOnClickListener(view -> {
            showQrCode();
            VUtil.showSuccessToast(PaymentPageActivity.this, "Refreshed");

        });

        bind.paymentDone.setOnClickListener(view -> {
            CProgressDialog.mShow(PaymentPageActivity.this);
            String changedPlan = bind.changePlanTxt.getText().toString();

            if (changedPlan.equals("Want to change your plan")) {
                CProgressDialog.mDismiss();
                VUtil.showWarning(PaymentPageActivity.this, "You don't change any thing !");
            } else {
                Map<String, Object> planUpdate = new HashMap<>();
                planUpdate.put("userPlan", changedPlan);

                if (changedPlan.startsWith("Free Trial")) {
                    planUpdate.put("userPlanType", "free");
                } else {
                    planUpdate.put("userPlanType", "paid");
                    planUpdate.put("paymentStatus", "pending");
                }

                Constant.userDB.child(Objects.requireNonNull(AuthManager.getUid())).updateChildren(planUpdate).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updatePlan();
                        CProgressDialog.mDismiss();
                        planDialog.dismiss();
                        VUtil.showSuccessToast(PaymentPageActivity.this, "Your plan has been changed.");
                        AuthManager.handleUserLogin(PaymentPageActivity.this);
                    }
                }).addOnFailureListener(e -> {
                    CProgressDialog.mDismiss();
                    VUtil.showErrorToast(PaymentPageActivity.this, e.getMessage());

                });

            }


        });

        updatePlan();


    }

    private void showQrCode() {

        Constant.adminDB.child("QrCode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String qrCodeImgUrl = snapshot.child("qr_img_url").getValue(String.class);
                Picasso.get().load(qrCodeImgUrl).into(bind.paymentQrImage);
                bind.pd.show.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                VUtil.showErrorToast(PaymentPageActivity.this, error.getMessage());
                bind.pd.show.setVisibility(View.GONE);
            }
        });



    }

    private void updatePlan() {
        UserModel model = AuthManager.userChecker(PaymentPageActivity.this);

        bind.selectedPlanView.setText(model.getUserPlan());


    }

    private void getPlanList() {
        RecyclerView recyclerView = planDialog.getView().findViewById(R.id.memberShipRecyclerview);

        try {

            VUtil.EmptyViewHandler(Constant.planDB, planDialog.getView().findViewById(R.id.view), planDialog.getView().findViewById(R.id.progressBar));

            recyclerView.setLayoutManager(new ReverseLinearLayoutManager(PaymentPageActivity.this));

        } catch (Exception e) {
            Log.e("YourTag", "An error occurred", e);

        }


        FirebaseRecyclerOptions<PlanModel> options = new FirebaseRecyclerOptions.Builder<PlanModel>().setQuery(Constant.planDB, PlanModel.class).build();
        adapter = new FirebaseRecyclerAdapter<PlanModel, MyAdapter.MyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyAdapter.MyHolder holder, int position, @NonNull PlanModel model) {

                try {
                    TextView planName = holder.itemView.findViewById(R.id.plan_name);
                    TextView planPrice = holder.itemView.findViewById(R.id.plan_price_and_validity);

                    planPrice.setText(model.getPrice());
                    planName.setText(model.getPlan());

                    String plan = model.getPlan() + " " + model.getPrice();

                    holder.itemView.setOnClickListener(view -> {
                        bind.changePlanTxt.setTextColor(ContextCompat.getColor(PaymentPageActivity.this, R.color.black));
                        bind.changePlanTxt.setText(plan);
                        planDialog.dismiss();
                    });
                } catch (Exception e) {
                    Log.e("YourTag", "An error occurred", e);

                }


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