package com.wingfac.laundry.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.ui.fragment.LocationCloseFragment;
import com.wingfac.laundry.ui.fragment.LocationShopFragment;
import com.wingfac.laundry.weight.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/7 0007.
 */

public class HomeLocationActivity extends BaseActivity {
    @Bind(R.id.activity_home_location)
    NoScrollViewPager viewPager;
    @Bind(R.id.fragment_order_wash_button)
    Button washButton;
    @Bind(R.id.fragment_order_shopping_button)
    Button shoppingButton;
    @Bind(R.id.title_img)
    ImageView left;
    LocationShopFragment shopFragment;
    LocationCloseFragment closeFragment;
    private List<Fragment> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_location);
        ButterKnife.bind(this);
        initData();
    }

    void initData() {
        left.setOnClickListener(view -> finish());
        washButton.setOnClickListener(view -> {
            viewPager.setCurrentItem(0);
            select();
            washButton.setBackgroundResource(R.drawable.left_button_select);
            washButton.setTextColor(getResources().getColor(R.color.them));
        });
        shoppingButton.setOnClickListener(view -> {
            viewPager.setCurrentItem(1);
            select();
            shoppingButton.setBackgroundResource(R.drawable.right_button_select);
            shoppingButton.setTextColor(getResources().getColor(R.color.them));
        });
        shopFragment = new LocationShopFragment();
        closeFragment = new LocationCloseFragment();
        list.add(closeFragment);
        list.add(shopFragment);
        // 设置适配器
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(
                getActivity().getSupportFragmentManager()) {
            @Override
            public int getCount() {

                return list.size();

            }

            @Override
            public Fragment getItem(int arg0) {

                return list.get(arg0);

            }
        };
        // 绑定适配器
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.setOnTouchListener((view, motionEvent) -> true);
    }

    void select() {
        washButton.setBackgroundResource(R.drawable.left_button);
        washButton.setTextColor(getResources().getColor(R.color.white));
        shoppingButton.setBackgroundResource(R.drawable.right_button);
        shoppingButton.setTextColor(getResources().getColor(R.color.white));
    }
}
