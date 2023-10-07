package com.prashant.stockmarketadviser.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.adapter.MyAdapter;
import com.prashant.stockmarketadviser.adapter.ReverseLinearLayoutManager;
import com.prashant.stockmarketadviser.databinding.ActivityRegistrationBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.PlanModel;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.activity.admin.BaseActivity;
import com.prashant.stockmarketadviser.activity.admin.PrivacyPolicyActivity;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.MyDialog;
import com.prashant.stockmarketadviser.util.VUtil;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RegistrationActivity extends BaseActivity {

    ActivityRegistrationBinding bind;

    CountDownTimer resendTimer;
    long timeLeftInMillis = 60000; // 60 seconds
    boolean timerRunning = false;

    FirebaseRecyclerAdapter<PlanModel, MyAdapter.MyHolder> adapter;
    MyDialog planDialog;

    private String firstName, lastName, mobile, stockPlan, email, password, confirmPassword;


    private FirebaseAuth mAuth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();


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


        bind.continueBtn.setOnClickListener(view -> {

            CProgressDialog.mShow(RegistrationActivity.this);
            firstName = bind.firstNameEd.getText().toString().trim().toLowerCase();
            lastName = bind.lastNameEd.getText().toString().trim().toLowerCase();

            mobile = bind.mobileEd.getText().toString().trim();
            stockPlan = bind.stockPlanTextView.getText().toString();
            email = bind.emailEd.getText().toString().trim().toLowerCase();
            password = bind.passwordEd.getText().toString();
            confirmPassword = bind.confirmPasswordEd.getText().toString();

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


            mobileOtpVerification();


        });

        bind.verificationBtn.setOnClickListener(view -> {
            CProgressDialog.mShow(RegistrationActivity.this);
            String otp = bind.otpEd.getText().toString().trim();

            if (otp.isEmpty()) {
                CProgressDialog.mDismiss();
                VUtil.showWarning(RegistrationActivity.this, "Please enter OTP!");
            } else if (otp.length() != 6) {
                CProgressDialog.mDismiss();
                VUtil.showWarning(RegistrationActivity.this, "OTP should be exactly 6 digits");
            } else if (verificationId == null) {
                CProgressDialog.mDismiss();
                VUtil.showWarning(RegistrationActivity.this, "OTP verification failed. Please try again !");
            } else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                signInWithPhoneAuthCredential(credential);
            }
        });

        bind.privacyBtn.setOnClickListener(view -> startActivity(new Intent(RegistrationActivity.this, PrivacyPolicyActivity.class)));

        bind.back.setOnClickListener(view -> {
            if (bind.verificationLayout.getVisibility() == View.VISIBLE) {
                bind.verificationLayout.setVisibility(View.GONE);
                bind.registrationLayout.setVisibility(View.VISIBLE);
            } else {
                finish();
            }
        });


        bind.stockPlanBtn.setOnClickListener(view -> planDialog.show());

        bind.resentBtn.setOnClickListener(view -> {
            CProgressDialog.mShow(RegistrationActivity.this);
            mobileOtpVerification();
        });

    }

    private void mobileOtpVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + mobile, 60, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                CProgressDialog.mDismiss();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                CProgressDialog.mDismiss();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    VUtil.showErrorToast(RegistrationActivity.this, e.getMessage());
                }
            }

            @Override
            public void onCodeSent(String verification, PhoneAuthProvider.ForceResendingToken token) {
                startCountdownTimer();
                bind.registrationLayout.setVisibility(View.GONE);
                bind.verificationLayout.setVisibility(View.VISIBLE);
                CProgressDialog.mDismiss();
                verificationId = verification;
                VUtil.showSuccessToast(RegistrationActivity.this, "Otp Sent " + mobile);

            }
        });

    }

    private void startCountdownTimer() {
        resendTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);

                String message = "We sent your code to " + mobile + ". Resend code in " + seconds + " seconds";
                bind.codeSentTo.setText(message);
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                String message = "We sent your code to " + mobile + ". Now you can resend the OTP.";
                bind.codeSentTo.setText(message);
                bind.resentBtn.setVisibility(View.VISIBLE);
            }
        }.start();

        timerRunning = true;
        bind.resentBtn.setVisibility(View.GONE);

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                VUtil.showSuccessToast(RegistrationActivity.this, "Mobile number verified.");

                createUserWithEmail();
            } else {
                CProgressDialog.mDismiss();
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                    VUtil.showErrorToast(this, task.getException().getMessage());
                }
            }


        });
    }

    private void createUserWithEmail() {
        new AuthManager().registerUser(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                String uid = Objects.requireNonNull(task.getResult().getUser()).getUid();


                UserModel model = new UserModel();
                model.setFullName(firstName + " " + lastName);
                model.setMobile(mobile);
                model.setEmail(email);
                model.setUserPlan(stockPlan);
                model.setUserType("user");

                model.setMemberShip("no");
                model.setUserStatus("active");

                if (stockPlan.startsWith("Free Trial")) {
                    model.setUserPlanType("free");
                } else {
                    model.setUserPlanType("paid");
                    model.setPaymentStatus("pending");
                }

                model.setUserImage(VUtil.getRandomDp());
                model.setDeviceName(VUtil.getDeviceName());
                model.setUserUid(uid);
                model.setRegistrationDate(VUtil.getCurrentDateTimeFormatted());
                model.setDeviceId(VUtil.getDeviceId(RegistrationActivity.this));

                Constant.userDB.child(uid).setValue(model).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        AuthManager.userLogin(email, password, RegistrationActivity.this);
                    }

                }).addOnFailureListener(e -> {
                    VUtil.showErrorToast(RegistrationActivity.this, e.getMessage());
                    CProgressDialog.mDismiss();
                });
            }
        }).addOnFailureListener(e -> {
            VUtil.showErrorToast(RegistrationActivity.this, e.getMessage());
            CProgressDialog.mDismiss();

        });
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

    @Override
    public void onBackPressed() {
        if (bind.verificationLayout.getVisibility() == View.VISIBLE) {
            bind.verificationLayout.setVisibility(View.GONE);
            bind.registrationLayout.setVisibility(View.VISIBLE);
            verificationId = null;

        } else {
            super.onBackPressed();
        }
    }

}