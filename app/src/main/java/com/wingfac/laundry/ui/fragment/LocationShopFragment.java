package com.wingfac.laundry.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.InfoBean;
import com.wingfac.laundry.bean.LocationGridBean;
import com.wingfac.laundry.bean.LocationMod;
import com.wingfac.laundry.bean.StoreBean;
import com.wingfac.laundry.bean.TitleClassBean;
import com.wingfac.laundry.bean.TwoClassBean;
import com.wingfac.laundry.ui.activity.ShopHomeActivity;
import com.wingfac.laundry.ui.adapter.LocationShopGridAdapter;
import com.wingfac.laundry.ui.adapter.LocationShopListAdapter;
import com.wingfac.laundry.weight.LoadingDialog;
import com.yuyh.library.utils.DimenUtils;
import com.yuyh.library.utils.toast.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/7 0007.
 */

public class LocationShopFragment extends Fragment implements View.OnClickListener, AMapLocationListener {
    @Bind(R.id.fragment_location_shop_layout)
    LinearLayout linearLayout;
    @Bind(R.id.fragment_location_shop_list)
    PullToRefreshListView listView;
    @Bind(R.id.fragment_location_shop_grid)
    GridView gridView;
    @Bind(R.id.fragment_location_shop_address)
    TextView address;
    LocationGridBean locationGridBean = new LocationGridBean();
    LocationShopGridAdapter adapter;
    StoreBean storeBean = new StoreBean();
    LocationShopListAdapter locationShopListAdapter;
    List<View> checkBoxList = new ArrayList<>();
    List<InfoBean> title = new ArrayList<>();
    public AMapLocationClientOption mLocationOption = null;
    public AMapLocationClient mlocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_shop, null, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    void getTitleClass() {
        APPApi.getInstance().service
                .getOneClass()
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<TitleClassBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(TitleClassBean value) {
                        if (value.responseStatus.equals("0")) {
                            for (int i = 0; i < value.obj.size(); i++) {
                                InfoBean infoBean = new InfoBean(value.obj.get(i).lm_name, value.obj.get(i).lm_picture_path, value.obj.get(i).lm_id);
                                title.add(infoBean);
                            }
                            for (int i = 0; i < title.size(); i++) {
                                addViewByJava(i);
                            }
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

    void getShop(Integer tlm_id) {
        APPApi.getInstance().service
                .getShop(String.valueOf(tlm_id), String.valueOf(LocationMod.s_longitude), String.valueOf(LocationMod.s_latitude))
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<StoreBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(StoreBean value) {
                        LoadingDialog.mDialog.dismiss();
                        storeBean.obj.clear();
                        if (value.responseStatus.equals("0")) {
                            storeBean.obj.addAll(value.obj);
                        } else {
                            Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                        }
                        locationShopListAdapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialog.mDialog.dismiss();
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void initData() {
        if (LocationMod.s_latitude == 0) {
            initLocation();
        } else {
            getTitleClass();
            address.setText("当前位置:"+LocationMod.address);
        }
        adapter = new LocationShopGridAdapter(getActivity(), locationGridBean);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            for (int j = 0; j < locationGridBean.data.size(); j++) {
                locationGridBean.data.get(j).state = false;
            }
            locationGridBean.data.get(i).state = true;
            adapter.notifyDataSetChanged();

            LoadingDialog.showRoundProcessDialog(getActivity());
            getShop(locationGridBean.data.get(i).tlm_id);
        });
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
        listView.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载中");
        listView.getLoadingLayoutProxy(false, true).setReleaseLabel("加载完毕");
        locationShopListAdapter = new LocationShopListAdapter(getActivity(), storeBean);
        listView.setAdapter(locationShopListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ShopHomeActivity.class);
                intent.putExtra("s_id",storeBean.obj.get(i-1).s_id);
                startActivity(intent);
            }
        });

    }

    void getTwoClass(Integer lm_id) {
        APPApi.getInstance().service
                .getTwoClass(String.valueOf(lm_id))
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<TwoClassBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(TwoClassBean value) {
                        if (value.responseStatus.equals("0")) {
                            locationGridBean.data.clear();
                            for (int i = 0; i < value.obj.size(); i++) {
                                LocationGridBean.LocationGrid locationGrid = locationGridBean.new LocationGrid();
                                locationGrid.name = value.obj.get(i).tlm_name;
                                locationGrid.tlm_id = value.obj.get(i).tlm_id;
                                if (i == 0)
                                    locationGrid.state = true;
                                else
                                    locationGrid.state = false;
                                locationGridBean.data.add(locationGrid);
                            }
                            adapter.notifyDataSetChanged();
                            LoadingDialog.showRoundProcessDialog(getActivity());
                            getShop(locationGridBean.data.get(0).tlm_id);
                        } else {
                            Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showToast("请检查您的网络设置");
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
                getTitleClass();
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

    private void addViewByJava(int i) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_location_shop, null);
        RelativeLayout relativeLayout = view.findViewById(R.id.item_location_shop_layout);
        TextView textView = view.findViewById(R.id.item_location_shop_text);
        textView.setText(title.get(i).getName());
        ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();
        params.width = DimenUtils.getScreenWidth() / 6;
        params.height = relativeLayout.getHeight();
        relativeLayout.setLayoutParams(params);
        View tv = view.findViewById(R.id.item_location_shop_view);
        relativeLayout.setId(i);
        relativeLayout.setTag(title.get(i).getName());
        relativeLayout.setOnClickListener(LocationShopFragment.this);
        linearLayout.addView(view);
        checkBoxList.add(tv);
        if (i == 0) {
            relativeLayout.performClick();
        }
    }

    @Override
    public void onClick(View view) {
        View view1 = view.findViewById(R.id.item_location_shop_view);
        int id = view.getId();
        for (int i = 0; i < title.size(); i++) {
            if (i == id) {
                for (int j = 0; j < checkBoxList.size(); j++) {
                    checkBoxList.get(j).setVisibility(View.GONE);
                }
                view1.setVisibility(View.VISIBLE);
                getTwoClass(title.get(i).getId());
            }
        }
    }
}
