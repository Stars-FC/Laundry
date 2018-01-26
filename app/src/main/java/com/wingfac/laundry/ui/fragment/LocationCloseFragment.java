package com.wingfac.laundry.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.LocationMod;
import com.wingfac.laundry.bean.NearbyCloseBean;
import com.wingfac.laundry.ui.activity.AppointmentWashActivity;
import com.wingfac.laundry.ui.adapter.NearbyCloseAdapter;
import com.yuyh.library.utils.toast.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/7 0007.
 */

public class LocationCloseFragment extends Fragment implements AMapLocationListener {
    @Bind(R.id.fragment_location_colse_list)
    PullToRefreshListView listView;
    @Bind(R.id.fragment_location_close_address)
    TextView address;
    NearbyCloseBean list = new NearbyCloseBean();
    NearbyCloseAdapter adapter;
    public AMapLocationClientOption mLocationOption = null;
    public AMapLocationClient mlocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_colse, null, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    void initData() {
        adapter = new NearbyCloseAdapter(getActivity(), list);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), AppointmentWashActivity.class);
                intent.putExtra("aId",String.valueOf(list.obj.get(i-1).a_id));
                intent.putExtra("eId",String.valueOf(list.obj.get(i-1).e_id));
                startActivity(intent);
            }
        });
        if (LocationMod.s_latitude == 0) {
            initLocation();
        } else {
            setData();
            address.setText("当前位置:"+LocationMod.address);
        }

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
                setData();
                address.setText("当前位置:"+LocationMod.address);
            } else {
                ToastUtils.showToast("定位失败");
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    void setData() {
        APPApi.getInstance().service
                .getLocationClose(String.valueOf(LocationMod.s_longitude), String.valueOf(LocationMod.s_latitude))
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<NearbyCloseBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(NearbyCloseBean value) {
                        if (value.responseStatus.equals("0")) {
                            list.obj.addAll(value.obj);
                        } else {
                            ToastUtils.showToast(value.msg);
                        }
                        adapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                        ToastUtils.showToast("请检查您的网络设置");
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

}
