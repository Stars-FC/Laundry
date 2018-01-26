package com.wingfac.laundry.ui.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.app.MyApplication;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.weight.LoadingDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/19 0019.
 */

public class CommercialActivityMapActivity extends BaseActivity implements View.OnClickListener, LocationSource, AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.OnMapClickListener {
    public static final String TAG = "CommercialActivityMapFragment";
    @Bind(R.id.head_layout_left)
    RelativeLayout returnLayout;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.map)
    MapView mMapView;
    @Bind(R.id.fragment_navigation_location)
    Button location;
    @Bind(R.id.content_layout)
    RelativeLayout contentLayout;
    AMap aMap;
    PopupWindow mPopupWindow;
    Marker marker;
    private MarkerOptions markerOption;
    private UiSettings mUiSettings;//定义一个UiSettings对象
    private AMapLocation aLocation;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private ProgressDialog progDialog = null;
    private GeocodeSearch geocoderSearch;
    private float touchY;
    private int type = 0;
    private Handler popupHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (aMap == null) {
                        aMap = mMapView.getMap();
                        setUpMap();
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
                    }
                    aMap.setOnMarkerClickListener(CommercialActivityMapActivity.this);
                    //实例化UiSettings类对象
                    mUiSettings = aMap.getUiSettings();
                    mUiSettings.setScaleControlsEnabled(true);//控制比例尺控件是否显示
                    location.setOnClickListener(CommercialActivityMapActivity.this);
                    aMap.setOnInfoWindowClickListener(CommercialActivityMapActivity.this);// 设置点击infoWindow事件监听器
                    aMap.setOnMapClickListener(CommercialActivityMapActivity.this);
                    geocoderSearch = new GeocodeSearch(getActivity());
                    geocoderSearch.setOnGeocodeSearchListener(CommercialActivityMapActivity.this);
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commercial_fragment_map);
        ButterKnife.bind(this);
        mMapView.onCreate(savedInstanceState);
        progDialog = new ProgressDialog(getActivity());
        popupHandler.sendEmptyMessageDelayed(0, 100);
        location.getBackground().setAlpha(200);
        initData();

    }

    void initData() {
        returnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title.setText("店铺定位");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
        deactivate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
//        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            this.aLocation = amapLocation;
            if (UserBean.userStore != null) {
                location.performClick();
                amapLocation.setLongitude(UserBean.userStore.s_longitude);
                amapLocation.setLatitude(UserBean.userStore.s_latitude);
            }
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(LocationSource.OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(MyApplication.getInstance());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            mLocationOption.setOnceLocation(true);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_navigation_location:
                if (UserBean.userStore != null) {
                    aMap.animateCamera(CameraUpdateFactory.changeLatLng(new LatLng(
                            UserBean.userStore.s_latitude, UserBean.userStore.s_longitude)));
                    return;
                }
                aMap.animateCamera(CameraUpdateFactory.changeLatLng(new LatLng(
                        aLocation.getLatitude(), aLocation.getLongitude())));
                break;
        }
    }

    /**
     * marker点击时跳动一下
     */
    public void jumpPoint(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = aMap.getProjection();
        final LatLng markerLatlng = marker.getPosition();
        Point markerPoint = proj.toScreenLocation(markerLatlng);
        markerPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(markerPoint);
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * markerLatlng.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * markerLatlng.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        dismissDialog();
        if (i == AMapException.CODE_AMAP_SUCCESS) {
            if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null
                    && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                if (regeocodeResult.getRegeocodeAddress().getAois().size() > 0) {
                    marker.setTitle(regeocodeResult.getRegeocodeAddress().getAois().get(0).getAoiName());
                } else {
                    marker.setTitle(regeocodeResult.getRegeocodeAddress().getPois().get(0).getTitle());
                }
                marker.setSnippet(regeocodeResult.getRegeocodeAddress().getFormatAddress());
                marker.showInfoWindow();
            } else {
                marker.setTitle("");
                marker.setSnippet("");
                marker.showInfoWindow();
            }
        } else {
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLonPoint latLonPoint = new LatLonPoint(marker.getPosition().latitude, marker.getPosition().longitude);
        if (type == 1) {
            getAddress(latLonPoint);
        } else {
            marker.showInfoWindow();
        }
        if (aMap != null) {
            jumpPoint(marker);
            this.marker = marker;
        }
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (mPopupWindow != null) {
            mPopupWindow = null;
        }
        showPopWindow(contentLayout, R.layout.window_navigation, marker.getTitle());
    }

    private void showPopWindow(View parentView, int convertViewResource, String address) {
        //创建一个popUpWindow
        View popLayout = LayoutInflater.from(getActivity()).inflate(convertViewResource, null);
        LinearLayout rootLayout = (LinearLayout) popLayout.findViewById(R.id.navigation_root_layout);
        final RelativeLayout pLayout = (RelativeLayout) popLayout.findViewById(R.id.navigation_p_layout);
        //给popUpWindow内的空间设置点击事件
        TextView name = (TextView) popLayout.findViewById(R.id.windows_name);
        name.setText("确定定位至" + address + "吗?");
        popLayout.findViewById(R.id.windows_delete_message_cancel).setOnClickListener(v -> {
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
        });
        popLayout.findViewById(R.id.windows_delete_message_confirm).setOnClickListener(view -> {

            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
            LoadingDialog.showRoundProcessDialog(getActivity());
            upDataAddress(address, marker.getPosition().latitude, marker.getPosition().longitude);
        });
        if (mPopupWindow == null) {
            //实例化一个popupWindow
            mPopupWindow =
                    new PopupWindow(popLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //产生背景变暗效果
            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.alpha = 1f;
            rootLayout.setBackgroundResource(R.color.black);
            rootLayout.getBackground().setAlpha(150);
            rootLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touchY = motionEvent.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_UP:
                            if (touchY < pLayout.getTop() || touchY > pLayout.getBottom()) {
                                mPopupWindow.dismiss();
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
            getActivity().getWindow().setAttributes(lp);
            //点击外面popupWindow消失
            mPopupWindow.setOutsideTouchable(true);
            //popupWindow获取焦点
            mPopupWindow.setFocusable(true);
            //popupWindow设置背景图
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            //popupWindow设置开场动画风格
            //popupWindow.setAnimationStyle(R.style.popupWindow_anim);
            //刷新popupWindow
            //popupWindow.update();

            //设置popupWindow消失时的监听
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                //在dismiss中恢复透明度
                public void onDismiss() {
                    WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                    lp.alpha = 1f;
                    getActivity().getWindow().setAttributes(lp);
                }
            });
            mPopupWindow.showAtLocation(parentView, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        } else {
            //如果popupWindow正在显示，接下来隐藏
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            } else {
                //产生背景变暗效果
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
                mPopupWindow.showAtLocation(parentView, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        }
    }

    void upDataAddress(String address, double lat, double lon) {
        APPApi.getInstance().service
                .upDataStoreAddress(String.valueOf(UserBean.userStore.s_id), address, String.valueOf(lon), String.valueOf(lat))
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Base value) {
                        LoadingDialog.mDialog.dismiss();
                        Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                        LoadingDialog.mDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (UserBean.userStore != null) {
            type = 1;
            markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(latLng)
                    .draggable(true);
            aMap.clear();
            aMap.addMarker(markerOption);
        }
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        showDialog();
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }

    /**
     * 显示进度条对话框
     */
    public void showDialog() {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在获取地址");
        progDialog.show();
    }

    /**
     * 隐藏进度条对话框
     */
    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
}
