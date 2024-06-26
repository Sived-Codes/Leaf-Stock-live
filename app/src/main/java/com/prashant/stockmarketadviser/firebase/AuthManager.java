package com.prashant.stockmarketadviser.firebase;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.model.UserModel;
import com.prashant.stockmarketadviser.activity.admin.PaymentPendingActivity;
import com.prashant.stockmarketadviser.activity.auth.LoginActivity;
import com.prashant.stockmarketadviser.activity.dashboard.DashboardActivity;
import com.prashant.stockmarketadviser.util.AsyncTaskHelper;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.MyDialog;
import com.prashant.stockmarketadviser.util.VUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AuthManager {

    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static UserModel model = null;
    private static boolean isSubscribed = false;
    private static boolean isAdmin = false;

    public static UserModel userChecker(Context appContext) {
        AsyncTaskHelper.runInBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    String uid = getUid();
                    if (uid != null) {
                        DatabaseReference userRef = Constant.userDB.child(uid);

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                model = snapshot.getValue(UserModel.class);
                                if (model != null) {
                                    setupUser(appContext, model);

                                } else {
                                    handleDataConsistencyIfNeeded(appContext);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                handleDataConsistencyIfNeeded(appContext);
                            }
                        });
                    } else {
                        handleDataConsistencyIfNeeded(appContext);
                    }
                } catch (Exception e) {
                    Log.e("AuthManager", "userChecker: " + e.getMessage(), e);
                }

            }
        });

        return model;
    }

    public static void userLogin(String email, String password, Context context) {

        try{
            if (!CProgressDialog.isDialogShown)CProgressDialog.mShow(context);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String uid = AuthManager.getUid();
                    if (uid != null) {
                        Constant.userDB.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {

                                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task12 -> {
                                        if (task12.isSuccessful()){
                                            Map<String, Object> token = new HashMap<>();
                                            token.put("firebaseToken", task12.getResult());

                                            Constant.userDB.child(uid).updateChildren(token).addOnCompleteListener(task1 -> {

                                                if (task1.isSuccessful()) {
                                                    handleUserLogin(context);
                                                    Map<String, Object> map = new HashMap<>();

                                                    map.put("deviceId", VUtil.getDeviceId(context));
                                                    map.put("deviceName", VUtil.getDeviceName());
                                                    Constant.userDB.child(uid).updateChildren(map).addOnCompleteListener(task2 -> {
                                                        if (task2.isSuccessful()){
                                                            model = snapshot.getValue(UserModel.class);
                                                            if (model != null) {
                                                                setupUser(context, model);
                                                            }
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(e -> {
                                                CProgressDialog.mDismiss();
                                                VUtil.showErrorToast(context, e.getMessage());
                                            });
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                CProgressDialog.mDismiss();
                                VUtil.showErrorToast(context, error.getMessage());
                            }
                        });
                    }
                } else {
                    CProgressDialog.mDismiss();
                    VUtil.showErrorToast(context, task.getException().getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("AuthManager", "userLogin: " + e.getMessage(), e);
        }
    }

    private static void setupUser(Context appContext, UserModel userModel) {

        FirebaseMessaging.getInstance().subscribeToTopic(Constant.All_NOTIFICATION_TOPIC);
        FirebaseMessaging.getInstance().subscribeToTopic(Constant.TRIAL_NOTIFICATION_TOPIC);

        handleUserStatus(appContext, userModel);
        handleSubscriptionStatus(userModel);
        isAdmin = userModel.getUserType().equals("admin");
        handleUserProfileImageIfNeeded(userModel);
        handleDataConsistencyIfNeeded(appContext, userModel);
        if (CProgressDialog.isDialogShown)CProgressDialog.mDismiss();

    }

    public static void handleUserLogin(Context appContext) {

        if (mAuth.getCurrentUser() != null) {
            if (!CProgressDialog.isDialogShown) CProgressDialog.mShow(appContext);

            String uid = getUid();
            if (uid != null) {
                Constant.userDB.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        model = snapshot.getValue(UserModel.class);
                        CProgressDialog.mDismiss();
                        if (model != null) {
                            Intent intent;
                            if (isPaymentPending(model)) {
                                intent = new Intent(appContext, PaymentPendingActivity.class);
                            } else {
                                intent = new Intent(appContext, DashboardActivity.class);
                            }

                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            appContext.startActivity(intent);

                        } else {
                            CProgressDialog.mDismiss();
                            VUtil.showErrorToast(appContext, "Model or Auth Null");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        CProgressDialog.mDismiss();
                        VUtil.showErrorToast(appContext, error.getMessage());
                    }
                });
            }
        }

    }

    public static void getFreeTrialExpireUser(Context context) {

        AsyncTaskHelper.runInBackground(new Runnable() {
            @Override
            public void run() {
                CProgressDialog.mShow(context);
                Constant.userDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Date currentDate = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a - dd MMM yyyy", Locale.ENGLISH);

                        List<UserModel> expiredUsers = new ArrayList<>();

                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            UserModel userModel = userSnapshot.getValue(UserModel.class);

                            if (userModel != null && userModel.getRegistrationDate() != null && userModel.getUserPlanType().equals("free")) {
                                try {
                                    Date registrationDate = sdf.parse(userModel.getRegistrationDate());

                                    Calendar calendar = Calendar.getInstance();
                                    if (registrationDate != null) {
                                        calendar.setTime(registrationDate);
                                    }
                                    calendar.add(Calendar.DAY_OF_MONTH, 14);
                                    Date expireDate = calendar.getTime();

                                    if (currentDate.after(expireDate)) {
                                        userModel.setUserStatus("inactive");
                                        DatabaseReference userRef = Constant.userDB.child(userModel.getUserUid());
                                        userRef.child("userStatus").setValue("inactive");

                                        // Add the user to the list of expired users
                                        expiredUsers.add(userModel);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        VUtil.showSuccessToast(context, "Scanned Successful");
                        CProgressDialog.mDismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        CProgressDialog.mDismiss();
                    }
                });
            }
        });


    }


    private static boolean isPaymentPending(UserModel userModel) {
        return userModel.getUserPlanType().equals("paid") && userModel.getPaymentStatus().equals("pending");
    }

    private static void handleUserStatus(Context context, UserModel userModel) {
        if (!userModel.getUserStatus().equals("active")) {
            showDisableDialog(context);
            VUtil.showWarning(context, "You are disabled by admin!");
        }
    }

    private static void handleSubscriptionStatus(UserModel userModel) {
        if ("yes".equals(userModel.getMemberShip())) {
            FirebaseMessaging.getInstance().subscribeToTopic(Constant.PRIME_NOTIFICATION_TOPIC);
            isSubscribed = true;
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Constant.PRIME_NOTIFICATION_TOPIC);
            isSubscribed = false;
        }
    }



    private static void handleUserProfileImageIfNeeded(UserModel userModel) {
        if (userModel.getUserImage().isEmpty()) {
            String uid = getUid();
            if (uid != null) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("userImage", VUtil.getRandomDp());
                Constant.userDB.child(uid).updateChildren(updates);
            }
        }
    }

    private static void handleDataConsistencyIfNeeded(Context context, UserModel userModel) {
        if (userModel.getUserUid() == null && userModel.getDeviceId() == null && userModel.getMobile() == null) {
            VUtil.clearAppDataAndRestart(context);
        }
    }

    private static void showDisableDialog(Context context) {
        MyDialog dialog = new MyDialog(context, R.layout.cl_user_disabled);
        dialog.getView().findViewById(R.id.connectBtn).setOnClickListener(view -> {});
        dialog.getView().findViewById(R.id.logoutBtn).setOnClickListener(view -> signOut(context));
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void signOut(Context context) {

        AsyncTaskHelper.runInBackground(new Runnable() {
            @Override
            public void run() {
                try{
                    mAuth.signOut();
                    FirebaseMessaging.getInstance().deleteToken()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                    CProgressDialog.mDismiss();
                                    VUtil.showSuccessToast(context, "User logged out!");
                                } else {
                                    CProgressDialog.mDismiss();
                                    VUtil.showErrorToast(context, "Failed to log out. Please try again.");
                                }
                            });
                }catch (Exception e){
                    VUtil.showErrorToast(context, e.getMessage());

                }
            }
        });



    }


    public static String getUid() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return (currentUser != null) ? currentUser.getUid() : null;
    }

    public static boolean isAdmin() {
        return isAdmin;
    }

    public static boolean isSubscribed() {
        return isSubscribed;
    }


    public static UserModel getUserModel() {
        return model;
    }

    public Task<AuthResult> registerUser(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    public static boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public static FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public Task<Void> deleteUserAccount() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.delete();
        }
        return null;
    }

    public AuthCredential getCredentials(String email, String password) {
        return EmailAuthProvider.getCredential(email, password);
    }

    public Task<Void> reAuthenticateUser(AuthCredential credential) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.reauthenticate(credential);
        }
        return null;
    }



    private static void handleDataConsistencyIfNeeded(Context context) {
        if (getCurrentUser() == null) {
            VUtil.clearAppDataAndRestart(context);
        }
    }

    private static void showDisableDialog(Context context, MyDialog dialog) {
        dialog.getView().findViewById(R.id.connectBtn).setOnClickListener(view -> {
        });

        dialog.getView().findViewById(R.id.logoutBtn).setOnClickListener(view -> signOut(context));

        dialog.setCancelable(false);
        dialog.show();
    }

}
