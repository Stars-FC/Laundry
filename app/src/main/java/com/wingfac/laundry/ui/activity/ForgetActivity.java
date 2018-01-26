package com.wingfac.laundry.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.weight.LoadingDialog;
import com.yuyh.library.utils.toast.ToastUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class ForgetActivity extends BaseActivity {
    @Bind(R.id.et_phone)
    EditText phone;
    @Bind(R.id.et_code)
    EditText code;
    @Bind(R.id.btn_code)
    Button getCode;
    @Bind(R.id.et_input)
    EditText password;
    @Bind(R.id.et_confirm)
    EditText passwordAgain;
    @Bind(R.id.btn_login)
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        ButterKnife.bind(this);
        initData();
    }
    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }
    void initData() {
        getCode.setOnClickListener(view -> {
            if (phone.getText().toString().equals("")) {
                ToastUtils.showToast("请输入手机号");
                return;
            }
            if(!isChinaPhoneLegal(phone.getText().toString())){
                ToastUtils.showToast("您输入的手机号有误");
                return;
            }
            LoadingDialog.showRoundProcessDialog(getActivity());
            sendCode();
        });
        login.setOnClickListener(view -> {
            if (phone.getText().toString().equals("")) {
                ToastUtils.showToast("请输入手机号");
                return;
            }
            if (code.getText().toString().equals("")) {
                ToastUtils.showToast("请输入验证码");
                return;
            }
            if(!isChinaPhoneLegal(phone.getText().toString())){
                ToastUtils.showToast("您输入的手机号有误");
                return;
            }
            if (password.getText().toString().equals("")) {
                ToastUtils.showToast("请输入新密码");
                return;
            }
            if (!passwordAgain.getText().toString().equals(password.getText().toString())) {
                ToastUtils.showToast("两次输入密码不一致");
                return;
            }
            LoadingDialog.showRoundProcessDialog(getActivity());
            forget();
        });
    }

    private void getCode() {
        /**
         * 设置点击后验证按钮的背景样式
         */
        getCode.setClickable(false);
        getCode.setBackgroundResource(R.drawable.register_selector_last);
        /**
         * 计时器
         */
        CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long l) {
                getCode.setText(l / 1000 + "S");
            }

            @Override
            public void onFinish() {
                getCode.setClickable(true);
                getCode.setBackgroundResource(R.drawable.shape_btn_code);
                getCode.setText("获取验证码");
            }
        };
        /**
         * 启动计时器
         */
        timer.start();
        toastS("验证码已发送，请注意查收哦");
    }

    void sendCode() {
        APPApi.getInstance().service
                .sendCode(phone.getText().toString(), "2")
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Base value) {
                        LoadingDialog.mDialog.dismiss();
                        if (value.responseStatus.equals("0")) {
                            getCode();
                        } else {
                            Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                        }
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

    void forget() {
        APPApi.getInstance().service
                .forget(phone.getText().toString(), code.getText().toString(), password.getText().toString())
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Base value) {
                        LoadingDialog.mDialog.dismiss();
                        if (value.responseStatus.equals("0")) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.putExtra("name", phone.getText().toString());
                            intent.putExtra("password", password.getText().toString());
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                        }
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
}
