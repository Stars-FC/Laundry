package com.wingfac.laundry.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.app.MyApplication;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.base.Constant;
import com.wingfac.laundry.ui.activity.LoginActivity;
import com.wingfac.laundry.ui.activity.MineAddressActivity;
import com.wingfac.laundry.ui.activity.MineCartActivity;
import com.wingfac.laundry.ui.activity.MineComplaintSuggestionActivity;
import com.wingfac.laundry.ui.activity.MineRechargeActivity;
import com.wingfac.laundry.ui.activity.MineUserActivity;
import com.wingfac.laundry.weight.CircleImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/7/25 0025.
 */

public class HomeMineFragment extends Fragment {
    @Bind(R.id.fragment_mine_cart)
    RelativeLayout cart;
    @Bind(R.id.fragment_mine_user)
    RelativeLayout user;
    @Bind(R.id.fragment_mine_complaint)
    RelativeLayout complaint;
    @Bind(R.id.fragment_mine_address)
    RelativeLayout address;
    @Bind(R.id.fragment_mine_out)
    RelativeLayout out;
    @Bind(R.id.fragment_mine_vip)
    RelativeLayout vip;
    Intent intent;
    @Bind(R.id.fragment_mine_icon)
    CircleImageView icon;
    @Bind(R.id.fragment_mine_name)
    TextView name;
    @Bind(R.id.fragment_mine_mobile)
    TextView mobile;
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.fragment_mine_call_me)
    RelativeLayout callMe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, null, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mobile.setText(UserBean.user.tel);
        name.setText(UserBean.user.nickname);
        Glide.with(getActivity())
                .load(Constant.BASE_IMG + UserBean.user.headPortrait)
                .dontAnimate()
                .placeholder(R.drawable.default_icon)
                .into(icon);
        left.setVisibility(View.GONE);
        title.setText("我的");
    }

    void initData() {
        cart.setOnClickListener(view -> {
            intent = new Intent(getActivity(), MineCartActivity.class);
            startActivity(intent);
        });
        user.setOnClickListener(view -> {
            intent = new Intent(getActivity(), MineUserActivity.class);
            startActivity(intent);
        });
        complaint.setOnClickListener(view -> {
            intent = new Intent(getActivity(), MineComplaintSuggestionActivity.class);
            intent.putExtra("state","1");
            startActivity(intent);
        });
        address.setOnClickListener(view -> {
            intent = new Intent(getActivity(), MineAddressActivity.class);
            startActivity(intent);
        });
        out.setOnClickListener(view -> {
            MyApplication.getAcache().put("userName", "");
            MyApplication.getAcache().put("passWord", "");
            UserBean.user = null;
            JPushInterface.setAlias(getActivity(), "", (i, s, set) -> {

            });
            intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });
        vip.setOnClickListener(view -> {
            intent = new Intent(getActivity(), MineRechargeActivity.class);
            startActivity(intent);
        });
        callMe.setOnClickListener(view -> {
            intent = new Intent(getActivity(), MineComplaintSuggestionActivity.class);
            intent.putExtra("state","2");
            startActivity(intent);
        });
    }
}
