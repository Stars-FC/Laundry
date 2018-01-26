package com.wingfac.laundry.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.StoreDetailBean;
import com.wingfac.laundry.bean.base.Constant;
import com.wingfac.laundry.ui.activity.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/4 0004.
 */

public class StoreDetailActivity extends BaseActivity {
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.head_layout_right)
    Button right;
    @Bind(R.id.activity_store_detail_img)
    ImageView img;
    @Bind(R.id.activity_store_detail_logo)
    ImageView logo;
    @Bind(R.id.activity_store_detail_name)
    TextView name;
    @Bind(R.id.activity_store_detail_open_time)
    TextView openTime;
    @Bind(R.id.activity_store_detail_address)
    TextView address;
    @Bind(R.id.activity_store_detail_mobile)
    TextView mobile;
    @Bind(R.id.activity_store_detail_explain)
    TextView explain;
    private int s_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        ButterKnife.bind(this);
        s_id = getIntent().getIntExtra("s_id", -1);
        initData();
    }

    void initData() {
        left.setOnClickListener(view -> finish());
        title.setText("店铺详情");
        right.setVisibility(View.GONE);
        getStoreDetail();
    }

    void getStoreDetail() {
        APPApi.getInstance().service
                .getStoreDetail(String.valueOf(s_id))
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<StoreDetailBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(StoreDetailBean value) {
                        if (value.responseStatus.equals("0")) {
                            Glide.with(getActivity())
                                    .load(Constant.BASE_IMG + value.obj.picture)
                                    .dontAnimate()
                                    .placeholder(R.drawable.erro_store)
                                    .into(img);
                            Glide.with(getActivity())
                                    .load(Constant.BASE_IMG + value.obj.s_logo)
                                    .dontAnimate()
                                    .placeholder(R.drawable.erro_store)
                                    .into(logo);
                            name.setText("店铺名称\r" + value.obj.s_name);
                            openTime.setText(value.obj.open_time);
                            address.setText(value.obj.s_address);
                            mobile.setText(value.obj.s_mobile);
                            explain.setText(value.obj.describe);
                        } else {
                            Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
