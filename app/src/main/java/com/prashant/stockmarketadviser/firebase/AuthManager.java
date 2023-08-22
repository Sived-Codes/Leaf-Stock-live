package com.prashant.stockmarketadviser.firebase;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.ui.auth.LoginActivity;

public class AuthManager {

    private final FirebaseAuth mAuth;

    public AuthManager() {
        mAuth = FirebaseAuth.getInstance();
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

    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public Task<Void> deleteUserAccount() {
        return mAuth.getCurrentUser().delete();
    }

    public AuthCredential getCredentials(String email, String password) {
        return EmailAuthProvider.getCredential(email, password);
    }

    public Task<Void> reAuthenticateUser(AuthCredential credential) {
        return mAuth.getCurrentUser().reauthenticate(credential);
    }

    public void signOut(Context context) {
        if (isUserLoggedIn()) {
            mAuth.signOut();
            CProgressDialog.mDismiss();
            Toast.makeText(context, "User logged out !", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else{
            CProgressDialog.mDismiss();
            Toast.makeText(context, "Failed to logged out !", Toast.LENGTH_SHORT).show();

        }
    }

    public static String getUid(){
        return new AuthManager().getCurrentUser().getUid().toString();
    }



}
