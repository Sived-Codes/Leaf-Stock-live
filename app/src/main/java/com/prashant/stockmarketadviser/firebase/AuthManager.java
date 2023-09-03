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
import com.prashant.stockmarketadviser.ui.auth.LoginActivity;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.MyDialog;
import com.prashant.stockmarketadviser.util.VUtil;

import java.util.HashMap;
import java.util.Map;

public class AuthManager {

    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static UserModel model = null;
    private static boolean isSubscribed = false;
    private static boolean isAdmin = false;

    public static void userChecker(Context appContext) {
        try {
            String uid = getUid();
            if (uid != null) {
                MyDialog dialog = new MyDialog(appContext, R.layout.cl_user_disabled);

                DatabaseReference userRef = Constant.userDB.child(uid);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        model = dataSnapshot.getValue(UserModel.class);

                        FirebaseMessaging.getInstance().subscribeToTopic(Constant.All_NOTIFICATION_TOPIC);
                        FirebaseMessaging.getInstance().subscribeToTopic(Constant.TRIAL_NOTIFICATION_TOPIC);

                        if (model != null) {
                            handleUserStatus(appContext, model, dialog);
                            handleSubscriptionStatus(model);
                            isAdmin = model.getUserType().equals("admin");
                            handleDeviceChange(appContext, model);
                            handleUserProfileImageIfNeeded(uid, model);
                            handleDataConsistencyIfNeeded(appContext, model);
                        } else {
                            handleDataConsistencyIfNeeded(appContext);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error, if needed
                    }
                });
            } else {
                handleDataConsistencyIfNeeded(appContext);
            }
        } catch (Exception e) {
            Log.e("AuthManager", "userChecker: " + e.getMessage(), e);
        }
    }

    public static UserModel getUserModel() {
        return model;
    }

    public Task<AuthResult> registerUser(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> signInUser(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    public Task<Void> sendPasswordResetEmail(String email) {
        return mAuth.sendPasswordResetEmail(email);
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

    public static void signOut(Context context) {
        mAuth.signOut();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        CProgressDialog.mDismiss();
        VUtil.showSuccessToast(context, "User logged out!");
    }

    public static String getUid() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        }
        return null;
    }

    public static void adminChecker(MaterialButton button) {
        if (isAdmin()) {
            button.setVisibility(View.VISIBLE);
        }
    }

    public static boolean isAdmin() {
        return isAdmin;
    }

    public static boolean isSubscribed() {
        return isSubscribed;
    }

    private static void handleUserStatus(Context context, UserModel userModel, MyDialog dialog) {
        if (!userModel.getUserStatus().equals("active")) {
            showDisableDialog(context, dialog);
            VUtil.showWarning(context, "You are disabled by admin!");
        } else {
            dialog.dismiss();
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

    private static void handleDeviceChange(Context context, UserModel userModel) {
        if (!userModel.getDeviceId().equals(VUtil.getDeviceId(context))) {
            signOut(context);
        }
    }

    private static void handleUserProfileImageIfNeeded(String uid, UserModel userModel) {
        if (userModel.getUserImage().isEmpty()) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("userImage", VUtil.getRandomDp());
            Constant.userDB.child(uid).updateChildren(updates);
        }
    }

    private static void handleDataConsistencyIfNeeded(Context context, UserModel userModel) {
        if (userModel.getUserUid() == null && userModel.getDeviceId() == null && userModel.getMobile() == null) {
            VUtil.clearAppDataAndRestart(context);
        }
    }

    private static void handleDataConsistencyIfNeeded(Context context) {
        if (getCurrentUser() == null) {
            VUtil.clearAppDataAndRestart(context);
        }
    }

    private static void showDisableDialog(Context context, MyDialog dialog) {
        dialog.getView().findViewById(R.id.connectBtn).setOnClickListener(view -> {
            // Handle button click if needed
        });

        dialog.getView().findViewById(R.id.logoutBtn).setOnClickListener(view -> signOut(context));

        dialog.setCancelable(false);
        dialog.show();
    }
}
