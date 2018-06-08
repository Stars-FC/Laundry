package com.wingfac.laundry.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.app.MyApplication;
import com.wingfac.laundry.bean.LocationMod;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.runtimepermissions.PermissionsManager;
import com.wingfac.laundry.runtimepermissions.PermissionsResultAction;
import com.yuyh.library.utils.toast.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/3/15 0015.
 */

public class InitActivity extends AppCompatActivity implements AMapLocationListener {
    private final String TAG = "InitActivity";
    public AMapLocationClientOption mLocationOption = null;
    public AMapLocationClient mlocationClient;
    @Bind(R.id.init_image)
    ImageView img;
    Intent intent;
    private Animation animaation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.flag = 0;
        setContentView(R.layout.activity_init);
        ButterKnife.bind(this);
        initPermissions();
        initLocation();
        initData();
        initViewOper();
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

    /**
     * 加载布局动画
     */
    public void initData() {
        animaation = AnimationUtils.loadAnimation(this, R.anim.init_alpha);
    }

    public void initViewOper() {
        img.startAnimation(animaation);

        animaation.setAnimationListener(new Animation.AnimationListener() {
            /**
             * 动画开始
             */
            @Override
            public void onAnimationStart(Animation animation) {

            }

            /**
             * 动画重复
             */
            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            /**
             * 动画结束
             */
            @Override
            public void onAnimationEnd(Animation animation) {
                if (MyApplication.getAcache().getAsString("userName") != null && MyApplication.getAcache().getAsString("passWord") != null) {
                    if (!MyApplication.getAcache().getAsString("userName").equals("") && !MyApplication.getAcache().getAsString("passWord").equals("")) {
                        login(MyApplication.getAcache().getAsString("userName"), MyApplication.getAcache().getAsString("passWord"));
                    } else {
                        intent = new Intent(InitActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    intent = new Intent(InitActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

        });
    }

    void login(String phone, String password) {
        APPApi.getInstance().service
                .login(phone, password)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<UserBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UserBean value) {
                        if (value.responseStatus.equals("0")) {
                            JPushInterface.setAlias(InitActivity.this, value.obj.tel, (i, s, set) -> {

                            });
                            UserBean.user = value.obj;
                            UserBean.userStore = value.obj1;
                            if (value.obj.type == 1) {
                                intent = new Intent(InitActivity.this, MainMerchantActivity.class);
                            } else {
                                intent = new Intent(InitActivity.this, MainActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            ToastUtils.showToast(value.msg);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showToast("请检查您的网络设置");
                        finish();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void initLocation() {
        //声明mLocationOption对象
        mlocationClient = new AMapLocationClient(this);
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
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }
}
