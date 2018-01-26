package com.wingfac.laundry.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.ui.activity.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class MineComplaintSuggestionActivity extends BaseActivity {
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.bottom)
    RelativeLayout bottom;
    private String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_complaint_suggestion);
        ButterKnife.bind(this);
        state = getIntent().getStringExtra("state");
        initData();
    }

    void initData() {
        left.setOnClickListener(view -> finish());
        if (state.equals("1")) {
            title.setText("投诉建议");
            bottom.setVisibility(View.GONE);
        } else {
            title.setText("联系我们");
            bottom.setVisibility(View.VISIBLE);
        }
    }
}
