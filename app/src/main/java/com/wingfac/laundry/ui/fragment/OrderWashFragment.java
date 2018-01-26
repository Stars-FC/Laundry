package com.wingfac.laundry.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.wingfac.laundry.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/1 0001.
 */

public class OrderWashFragment extends Fragment {
    @Bind(R.id.fragment_home_order_wash_page)
    ViewPager viewPager;
    OrderListFragment oneFragment, twoFragment, threeFragment, fourFragment, fiveFragment;
    List<Fragment> list = new ArrayList<>();
    @Bind(R.id.fragment_home_order_wash_one)
    RelativeLayout one;
    @Bind(R.id.fragment_home_order_wash_one_view)
    View oneView;
    @Bind(R.id.fragment_home_order_wash_two)
    RelativeLayout two;
    @Bind(R.id.fragment_home_order_wash_two_view)
    View twoView;
    @Bind(R.id.fragment_home_order_wash_three)
    RelativeLayout three;
    @Bind(R.id.fragment_home_order_wash_three_view)
    View threeView;
    @Bind(R.id.fragment_home_order_wash_four)
    RelativeLayout four;
    @Bind(R.id.fragment_home_order_wash_four_view)
    View fourView;
    @Bind(R.id.fragment_home_order_wash_five)
    RelativeLayout five;
    @Bind(R.id.fragment_home_order_wash_five_view)
    View fiveView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_order_wash, null, false);
        ButterKnife.bind(this, view);
        select();
        oneView.setVisibility(View.VISIBLE);
        initData();
        return view;
    }

    void initData() {
        oneFragment = new OrderListFragment();
        oneFragment.getType("1");
        twoFragment = new OrderListFragment();
        twoFragment.getType("2");
        threeFragment = new OrderListFragment();
        threeFragment.getType("3");
        fourFragment = new OrderListFragment();
        fourFragment.getType("4");
        fiveFragment = new OrderListFragment();
        fiveFragment.getType("5");
        list.add(oneFragment);
        list.add(twoFragment);
        list.add(threeFragment);
        list.add(fourFragment);
        list.add(fiveFragment);
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
        one.setOnClickListener(view -> {
            viewPager.setCurrentItem(0);
            select();
            oneView.setVisibility(View.VISIBLE);
        });
        two.setOnClickListener(view -> {
            viewPager.setCurrentItem(1);
            select();
            twoView.setVisibility(View.VISIBLE);
        });
        three.setOnClickListener(view -> {
            viewPager.setCurrentItem(2);
            select();
            threeView.setVisibility(View.VISIBLE);
        });
        four.setOnClickListener(view -> {
            viewPager.setCurrentItem(3);
            select();
            fourView.setVisibility(View.VISIBLE);
        });
        five.setOnClickListener(view -> {
            viewPager.setCurrentItem(4);
            select();
            fiveView.setVisibility(View.VISIBLE);
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        select();
                        oneView.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        select();
                        twoView.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        select();
                        threeView.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        select();
                        fourView.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        select();
                        fiveView.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    void delete() {
        switch (viewPager.getCurrentItem()) {
            case 0:
                oneFragment.delete();
                break;
            case 1:
                twoFragment.delete();
                break;
            case 2:
                threeFragment.delete();
                break;
            case 3:
                fourFragment.delete();
                break;
            case 4:
                fiveFragment.delete();
                break;
        }
    }

    void select() {
        oneView.setVisibility(View.GONE);
        twoView.setVisibility(View.GONE);
        threeView.setVisibility(View.GONE);
        fourView.setVisibility(View.GONE);
        fiveView.setVisibility(View.GONE);
    }
}
