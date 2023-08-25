package com.prashant.stockmarketadviser.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.LayoutRes;
import androidx.appcompat.widget.PopupMenu;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.util.PopupMenuHelper;

public class MyDialog {

    private Context context;
    private View dialogView;
    private MaterialAlertDialogBuilder builder;

    public MyDialog(Context context, @LayoutRes int layoutResId) {
        this.context = context;
        dialogView = LayoutInflater.from(context).inflate(layoutResId, null);
        builder = new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_Rounded);
        builder.setView(dialogView);
    }

    public View getView() {
        return dialogView;
    }

    public void setCancelable(boolean isCancelable) {
        builder.setCancelable(isCancelable);
    }

    public void show() {
        builder.show();
    }


}
