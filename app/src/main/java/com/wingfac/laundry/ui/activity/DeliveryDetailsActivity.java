package com.wingfac.laundry.ui.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.bean.WashMessageBean;
import com.wingfac.laundry.bean.base.Constant;
import com.wingfac.laundry.ui.activity.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/7 0007.
 */

public class DeliveryDetailsActivity extends BaseActivity {
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.head_layout_right)
    Button right;
    PopupWindow orderWindow;
    @Bind(R.id.activity_delivery_details_qr)
    TextView qr;
    @Bind(R.id.content_layout)
    LinearLayout contentLayout;
    @Bind(R.id.activity_delivery_details_address)
    TextView address;
    @Bind(R.id.activity_delivery_details_password)
    TextView password;
    @Bind(R.id.activity_delivery_details_name)
    TextView name;
    @Bind(R.id.activity_delivery_details_gunk)
    TextView gunk;
    WashMessageBean.WashMessage washMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);
        ButterKnife.bind(this);
        washMessage = (WashMessageBean.WashMessage) getIntent().getSerializableExtra("washMessage");
        initData();
    }

    void initData() {
        left.setOnClickListener(view -> finish());
        title.setText("取件详情");
        qr.setOnClickListener(view -> showOrderPopWindow(contentLayout, R.layout.window_qr));
        address.setText(washMessage.address);
        password.setText(washMessage.pickup_password);
        name.setText(washMessage.goods_name);
        gunk.setText(washMessage.guest_book);
    }

    private void showOrderPopWindow(View parentView, int convertViewResource) {
        //创建一个popUpWindow
        View popLayout = LayoutInflater.from(getActivity()).inflate(convertViewResource, null);

        ImageView qr =  popLayout.findViewById(R.id.windows_qr);
        Glide.with(getActivity()).load(washMessage.pickup_code).into(qr);
        if (orderWindow == null) {
            //实例化一个popupWindow
            orderWindow =
                    new PopupWindow(popLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //产生背景变暗效果
            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.alpha = 0.4f;
            getActivity().getWindow().setAttributes(lp);
            //点击外面popupWindow消失
            orderWindow.setOutsideTouchable(true);
            //popupWindow获取焦点
            orderWindow.setFocusable(true);
            //popupWindow设置背景图
            orderWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            //popupWindow设置开场动画风格
            //popupWindow.setAnimationStyle(R.style.popupWindow_anim);
            //刷新popupWindow
            //popupWindow.update();

            //设置popupWindow消失时的监听
            orderWindow.setOnDismissListener(() -> {
                WindowManager.LayoutParams lp1 = getActivity().getWindow().getAttributes();
                lp1.alpha = 1f;
                getActivity().getWindow().setAttributes(lp1);
            });
            orderWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        } else {
            //如果popupWindow正在显示，接下来隐藏
            if (orderWindow.isShowing()) {
                orderWindow.dismiss();
            } else {
                //产生背景变暗效果
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 0.4f;
                getActivity().getWindow().setAttributes(lp);
                orderWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
            }
        }
    }
}
