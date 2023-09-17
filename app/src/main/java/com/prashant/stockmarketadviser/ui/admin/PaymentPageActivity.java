package com.prashant.stockmarketadviser.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.adapter.MyAdapter;
import com.prashant.stockmarketadviser.adapter.ReverseLinearLayoutManager;
import com.prashant.stockmarketadviser.databinding.ActivityPaymentPageBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.PlanModel;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.MyDialog;
import com.prashant.stockmarketadviser.util.VUtil;

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


        planDialog = new MyDialog(PaymentPageActivity.this, R.layout.cl_membership_dialog);

        getPlanList();

        bind.changeSubscriptionPlanBtn.setOnClickListener(view -> planDialog.show());


        bind.paymentDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                    Constant.userDB.child(Objects.requireNonNull(AuthManager.getUid())).updateChildren(planUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                updatePlan();
                                CProgressDialog.mDismiss();
                                planDialog.dismiss();
                                VUtil.showSuccessToast(PaymentPageActivity.this, "Your plan has been changed.");
                                AuthManager.handleUserLogin(PaymentPageActivity.this);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CProgressDialog.mDismiss();
                            VUtil.showErrorToast(PaymentPageActivity.this, e.getMessage());

                        }
                    });

                }


            }
        });

        updatePlan();


    }

    private String updatePlan() {
        UserModel model = AuthManager.userChecker(PaymentPageActivity.this);

        bind.selectedPlanView.setText(model.getUserPlan());


        return model.getUserPlan();
    }

    private void getPlanList() {
        RecyclerView recyclerView = planDialog.getView().findViewById(R.id.memberShipRecyclerview);

        VUtil.EmptyViewHandler(Constant.planDB, planDialog.getView().findViewById(R.id.view), planDialog.getView().findViewById(R.id.progressBar));

        recyclerView.setLayoutManager(new ReverseLinearLayoutManager(PaymentPageActivity.this));

        FirebaseRecyclerOptions<PlanModel> options = new FirebaseRecyclerOptions.Builder<PlanModel>().setQuery(Constant.planDB, PlanModel.class).build();
        adapter = new FirebaseRecyclerAdapter<PlanModel, MyAdapter.MyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyAdapter.MyHolder holder, int position, @NonNull PlanModel model) {

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