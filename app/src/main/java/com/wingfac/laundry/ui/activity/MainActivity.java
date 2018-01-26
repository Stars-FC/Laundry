package com.wingfac.laundry.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.CanCloseBean;
import com.wingfac.laundry.bean.LocationMod;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.utiil.FragmentUtil;
import com.yuyh.library.utils.toast.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements AMapLocationListener {
    @Bind(R.id.tab_home)
    LinearLayout homeLayout;
    @Bind(R.id.tab_integral)
    LinearLayout shopLayout;
    @Bind(R.id.tab_cart)
    LinearLayout orderLayout;
    @Bind(R.id.tab_mine)
    LinearLayout mineLayout;
    @Bind(R.id.tab_home_img)
    ImageView homeImg;
    @Bind(R.id.tab_integral_img)
    ImageView shopImg;
    @Bind(R.id.tab_cart_img)
    ImageView orderImg;
    @Bind(R.id.tab_mine_img)
    ImageView mineImg;
    @Bind(R.id.tab_home_text)
    TextView homeText;
    @Bind(R.id.tab_integral_text)
    TextView shopText;
    @Bind(R.id.tab_cart_text)
    TextView orderText;
    @Bind(R.id.tab_mine_text)
    TextView mineText;
    @Bind(R.id.activity_main_appointment)
    RelativeLayout appointment;
    private FragmentUtil fragmentUtil;
    private long time = 0;
    public AMapLocationClientOption mLocationOption = null;
    public AMapLocationClient mlocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        fragmentUtil = new FragmentUtil(getActivity());
        if (getIntent().getStringExtra("check") != null) {
            select();
            orderImg.setImageResource(R.drawable.tab_order_select);
            orderText.setTextColor(getResources().getColor(R.color.them));
            fragmentUtil.initFragment("three");
        } else {
            select();
            homeImg.setImageResource(R.drawable.tab_home_select);
            homeText.setTextColor(getResources().getColor(R.color.them));
            fragmentUtil.initFragment("one");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        i = 0;
    }

    void select() {
        homeImg.setImageResource(R.drawable.tab_home);
        homeText.setTextColor(getResources().getColor(R.color.default_text));
        shopImg.setImageResource(R.drawable.tab_shop);
        shopText.setTextColor(getResources().getColor(R.color.default_text));
        orderImg.setImageResource(R.drawable.tab_order);
        orderText.setTextColor(getResources().getColor(R.color.default_text));
        mineImg.setImageResource(R.drawable.tab_mine);
        mineText.setTextColor(getResources().getColor(R.color.default_text));
    }

    public static int i = 0;

    void initData() {
        homeLayout.setOnClickListener(view -> {
            select();
            homeImg.setImageResource(R.drawable.tab_home_select);
            homeText.setTextColor(getResources().getColor(R.color.them));
            fragmentUtil.initFragment("one");
        });
        shopLayout.setOnClickListener(view -> {
            select();
            shopImg.setImageResource(R.drawable.tab_shop_select);
            shopText.setTextColor(getResources().getColor(R.color.them));
            fragmentUtil.initFragment("two");
        });
        orderLayout.setOnClickListener(view -> {
            select();
            orderImg.setImageResource(R.drawable.tab_order_select);
            orderText.setTextColor(getResources().getColor(R.color.them));
            fragmentUtil.initFragment("three");
        });
        mineLayout.setOnClickListener(view -> {
            select();
            mineImg.setImageResource(R.drawable.tab_mine_select);
            mineText.setTextColor(getResources().getColor(R.color.them));
            fragmentUtil.initFragment("four");
        });
        appointment.setOnClickListener(view -> {
            if (LocationMod.s_latitude == 0) {
                if (i == 0) {
                    initLocation();
                }
            } else {
                if (i == 0) {
                    canGo();
                }
            }
        });
    }

    void canGo() {
        APPApi.getInstance().service
                .canClose(String.valueOf(LocationMod.s_longitude), String.valueOf(LocationMod.s_latitude))
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<CanCloseBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CanCloseBean value) {
                        if (value.responseStatus.equals("0")) {
                            i += 1;
                            Intent intent = new Intent(getActivity(), AppointmentWashActivity.class);
                            intent.putExtra("aId", String.valueOf(value.obj.get(0).a_id));
                            intent.putExtra("eId", String.valueOf(value.obj1));
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void initLocation() {
        //声明mLocationOption对象
        mlocationClient = new AMapLocationClient(getActivity());
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                LocationMod.s_latitude = amapLocation.getLatitude();
                LocationMod.s_longitude = amapLocation.getLongitude();
                LocationMod.address = amapLocation.getAddress();
                mlocationClient.onDestroy();
                canGo();
            } else {
                ToastUtils.showToast("定位失败");
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
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

    private void exit() {

        //如果在两秒大于2秒

        if (System.currentTimeMillis() - time > 2000) {

            //获得当前的时间

            time = System.currentTimeMillis();

            Toast.makeText(MainActivity.this, "再点击一次退出应用程序", Toast.LENGTH_SHORT).show();

        } else {

            //点击在两秒以内

            removeALLActivity();

        }

    }

}
