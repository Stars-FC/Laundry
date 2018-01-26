package com.wingfac.laundry.weight;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;

import com.wingfac.laundry.R;

public class ALoadingDialog extends Dialog {
    private static ALoadingDialog loadingDialog = null;
    public Animation animation;

    public ALoadingDialog(Context context) {
        super(context);
    }

    public ALoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    public static ALoadingDialog createDialog(Context context) {
        loadingDialog = new ALoadingDialog(context, R.style.CustomProgressDialog);
        loadingDialog.setContentView(R.layout.loading_dialog);
        loadingDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

        return loadingDialog;
    }

}
