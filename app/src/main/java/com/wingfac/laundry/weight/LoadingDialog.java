package com.wingfac.laundry.weight;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.wingfac.laundry.R;


/**
 * Created by Administrator on 2017/3/16 0016.
 */

public class LoadingDialog {
    public static Dialog mDialog;

    public static void showRoundProcessDialog(Context mContext) {
        DialogInterface.OnKeyListener keyListener = (dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK) {
                return true;
            }
            return false;
        };

        mDialog = new AlertDialog.Builder(mContext, R.style.NoBackGroundDialog).create();
        mDialog.setCancelable(false);
        mDialog.setOnKeyListener(keyListener);
        mDialog.show();
        // 注意此处要放在show之后 否则会报异常
        mDialog.setContentView(R.layout.loading_process_dialog_anim);
    }
}
