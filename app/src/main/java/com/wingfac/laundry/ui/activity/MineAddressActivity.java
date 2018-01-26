package com.wingfac.laundry.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.AddressBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.ui.adapter.MineAddressAdapter;
import com.yuyh.library.utils.toast.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class MineAddressActivity extends BaseActivity {
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.activity_delivery_address)
    ListView listView;
    @Bind(R.id.head_layout_right)
    Button right;
    MineAddressAdapter addressAdapter;
    AddressBean addressBean = new AddressBean();
    private String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_address);
        ButterKnife.bind(this);
        state = getIntent().getStringExtra("state");
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addressBean.obj.clear();
        getData();
    }

    void initData() {
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state != null) {
                    for (int i = 0; i < addressBean.obj.size(); i++) {
                        if (addressBean.obj.get(i).ua_default == 1) {
                            Dstate = i;
                        }
                    }
                    if (Dstate != -1) {
                        Intent intent = getIntent();
                        intent.putExtra("AddressId", String.valueOf(addressBean.obj.get(Dstate).ua_id));
                        setResult(0,intent);
                        finish();
                    } else {
                        ToastUtils.showToast("您还未选择地址");
                        return;
                    }

                }
                finish();
            }
        });
        title.setText("编辑收货地址");
        right.setText("新增");
        right.setVisibility(View.VISIBLE);
        right.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AddressAlterActivity.class);
            startActivity(intent);
        });
        addressAdapter = new MineAddressAdapter(getActivity(), addressBean);
        listView.setAdapter(addressAdapter);
    }

    void getData() {
        APPApi.getInstance().service
                .getAddress(UserBean.user.id)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<AddressBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(AddressBean value) {
                        if (value.responseStatus.equals("0")) {
                            addressBean.obj.addAll(value.obj);
                            addressAdapter.notifyDataSetChanged();
                            if (value.obj.size() == 2) {
                                right.setVisibility(View.GONE);
                            }
                        } else {
                            ToastUtils.showToast(value.msg);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showToast("请检查您的网络设置");
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    int Dstate = -1;

    //重写onKeyDown方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断当点击的是返回键
        if (keyCode == event.KEYCODE_BACK) {
            if (state != null) {
                for (int i = 0; i < addressBean.obj.size(); i++) {
                    if (addressBean.obj.get(i).ua_default == 1) {
                        Dstate = i;
                    }
                }
                if (Dstate != -1) {
                    Intent intent = getIntent();
                    intent.putExtra("AddressId", String.valueOf(addressBean.obj.get(Dstate).ua_id));
                    setResult(0,intent);
                    finish();
                    return true;
                } else {
                    ToastUtils.showToast("您还未选择地址");
                    return false;
                }

            }
            finish();

        }

        return true;
    }
}
