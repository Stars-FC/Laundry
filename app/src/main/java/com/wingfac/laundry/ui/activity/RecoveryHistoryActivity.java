package com.wingfac.laundry.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/5/28.
 */

public class RecoveryHistoryActivity extends BaseActivity {

    @Bind(R.id.title_img)
    ImageView titleImg;
    @Bind(R.id.head_layout_left)
    AutoRelativeLayout headLayoutLeft;
    @Bind(R.id.head_layout_title)
    TextView headLayoutTitle;
    @Bind(R.id.head_layout_right)
    Button headLayoutRight;
    @Bind(R.id.al_text)
    AutoLinearLayout alText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_recycling_recovery_history);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        headLayoutLeft.setOnClickListener(view -> finish());
        headLayoutTitle.setText("回收历史");
        headLayoutRight.setText("清除");
    }
}
