package com.wingfac.laundry.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.ui.activity.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MineRecyclingActivity extends BaseActivity {
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.head_layout_right)
    Button headLayoutRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_recycling);
        ButterKnife.bind(this);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void initData() {
        left.setOnClickListener(view -> finish());
        title.setText("旧衣回收");
        headLayoutRight.setText("回收历史");
        headLayoutRight.setVisibility(View.VISIBLE);
        headLayoutRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MineRecyclingActivity.this,RecoveryHistoryActivity.class);
                startActivity(intent);
            }
        });
    }
}
