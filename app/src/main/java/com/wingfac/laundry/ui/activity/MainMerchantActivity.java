package com.wingfac.laundry.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wingfac.laundry.R;
import com.wingfac.laundry.app.MyApplication;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.runtimepermissions.PermissionsManager;
import com.wingfac.laundry.runtimepermissions.PermissionsResultAction;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.ui.fragment.CommercialActivityMoneyFragment;
import com.wingfac.laundry.ui.fragment.CommercialActivityOrderFragment;
import com.wingfac.laundry.ui.fragment.CommercialActivityShopFragment;
import com.wingfac.laundry.ui.fragment.CommercialActivityStoreFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;


/**
 * Created by Administrator on 2017/5/18 0018.
 */

public class MainMerchantActivity extends BaseActivity implements View.OnClickListener{
    public static final String fragment1Tag = "fragment1";
    public static final String fragment2Tag = "fragment2";
    public static final String fragment3Tag = "fragment3";
    public static final String fragment4Tag = "fragment4";
    public static int index;
    @Bind(R.id.tab_home)
    LinearLayout tab_home;
    @Bind(R.id.tab_home_img)
    ImageView tab_home_img;
    @Bind(R.id.tab_home_text)
    TextView tab_home_text;
    @Bind(R.id.tab_integral)
    LinearLayout tab_integral;
    @Bind(R.id.tab_integral_img)
    ImageView tab_integral_img;
    @Bind(R.id.tab_integral_text)
    TextView tab_integral_text;
    @Bind(R.id.tab_cart)
    LinearLayout tab_cart;
    @Bind(R.id.tab_cart_img)
    ImageView tab_cart_img;
    @Bind(R.id.tab_cart_text)
    TextView tab_cart_text;
    @Bind(R.id.tab_mine)
    LinearLayout tab_mine;
    @Bind(R.id.tab_mine_img)
    ImageView tab_mine_img;
    @Bind(R.id.tab_mine_text)
    TextView tab_mine_text;
    @Bind(R.id.activity_main_appointment)
    RelativeLayout out;
    Fragment fragment1, fragment2, fragment3, fragment4;
    FragmentManager fm;
    FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_main);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = new CommercialActivityStoreFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content, fragment, fragment1Tag).commit();
        }
        initPermissions();
        initData();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        myApp.isApplicationBroughtToBackground(getActivity());
    }


    @Override
    protected void onStop() {
        super.onStop();
//        myApp.isApplicationBroughtToBackground(getActivity());
    }

    private void initData() {
        select();
        tab_home_img.setImageResource(R.drawable.storepress);
        tab_home_text.setTextColor(getResources().getColor(R.color.them));
        tab_home.setOnClickListener(this);
        tab_integral.setOnClickListener(this);
        tab_cart.setOnClickListener(this);
        tab_mine.setOnClickListener(this);
        out.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            MyApplication.getAcache().put("userName", "");
            MyApplication.getAcache().put("passWord", "");
            UserBean.user = null;
            JPushInterface.setAlias(getActivity(), "", (i, s, set) -> {

            });
            intent.putExtra("state","1");
            startActivity(intent);
            finish();
        });
    }

    private void initPermissions() {
        /**
         * 请求所有必要的权限----
         */
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                //Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
                //Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        fragment1 = fm.findFragmentByTag(fragment1Tag);
        fragment2 = fm.findFragmentByTag(fragment2Tag);
        fragment3 = fm.findFragmentByTag(fragment3Tag);
        fragment4 = fm.findFragmentByTag(fragment4Tag);
        if (fragment1 != null) {
            ft.hide(fragment1);
        }
        if (fragment2 != null) {
            ft.hide(fragment2);
        }
        if (fragment3 != null) {
            ft.hide(fragment3);
        }
        if (fragment4 != null) {
            ft.hide(fragment4);
        }
        switch (view.getId()) {
            case R.id.tab_home:
                index = 0;
                if (fragment1 == null) {
                    fragment1 = new CommercialActivityStoreFragment();
                    ft.add(R.id.content, fragment1, fragment1Tag);
                } else {
                    ft.show(fragment1);
                }
                select();
                tab_home_img.setImageResource(R.drawable.storepress);
                tab_home_text.setTextColor(getResources().getColor(R.color.them));
                break;
            case R.id.tab_integral:
                index = 1;
                if (fragment2 == null) {
                    fragment2 = new CommercialActivityShopFragment();
                    ft.add(R.id.content, fragment2, fragment2Tag);
                } else {
                    ft.show(fragment2);
                }
                select();
                tab_integral_img.setImageResource(R.drawable.goodspress);
                tab_integral_text.setTextColor(getResources().getColor(R.color.them));
                break;
            case R.id.tab_cart:
                index = 2;
                if (fragment3 == null) {
                    fragment3 = new CommercialActivityOrderFragment();
                    ft.add(R.id.content, fragment3, fragment3Tag);
                } else {
                    ft.show(fragment3);
                }
                select();
                tab_cart_img.setImageResource(R.drawable.iconorderpress);
                tab_cart_text.setTextColor(getResources().getColor(R.color.them));
                break;
            case R.id.tab_mine:
                index = 3;
                if (fragment4 == null) {
                    fragment4 = new CommercialActivityMoneyFragment();
                    ft.add(R.id.content, fragment4, fragment4Tag);
                } else {
                    ft.show(fragment4);
                }
                select();
                tab_mine_img.setImageResource(R.drawable.financepress);
                tab_mine_text.setTextColor(getResources().getColor(R.color.them));
                break;
        }
        ft.commit();
    }

    void select() {
        tab_home_img.setImageResource(R.drawable.store);
        tab_home_text.setTextColor(getResources().getColor(R.color.black));
        tab_cart_img.setImageResource(R.drawable.order);
        tab_cart_text.setTextColor(getResources().getColor(R.color.black));
        tab_mine_img.setImageResource(R.drawable.finance);
        tab_mine_text.setTextColor(getResources().getColor(R.color.black));
        tab_integral_img.setImageResource(R.drawable.goods);
        tab_integral_text.setTextColor(getResources().getColor(R.color.black));
    }

    /**
     * 返回键响应
     *
     * @param keyCode 状态
     * @param event
     * @return
     */

    //重写onKeyDown方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断当点击的是返回键
        if (keyCode == event.KEYCODE_BACK) {
            exit();//退出方法
        }
        return true;
    }
    //退出方法
    private long time = 0;
    private void exit() {

        //如果在两秒大于2秒

        if (System.currentTimeMillis() - time > 2000) {

            //获得当前的时间

            time = System.currentTimeMillis();

            Toast.makeText(getActivity(), "再点击一次退出应用程序", Toast.LENGTH_SHORT).show();

        } else {

            //点击在两秒以内
//            floatView.removeFromWindow();
            removeALLActivity();

        }

    }

}
