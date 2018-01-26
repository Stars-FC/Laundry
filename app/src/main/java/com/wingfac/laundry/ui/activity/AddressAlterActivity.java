package com.wingfac.laundry.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.AddressBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.weight.LoadingDialog;
import com.yuyh.library.utils.toast.ToastUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * 修改地址界面
 * Created by Snow on 2016/11/28 0028.
 */

public class AddressAlterActivity extends BaseActivity {
    private EditText name;
    private EditText mobile;
    private EditText address;
    private Button finishAddress;
    private AddressBean.Address addressBean;
    private RelativeLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        addressBean = (AddressBean.Address) getIntent().getSerializableExtra("address");
        initData();
    }

    private void initViews() {
        setContentView(R.layout.home_page_mine_address_addressmanage);
        name = (EditText) findViewById(R.id.home_page_mine_address_addressmanage_username);
        mobile = (EditText) findViewById(R.id.home_page_mine_address_addressmanage_phoneNumber);
        address = (EditText) findViewById(R.id.home_page_mine_address_addressmanage_address);
        finishAddress = (Button) findViewById(R.id.head_layout_right);
        back = (RelativeLayout) findViewById(R.id.head_layout_left);
        back.setOnClickListener(view -> finish());
        finishAddress.setText("完成");
        finishAddress.setVisibility(View.VISIBLE);
    }

    private void initData() {
        if (addressBean != null) {
            name.setText(addressBean.ua_name);
            mobile.setText(addressBean.ua_mobile);
            address.setText(addressBean.ua_address);
            finishAddress.setOnClickListener(view -> {
                if (name.getText().toString().equals("")) {
                    ToastUtils.showToast("请输入姓名");
                    return;
                } else if (mobile.getText().toString().equals("")) {
                    ToastUtils.showToast("请输入联系电话");
                    return;
                } else if (address.getText().toString().equals("")) {
                    ToastUtils.showToast("请输入收货地址");
                    return;
                }
                LoadingDialog.showRoundProcessDialog(getActivity());
                alertAddress();
            });
        } else {
            finishAddress.setOnClickListener(view -> {
                if (name.getText().toString().equals("")) {
                    ToastUtils.showToast("请输入姓名");
                    return;
                } else if (mobile.getText().toString().equals("")) {
                    ToastUtils.showToast("请输入联系电话");
                    return;
                } else if (address.getText().toString().equals("")) {
                    ToastUtils.showToast("请输入收货地址");
                    return;
                }
                LoadingDialog.showRoundProcessDialog(getActivity());
                addAddress();
            });
        }
    }

    void alertAddress() {
        APPApi.getInstance().service
                .alertAddress(addressBean.ua_id, name.getText().toString(), mobile.getText().toString(), address.getText().toString())
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Base value) {
                        LoadingDialog.mDialog.dismiss();
                        ToastUtils.showToast(value.msg);
                        if (value.responseStatus.equals("0")) {
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialog.mDialog.dismiss();
                        ToastUtils.showToast("请检查您的网络设置");
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void addAddress() {
        APPApi.getInstance().service
                .addAddress(UserBean.user.id, name.getText().toString(), mobile.getText().toString(), address.getText().toString())
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Base value) {
                        LoadingDialog.mDialog.dismiss();
                        ToastUtils.showToast(value.msg);
                        if (value.responseStatus.equals("0")) {
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialog.mDialog.dismiss();
                        ToastUtils.showToast("请检查您的网络设置");
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
