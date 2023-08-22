package com.prashant.stockmarketadviser.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.prashant.stockmarketadviser.R;

public class CProgressDialog {
    private static Dialog dialog;
    private static final int ANIMATION_DURATION = 600;
    public static boolean isDialogShown = false;

    public static void mShow(Context context) {
        if (isDialogShown) {
            return;
        }

        try {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.progress_bar_layout);
            dialog.setCancelable(false);
        } catch (Exception e) {
            Log.e("Tag", "Error message", e);
            return;
        }

        Window window = dialog.getWindow();
        if (window != null) {
            try {
                window.setBackgroundDrawableResource(android.R.color.transparent);
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                window.setDimAmount(0.05f); // Adjust the transparency level as desired (0.0f - fully transparent, 1.0f - fully opaque)
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(context, R.color.background_page));
            } catch (Exception e) {
                Log.e("Tag", "Error message", e);
            }
        }

        MaterialCardView dialogContainer = dialog.findViewById(R.id.dialog_container);
        if (dialogContainer != null) {
            dialogContainer.setAlpha(0f);

            try {
                dialog.show();

                // Perform fade-in animation
                dialogContainer.animate()
                        .alpha(1f)
                        .setDuration(ANIMATION_DURATION)
                        .setInterpolator(new DecelerateInterpolator())
                        .setListener(null);

                isDialogShown = true;
            } catch (Exception e) {
                Log.e("Tag", "Error message", e);
                dialog.dismiss();
                dialog = null;
            }
        }
    }

    public static boolean isDialogShown() {
        return isDialogShown;
    }

    public static void mDismiss() {
        if (dialog != null && dialog.isShowing()) {
            MaterialCardView dialogContainer = dialog.findViewById(R.id.dialog_container);

            if (dialogContainer != null) {
                // Perform fade-out animation
                dialogContainer.animate()
                        .alpha(0f)
                        .setDuration(ANIMATION_DURATION)
                        .setInterpolator(new DecelerateInterpolator())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (dialog != null && dialog.isShowing()) {
                                    try {
                                        dialog.dismiss();
                                        dialog = null;
                                    } catch (Exception e) {
                                        Log.e("Tag", "Error message", e);
                                    }
                                }
                            }
                        });
            }
        }

        isDialogShown = false;
    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View focusedView = activity.getCurrentFocus();
            if (focusedView != null && imm != null) {
                imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            }
        } catch (Exception e) {
            Log.e("Tag", "Error message", e);
        }
    }

    public static void handleBackPress(Activity activity, int keyCode) {
        if (isDialogShown && keyCode == KeyEvent.KEYCODE_BACK) {
            mDismiss();
            activity.onBackPressed();
        }
    }
}
