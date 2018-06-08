package com.wingfac.laundry.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.CanCloseBean;
import com.wingfac.laundry.bean.HeadImgBean;
import com.wingfac.laundry.bean.InfoBean;
import com.wingfac.laundry.bean.LocationMod;
import com.wingfac.laundry.bean.MessageBean;
import com.wingfac.laundry.bean.TitleClassBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.WashMessageBean;
import com.wingfac.laundry.bean.base.Constant;
import com.wingfac.laundry.ui.activity.AppointmentWashActivity;
import com.wingfac.laundry.ui.activity.HomeLocationActivity;
import com.wingfac.laundry.ui.activity.HomeMessageActivity;
import com.wingfac.laundry.ui.activity.MineComplaintSuggestionActivity;
import com.wingfac.laundry.ui.activity.MineRechargeNextActivity;
import com.wingfac.laundry.ui.activity.ShopNextActivity;
import com.wingfac.laundry.ui.activity.WashListActivity;
import com.wingfac.laundry.ui.adapter.ViewPager_GV_ItemAdapter;
import com.wingfac.laundry.ui.adapter.ViewPager_GridView_Adapter;
import com.yuyh.library.utils.toast.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/7/26 0026.
 */

public class HomeHomeFragment extends Fragment implements AMapLocationListener {
    @Bind(R.id.vPager)
    ViewPager viewPager;
    @Bind(R.id.ll_dots)
    LinearLayout ll_dots;
    @Bind(R.id.head_layout_right)
    RelativeLayout right;
    @Bind(R.id.title_img)
    ImageView left;
    @Bind(R.id.fragment_home_scroll_view)
    ScrollView scrollView;
    @Bind(R.id.fragment_home_wash)
    RelativeLayout wash;
    @Bind(R.id.fragment_home_delivery)
    RelativeLayout delivery;
    @Bind(R.id.fragment_home_wash_bottom)
    RelativeLayout washBottom;
    @Bind(R.id.fragment_home_message_text)
    TextView messageText;
    @Bind(R.id.fragment_home_complaint)
    RelativeLayout complaint;
    @Bind(R.id.fragment_home_head)
    ImageView headImg;
    @Bind(R.id.fragment_home_vip)
    RelativeLayout vip;
    @Bind(R.id.fragment_home_num)
    TextView Messagenum;
    @Bind(R.id.fragment_home_num_layout)
    RelativeLayout numLayout;
    //准备数据
    ArrayList<InfoBean> list = new ArrayList<>();
    InfoBean info = null;
    //gridView 页面item的数量
    int pageItemCount;
    //页面的宽高
    int width;
    int height;
    //保存每个GridView的视图
    ArrayList<View> viewlist = null;
    @Bind(R.id.head_layout_title)
    TextView headLayoutTitle;
    private ImageView[] dots;
    /**
     * ViewPager页数
     */
    private int viewPager_size;
    //当前页
    private int currentIndex;
    public AMapLocationClientOption mLocationOption = null;
    public AMapLocationClient mlocationClient;
    private Boolean flag = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null, false);
        ButterKnife.bind(this, view);
        WindowManager wm = (WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        //初始化视图
        initView();
        initData();
        return view;
    }

    private void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
        scrollView.post(() -> scrollView.smoothScrollTo(0, 0));
        i = 0;
    }

    public static int i = 0;

    private void initView() {
        right.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), HomeMessageActivity.class);
            startActivity(intent);
        });
        left.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), HomeLocationActivity.class);
            startActivity(intent);
        });

        wash.setOnClickListener(view -> {
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
        delivery.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), WashListActivity.class);
            startActivity(intent);
        });
        complaint.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), MineComplaintSuggestionActivity.class);
            intent.putExtra("state", "1");
            startActivity(intent);
        });
        vip.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), MineRechargeNextActivity.class);
            startActivity(intent);
        });
        getHeadImg();
        getTitleClass();
        getMessage();
    }

    void getMessage() {
        APPApi.getInstance().service
                .getWashMessage("0", "10", UserBean.user.id)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<WashMessageBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(WashMessageBean value) {
                        if (value.responseStatus.equals("0")) {
                            if (value.data.size() > 0) {
                                washBottom.setVisibility(View.VISIBLE);
                                washBottom.setOnClickListener(view -> {
                                    Intent intent = new Intent(getActivity(), WashListActivity.class);
                                    startActivity(intent);
                                });
                                messageText.setText("您有一个快递包裹至" + value.data.get(0).address + "，取件码【" + value.data.get(0).pickup_password + "】,请您尽快取件");
                            } else {
                                washBottom.setVisibility(View.GONE);
                            }
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

    void getData() {
        APPApi.getInstance().service
                .getMessage(UserBean.user.id, "1", "1", "1")
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<MessageBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(MessageBean value) {
                        if (value.responseStatus.equals("0")) {
                            if (value.obj1 != null && value.obj1 != 0) {
                                Messagenum.setText(String.valueOf(value.obj1));
                                numLayout.setVisibility(View.VISIBLE);
                            } else {
                                numLayout.setVisibility(View.VISIBLE);
                            }
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

    void getHeadImg() {
        APPApi.getInstance().service
                .getHomeImg()
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<HeadImgBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(HeadImgBean value) {
                        if (value.responseStatus.equals("0")) {
                            Glide.with(getActivity())
                                    .load(Constant.BASE_IMG + value.obj.indexAdver)
                                    .dontAnimate()
                                    .placeholder(R.drawable.erro_store)
                                    .into(headImg);
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
                                list.add(infoBean);
                            }
                            initDots();
                            setAdapter();
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

    private void setAdapter() {
        viewlist = new ArrayList<View>();
        for (int i = 0; i < viewPager_size; i++) {
            viewlist.add(getGridViewItem(i));    //36
        }
        viewPager.setAdapter(new ViewPager_GridView_Adapter(viewlist));
    }

    //每个GridView页面中的布局
    private View getGridViewItem(int index) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.channel_viewpage_gridview, null);
        GridView gridView = layout.findViewById(R.id.vp_gv);

        gridView.setNumColumns(3);
        //每个页面的adapter
        ViewPager_GV_ItemAdapter adapter = new ViewPager_GV_ItemAdapter(getActivity(), list, index, pageItemCount);
        gridView.setAdapter(adapter);
        //设置gridView中点击Item事件
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getActivity(), ShopNextActivity.class);
            intent.putExtra("lm_id", list.get(position).getId());
            startActivity(intent);
            if (null != list.get(position + currentIndex * pageItemCount).getOnClickListener()) {
                list.get(position + currentIndex * pageItemCount).getOnClickListener().ongvItemClickListener(view);

            }
        });
        return gridView;
    }

    private void initDots() {
        int col = (width / 160) > 2 ? (width / 160) : 3;   //3    4
        int row = (height / 400) > 4 ? (height / 400) : 2;   //2     2
        pageItemCount = 6;  //每一页可装item   //6
        //gridView 的页数
        if (list.size() <= 6) {
            viewPager_size = 1;
        } else if (list.size() > 6 && list.size() <= 12) {
            viewPager_size = 2;
        } else {
            viewPager_size = 3;
        }

        if (0 < viewPager_size) {
            ll_dots.removeAllViews();
            if (1 == viewPager_size) {
                ll_dots.setVisibility(View.GONE);
            } else if (1 < viewPager_size) {
                ll_dots.setVisibility(View.VISIBLE);
                for (int j = 0; j < viewPager_size; j++) {
                    ImageView image = new ImageView(getActivity());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);  //dot的宽高
                    params.setMargins(3, 0, 3, 0);
                    image.setBackgroundResource(R.drawable.home_dot);
                    ll_dots.addView(image, params);
                }
            }
        }
        if (viewPager_size != 1) {
            dots = new ImageView[viewPager_size];
            for (int i = 0; i < viewPager_size; i++) {
                //从布局中填充dots数组
                dots[i] = (ImageView) ll_dots.getChildAt(i);
            }
            currentIndex = 0;  //当前页
            dots[currentIndex].setBackgroundResource(R.drawable.home_dot);
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int arg0) {
                    setCurDot(arg0);
                }


                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {

                }
            });
        }

    }

    private void setCurDot(int position) {
        if (position < 0 || position > viewPager_size - 1 || currentIndex == position) {
            return;
        }
        for (int i = 0; i < dots.length; i++) {
            dots[i].setBackgroundResource(R.drawable.home_dot);
        }
        //dots[positon].setEnabled(false);
        // dots[currentIndex].setEnabled(true);
        dots[position].setBackgroundResource(R.drawable.home_dot);
        currentIndex = position;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
