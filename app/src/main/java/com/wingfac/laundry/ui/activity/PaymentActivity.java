package com.wingfac.laundry.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.zhy.autolayout.AutoRelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/5/11.
 */

public class PaymentActivity extends BaseActivity {

    @Bind(R.id.title_img)
    ImageView titleImg;
    @Bind(R.id.head_layout_left)
    AutoRelativeLayout headLayoutLeft;
    @Bind(R.id.head_layout_title)
    TextView headLayoutTitle;
    @Bind(R.id.head_layout_right)
    Button headLayoutRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        headLayoutLeft.setOnClickListener(view -> finish());
        headLayoutTitle.setText("付款");
    }
}
