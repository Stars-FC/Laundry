package com.wingfac.laundry.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.RechargeHistoryBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.ui.adapter.RechargeHistoryAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lenovo on 2017/8/4.
 */

public class MineRechargeHistoryActivity extends BaseActivity {
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.lv_recharge_history)
    ListView listView;
    private RechargeHistoryBean rechargeHistories = new RechargeHistoryBean();
    private RechargeHistoryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recharge_history);
        ButterKnife.bind(this);
        initData();
    }

    void initData() {
        adapter = new RechargeHistoryAdapter(this, rechargeHistories);
        listView.setAdapter(adapter);
        setData();
        left.setOnClickListener(view -> finish());
        title.setText("充值历史");
    }

    void setData() {
        APPApi.getInstance().service
                .getRechargeHistory(UserBean.user.id,"0")
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<RechargeHistoryBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(RechargeHistoryBean value) {
                        if (value.responseStatus.equals("0")) {
                            rechargeHistories.obj.clear();
                            rechargeHistories.obj.addAll(value.obj);
                            adapter.notifyDataSetChanged();
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
