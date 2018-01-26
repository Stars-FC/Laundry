package com.wingfac.laundry.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.weight.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/1 0001.
 */

public class HomeOrderFragment extends Fragment {
    @Bind(R.id.fragment_order_view_page)
    NoScrollViewPager viewPager;
    @Bind(R.id.fragment_order_wash_button)
    Button washButton;
    @Bind(R.id.fragment_order_shopping_button)
    Button shoppingButton;
    @Bind(R.id.fragment_order_delete)
    ImageView delete;
    OrderWashFragment orderWashFragment;
    OrderShoppingFragment orderShoppingFragment;
    private List<Fragment> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, null, false);
        ButterKnife.bind(this, view);
        select();
        washButton.setBackgroundResource(R.drawable.left_button_select);
        washButton.setTextColor(getResources().getColor(R.color.them));
        initData();
        return view;
    }

    void initData() {
        delete.setOnClickListener(view -> {
            switch (viewPager.getCurrentItem()) {
                case 0:
                    orderWashFragment.delete();
                    break;
                case 1:
                    orderShoppingFragment.delete();
                    break;
            }
        });
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

        orderWashFragment = new OrderWashFragment();
        orderShoppingFragment = new OrderShoppingFragment();
        list.add(orderWashFragment);
        list.add(orderShoppingFragment);
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
