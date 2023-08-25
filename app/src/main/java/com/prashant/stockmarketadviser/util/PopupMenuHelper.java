package com.prashant.stockmarketadviser.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import androidx.annotation.LayoutRes;

public class PopupMenuHelper {

    private Context context;
    private View anchorView;
    private PopupWindow popupWindow;
    private View popupView;

    public PopupMenuHelper(Context context, View anchorView, @LayoutRes int layoutResId) {
        this.context = context;
        this.anchorView = anchorView;
        popupView = LayoutInflater.from(context).inflate(layoutResId, null);

        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
    }

    public View getView() {
        return popupView;
    }

    public void show() {
        popupWindow.showAsDropDown(anchorView);
    }

    public void dismiss() {
        popupWindow.dismiss();
    }
}
