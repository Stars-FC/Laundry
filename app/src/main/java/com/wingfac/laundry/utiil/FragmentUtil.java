package com.wingfac.laundry.utiil;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.wingfac.laundry.R;
import com.wingfac.laundry.ui.fragment.HomeHomeFragment;
import com.wingfac.laundry.ui.fragment.HomeMineFragment;
import com.wingfac.laundry.ui.fragment.HomeOrderFragment;
import com.wingfac.laundry.ui.fragment.HomeShopFragment;

/**
 * Created by zhanghao on 2017/3/17.
 */

public class FragmentUtil {
    private HomeHomeFragment homeHomeFragment;
    private HomeShopFragment homeShopFragment;
    private HomeOrderFragment homeOrderFragment;
    private HomeMineFragment homeMineFragment;
    private FragmentManager mManager;
    private FragmentTransaction mTransaction;

    public FragmentUtil(AppCompatActivity activity) {
        mManager = activity.getSupportFragmentManager();
    }

    public void initFragment(String name) {
        mTransaction = mManager.beginTransaction();

        hideFragments(mTransaction);
        switch (name) {
            case "one":
                if (homeHomeFragment == null) {
                    homeHomeFragment = new HomeHomeFragment();
                    mTransaction.add(R.id.content, homeHomeFragment);
                } else
                    mTransaction.show(homeHomeFragment);
                break;
            case "two":
                if (homeShopFragment == null) {
                    homeShopFragment = new HomeShopFragment();
                    mTransaction.add(R.id.content, homeShopFragment);
                } else
                    mTransaction.show(homeShopFragment);
                break;
            case "three":
                if (homeOrderFragment == null) {
                    homeOrderFragment = new HomeOrderFragment();
                    mTransaction.add(R.id.content, homeOrderFragment);
                } else
                    mTransaction.show(homeOrderFragment);
                break;
            case "four":
                if (homeMineFragment == null) {
                    homeMineFragment = new HomeMineFragment();
                    mTransaction.add(R.id.content, homeMineFragment);
                } else
                    mTransaction.show(homeMineFragment);
                break;
            default:
                return;
        }
        mTransaction.commit();
    }

    private void hideFragments(FragmentTransaction mTransaction) {
        if (homeHomeFragment != null) mTransaction.hide(homeHomeFragment);
        if (homeShopFragment != null) mTransaction.hide(homeShopFragment);
        if (homeOrderFragment != null) mTransaction.hide(homeOrderFragment);
        if (homeMineFragment != null) mTransaction.hide(homeMineFragment);
    }

}
