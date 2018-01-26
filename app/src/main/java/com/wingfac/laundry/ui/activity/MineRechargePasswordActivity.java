package com.wingfac.laundry.ui.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.weight.LoadingDialog;
import com.yuyh.library.utils.toast.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/9 0009.
 */

public class MineRechargePasswordActivity extends BaseActivity {
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.head_layout_right)
    Button right;
    @Bind(R.id.activity_mine_vip_password_tx)
    TextView tx;
    @Bind(R.id.content_layout)
    LinearLayout contentLayout;
    PopupWindow passwordWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_vip_password);
        ButterKnife.bind(this);
        initData();
    }

    void initData() {
        left.setOnClickListener(view -> finish());
        title.setText("会员卡支付密码");
        right.setVisibility(View.VISIBLE);
        right.setText("修改");
        tx.setText(UserBean.user.payPassword);
        right.setOnClickListener(view -> {
            showPasswordWindow(contentLayout, R.layout.windows_pay_password);
        });
    }

    void upDataPassword(String passWord) {
        APPApi.getInstance().service
                .upDataPayPassword(UserBean.user.id, passWord)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Base value) {
                        LoadingDialog.mDialog.dismiss();
                        if (value.responseStatus.equals("0")) {
                            UserBean.user.payPassword = passWord;
                            tx.setText(passWord);
                            setResult(2);
                            finish();
                        }
                        Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialog.mDialog.dismiss();
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void showPasswordWindow(View parentView, int convertViewResource) {
        //创建一个popUpWindow
        View popLayout = LayoutInflater.from(getActivity()).inflate(convertViewResource, null);
        EditText password = popLayout.findViewById(R.id.windows_pay_password);
        popLayout.findViewById(R.id.windows_pay_way_confirm).setOnClickListener(view -> {
            if (password.getText().toString().equals("")) {
                ToastUtils.showToast("请输入支付密码");
                return;
            }else if(password.getText().toString().length()!=6){
                ToastUtils.showToast("请输入6位密码");
                return;
            }
            if (passwordWindow.isShowing()) {
                passwordWindow.dismiss();
                LoadingDialog.showRoundProcessDialog(getActivity());
                upDataPassword(password.getText().toString());
            }
        });
        if (passwordWindow == null) {
            //实例化一个popupWindow
            passwordWindow =
                    new PopupWindow(popLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //产生背景变暗效果
            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.alpha = 0.4f;
            getActivity().getWindow().setAttributes(lp);
            //点击外面popupWindow消失
            passwordWindow.setOutsideTouchable(true);
            //popupWindow获取焦点
            passwordWindow.setFocusable(true);
            //popupWindow设置背景图
            passwordWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            //popupWindow设置开场动画风格
            //popupWindow.setAnimationStyle(R.style.popupWindow_anim);
            //刷新popupWindow
            //popupWindow.update();

            //设置popupWindow消失时的监听
            passwordWindow.setOnDismissListener(() -> {
                WindowManager.LayoutParams lp1 = getActivity().getWindow().getAttributes();
                lp1.alpha = 1f;
                getActivity().getWindow().setAttributes(lp1);
            });
            passwordWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        } else {
            //如果popupWindow正在显示，接下来隐藏
            if (passwordWindow.isShowing()) {
                passwordWindow.dismiss();
            } else {
                //产生背景变暗效果
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 0.4f;
                getActivity().getWindow().setAttributes(lp);
                passwordWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
            }
        }
    }
}
