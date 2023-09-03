package com.prashant.stockmarketadviser.ui.auth;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
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
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.adapter.MyAdapter;
import com.prashant.stockmarketadviser.adapter.ReverseLinearLayoutManager;
import com.prashant.stockmarketadviser.databinding.ActivityRegistrationBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.PlanModel;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.ui.BaseActivity;
import com.prashant.stockmarketadviser.ui.admin.PrivacyPolicyActivity;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.MyDialog;
import com.prashant.stockmarketadviser.util.PopupMenuHelper;
import com.prashant.stockmarketadviser.util.VUtil;

import java.util.Calendar;
import java.util.Objects;

public class RegistrationActivity extends BaseActivity {

    ActivityRegistrationBinding bind;
    FirebaseRecyclerAdapter<PlanModel, MyAdapter.MyHolder> adapter;
    MyDialog planDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        planDialog = new MyDialog(RegistrationActivity.this, R.layout.cl_membership_dialog);


        getPlanList();
        setupListeners();


    }

    private void getPlanList() {
        RecyclerView recyclerView = planDialog.getView().findViewById(R.id.memberShipRecyclerview);

        VUtil.EmptyViewHandler(Constant.planDB, planDialog.getView().findViewById(R.id.view), planDialog.getView().findViewById(R.id.progressBar));

        recyclerView.setLayoutManager(new ReverseLinearLayoutManager(RegistrationActivity.this));

        FirebaseRecyclerOptions<PlanModel> options = new FirebaseRecyclerOptions.Builder<PlanModel>().setQuery(Constant.planDB, PlanModel.class).build();
        adapter = new FirebaseRecyclerAdapter<PlanModel, MyAdapter.MyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyAdapter.MyHolder holder, int position, @NonNull PlanModel model) {

                TextView plan = holder.itemView.findViewById(R.id.plan_name);
                TextView price = holder.itemView.findViewById(R.id.plan_price_and_validity);

                price.setText(model.getPrice());
                plan.setText(model.getPlan());

                holder.itemView.setOnClickListener(view -> {

                    bind.stockPlanTextView.setTextColor(ContextCompat.getColor(RegistrationActivity.this, R.color.black));
                    bind.stockPlanTextView.setText(model.getPlan() + " " + model.getPrice());
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

    private void setupListeners() {



        bind.submitBtn.setOnClickListener(view -> {
            CProgressDialog.mShow(RegistrationActivity.this);
            String firstName = bind.firstNameEd.getText().toString().trim();
            String lastName = bind.lastNameEd.getText().toString().trim();
            String dateOfBirth = bind.dobTextView.getText().toString().trim();
            String gender = bind.genderTextView.getText().toString().trim();
            String mobile = bind.mobileEd.getText().toString().trim();
            String stockPlan = bind.stockPlanTextView.getText().toString();
            String address = bind.postalAddressEd.getText().toString().trim();
            String email = bind.emailEd.getText().toString().trim();
            String password = bind.passwordEd.getText().toString();
            String confirmPassword = bind.confirmPasswordEd.getText().toString();


            if (firstName.isEmpty()) {
                CProgressDialog.mDismiss();
                bind.firstNameEd.setError("First name cannot be empty");
                bind.firstNameEd.requestFocus();
                return;
            }

            if (lastName.isEmpty()) {
                CProgressDialog.mDismiss();
                bind.lastNameEd.setError("Last name cannot be empty");
                bind.lastNameEd.requestFocus();
                return;
            }

            if (gender.equals("Select Gender")) {
                CProgressDialog.mDismiss();
                VUtil.showWarning(RegistrationActivity.this, "Please select Gender");
                return;
            }

            if (dateOfBirth.equals("Date of birth")) {
                CProgressDialog.mDismiss();
                bind.dobTextView.setError("Date of birth cannot be empty");
                bind.dobTextView.requestFocus();
                return;
            }

            if (mobile.isEmpty()) {
                CProgressDialog.mDismiss();
                bind.mobileEd.setError("Mobile number cannot be empty");
                bind.mobileEd.requestFocus();
                return;
            }

            if (mobile.length() != 10) {
                CProgressDialog.mDismiss();
                bind.mobileEd.setError("Mobile number should be 10 digits");
                bind.mobileEd.requestFocus();
                return;
            }

            if (stockPlan.equals("Select your plan")) {
                CProgressDialog.mDismiss();
                VUtil.showWarning(RegistrationActivity.this, "Please select any plan");
                return;
            }

            if (address.isEmpty()) {
                CProgressDialog.mDismiss();
                bind.postalAddressEd.setError("Address cannot be empty");
                bind.postalAddressEd.requestFocus();
                return;
            }


            if (email.isEmpty()) {
                CProgressDialog.mDismiss();
                bind.emailEd.setError("Email address cannot be empty");
                bind.emailEd.requestFocus();
                return;
            }


            if (!email.endsWith("@gmail.com") && !email.endsWith("@yahoo.com") && !email.endsWith("@outlook.com") && !email.endsWith("@hotmail.com")) {
                CProgressDialog.mDismiss();
                bind.emailEd.setError("Invalid email domain");
                bind.emailEd.requestFocus();
                return;
            }


            if (password.isEmpty()) {
                CProgressDialog.mDismiss();
                bind.passwordEd.setError("Password cannot be empty");
                bind.passwordEd.requestFocus();
                return;
            }


            if (confirmPassword.isEmpty()) {
                CProgressDialog.mDismiss();
                bind.confirmPasswordEd.setError("Confirm password cannot be empty");
                bind.confirmPasswordEd.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                CProgressDialog.mDismiss();
                bind.confirmPasswordEd.setError("Passwords do not match");
                bind.confirmPasswordEd.requestFocus();
                return;
            }


            new AuthManager().registerUser(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    String uid = Objects.requireNonNull(task.getResult().getUser()).getUid();
                    UserModel model = new UserModel();
                    model.setFullName(firstName + " " + lastName);
                    model.setGender(gender);
                    model.setMobile(mobile);
                    model.setEmail(email);
                    model.setDateOfBirth(dateOfBirth);
                    model.setAddress(address);
                    model.setUserPlan(stockPlan);
                    model.setUserType("user");
                    model.setMemberShip("no");
                    model.setUserStatus("active");
                    model.setUserImage(VUtil.getRandomDp());
                    model.setDeviceName(VUtil.getDeviceName());
                    model.setUserUid(uid);
                    model.setDeviceId(VUtil.getDeviceId(RegistrationActivity.this));

                    Constant.userDB.child(uid).setValue(model).addOnCompleteListener(task1 -> {
                        VUtil.showSuccessToast(RegistrationActivity.this, "Registration done !");
                        CProgressDialog.mDismiss();


                    }).addOnFailureListener(e -> {
                        VUtil.showErrorToast(RegistrationActivity.this, e.getMessage());
                        CProgressDialog.mDismiss();
                    });
                }
            }).addOnFailureListener(e -> {
                VUtil.showErrorToast(RegistrationActivity.this, e.getMessage());
                CProgressDialog.mDismiss();

            });


        });

        bind.privacyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, PrivacyPolicyActivity.class));
            }
        });

        bind.back.setOnClickListener(view -> finish());

        bind.genderBtn.setOnClickListener(view -> {

            PopupMenuHelper popupMenuHelper = new PopupMenuHelper(RegistrationActivity.this, bind.genderTextView, R.layout.select_gender);

            popupMenuHelper.getView().findViewById(R.id.select_male).setOnClickListener(view12 -> {
                bind.genderTextView.setText("Male");
                bind.genderTextView.setTextColor(ContextCompat.getColor(RegistrationActivity.this, R.color.black));
                popupMenuHelper.dismiss();
            });

            popupMenuHelper.getView().findViewById(R.id.select_female).setOnClickListener(view1 -> {
                bind.genderTextView.setText("Female");
                bind.genderTextView.setTextColor(ContextCompat.getColor(RegistrationActivity.this, R.color.black));
                popupMenuHelper.dismiss();

            });

            popupMenuHelper.getView().findViewById(R.id.select_not_to_say).setOnClickListener(view1 -> {
                bind.genderTextView.setText("Not to say");
                bind.genderTextView.setTextColor(ContextCompat.getColor(RegistrationActivity.this, R.color.black));
                popupMenuHelper.dismiss();

            });

            popupMenuHelper.show();
        });

        bind.dobBtn.setOnClickListener(v -> showDatePickerDialog());

        bind.stockPlanBtn.setOnClickListener(view -> planDialog.show());

    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            @SuppressLint("DefaultLocale") String selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear);
            bind.dobTextView.setText(selectedDate);
            bind.dobTextView.setTextColor(ContextCompat.getColor(RegistrationActivity.this, R.color.black));

        }, year, month, day);

        datePickerDialog.show();
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