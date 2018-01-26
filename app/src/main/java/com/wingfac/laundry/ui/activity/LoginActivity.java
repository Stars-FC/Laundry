package com.wingfac.laundry.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.app.MyApplication;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.weight.LoadingDialog;
import com.yuyh.library.utils.toast.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class LoginActivity extends BaseActivity {
    @Bind(R.id.et_name)
    EditText name;
    @Bind(R.id.et_password)
    EditText password;
    @Bind(R.id.btn_prompt)
    TextView forget;
    @Bind(R.id.btn_register)
    TextView register;
    @Bind(R.id.btn_login)
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (getIntent().getStringExtra("name") != null) {
            name.setText(getIntent().getStringExtra("name"));
        }
        if (getIntent().getStringExtra("password") != null) {
            password.setText(getIntent().getStringExtra("password"));
        }
        initData();
    }

    void initData() {
        login.setOnClickListener(view -> {
            if (name.getText().toString().equals("")) {
                ToastUtils.showToast("请输入用户名");
                return;
            } else if (password.getText().toString().equals("")) {
                ToastUtils.showToast("请输入密码");
                return;
            }
            LoadingDialog.showRoundProcessDialog(getActivity());
            login();
        });
        forget.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ForgetActivity.class);
            startActivity(intent);
        });
        register.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            startActivity(intent);
        });
    }

    //重写onKeyDown方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断当点击的是返回键
        if (keyCode == event.KEYCODE_BACK) {
            removeALLActivity();//退出方法
        }

        return true;
    }

    void login() {
        APPApi.getInstance().service
                .login(name.getText().toString(), password.getText().toString())
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<UserBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(UserBean value) {
                        LoadingDialog.mDialog.dismiss();
                        if (value.responseStatus.equals("0")) {
                            JPushInterface.setAlias(getActivity(), value.obj.tel, (i, s, set) -> {

                            });
                            UserBean.user = value.obj;
                            UserBean.userStore = value.obj1;
                            MyApplication.getAcache().put("userName", name.getText().toString());
                            MyApplication.getAcache().put("passWord", password.getText().toString());
                            MyApplication.getAcache().put("loginState", "true");
                            Intent intent;
                            if (value.obj.type == 0) {
                                intent = new Intent(getActivity(), MainActivity.class);
                            } else {
                                intent = new Intent(getActivity(), MainMerchantActivity.class);
                            }
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
