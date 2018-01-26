package com.wingfac.laundry.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.ui.activity.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/9 0009.
 */

public class MineRechargeActivity extends BaseActivity {
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.rl_recharge)
    RelativeLayout recharge;
    @Bind(R.id.rl_recharge_history)
    RelativeLayout rechargeHistory;
    @Bind(R.id.rl_payment_password)
    RelativeLayout password;
    @Bind(R.id.rl_consumption_list)
    RelativeLayout order;
    @Bind(R.id.activity_mine_vip_yue)
    TextView yue;
    @Bind(R.id.activity_mine_vip_code)
    TextView cardCode;
    @Bind(R.id.activity_mine_vip_type)
    TextView type;
    @Bind(R.id.activity_mine_vip_start)
    TextView startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_vip);
        ButterKnife.bind(this);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUser();
    }

    void getUser() {
        APPApi.getInstance().service
                .getUser(UserBean.user.id)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<UserBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UserBean value) {
                        if (value.responseStatus.equals("0")) {
                            UserBean.user = value.obj;
                            yue.setText(value.obj.balance);
                            cardCode.setText("卡号: "+value.obj.cardNumber);
                            type.setText("等级: "+value.obj.grade);
                            startTime.setText("开始时间: "+value.obj.createTime);

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

    void initData() {
        left.setOnClickListener(view -> finish());
        title.setText("我的会员卡");
        recharge.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), MineRechargeNextActivity.class);
            startActivity(intent);
        });
        rechargeHistory.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), MineRechargeHistoryActivity.class);
            startActivity(intent);
        });
        password.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), MineRechargePasswordActivity.class);
            startActivity(intent);
        });
        order.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("check", "3");
            startActivity(intent);
        });
    }
}
