package com.wingfac.laundry.ui.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.WashDetailBean;
import com.wingfac.laundry.bean.WashLogisticsBean;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.ui.adapter.WashDetailAdapter;
import com.wingfac.laundry.ui.adapter.WashLogisticsAdapter;
import com.wingfac.laundry.weight.ListViewForScrollView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/3 0003.
 */

public class WashOrderActivity extends BaseActivity {
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.head_layout_right)
    Button right;
    @Bind(R.id.activity_wash_details_list)
    ListView listView;
    @Bind(R.id.activity_wash_details_scroll)
    ScrollView scrollView;
    @Bind(R.id.activity_wash_details_text)
    TextView countdown;
    @Bind(R.id.activity_wash_details_number)
    TextView number;
    @Bind(R.id.activity_wash_details_qr)
    TextView qr;
    @Bind(R.id.activity_wash_details_kouling)
    TextView kouLing;
    @Bind(R.id.activity_wash_details_address)
    TextView address;
    @Bind(R.id.activity_wash_details_liuyan)
    TextView liuYan;
    @Bind(R.id.activity_wash_details_wash_list)
    ListViewForScrollView washListView;
    @Bind(R.id.activity_wash_details_content)
    LinearLayout contentLayout;
    WashLogisticsBean list = new WashLogisticsBean();
    WashLogisticsAdapter adapter;
    WashDetailAdapter washAdapter;
    WashDetailBean washDetailBean = new WashDetailBean();
    Handler handler = new Handler();
    private long time = 400;
    String loId;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time--;
            String formatLongToTimeStr = formatLongToTimeStr(time);
            String[] split = formatLongToTimeStr.split(":");
            countdown.setText(split[0] + ":" + split[1] + ":" + split[2]);
            if (time > 0) {
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wash_details);
        ButterKnife.bind(this);
        loId = getIntent().getStringExtra("loId");
        setTime();
        initData();
        scrollView.post(() -> scrollView.smoothScrollTo(0, 0));
        handler.postDelayed(runnable, 1000);
    }

    void setTime() {
        String times = countdown.getText().toString();
        String[] split = times.split(":");
        String h = split[0];
        String m = split[1];
        String s = split[2];
        int hi = Integer.parseInt(h);
        int mi = Integer.parseInt(m);
        int si = Integer.parseInt(s);
        time = Long.valueOf((hi * 3600 + mi * 60 + si) + "");
    }

    public String formatLongToTimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = 0;
        second = l.intValue();
        if (second > 60) {
            minute = second / 60;         //取整
            second = second % 60;         //取余
        }

        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        String strtime = hour + ":" + minute + ":" + second;
        return strtime;

    }

    void initData() {
        washAdapter = new WashDetailAdapter(getActivity(), washDetailBean);
        washListView.setAdapter(washAdapter);
        left.setOnClickListener(view -> finish());
        title.setText("订单详情");
        right.setVisibility(View.GONE);
        adapter = new WashLogisticsAdapter(getActivity(), list);
        listView.setAdapter(adapter);
        setListData();
        setHeight(listView);
    }

    void setData() {
        for (int i = 0; i < 5; i++) {
            WashLogisticsBean.WashLogistics logistics = list.new WashLogistics();
            logistics.info = "已发货" + i;
            logistics.time = "2017-05-21 12:25:0" + i;
            list.data.add(logistics);
        }
        adapter.notifyDataSetChanged();
    }

    void setListData() {
        APPApi.getInstance().service
                .getWashOrderDetail(loId)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<WashDetailBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(WashDetailBean value) {
                        if (value.responseStatus.equals("0")) {
                            washDetailBean.obj1.addAll(value.obj1);
                            number.setText(value.obj.lo_number);
                            qr.setOnClickListener(view -> showOrderPopWindow(contentLayout, R.layout.window_qr, value.obj.pickup_code));
                            kouLing.setText(value.obj.pickup_password);
                            address.setText(value.obj.address);
                            liuYan.setText(value.obj.guest_book);
                        } else {
                            Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    PopupWindow orderWindow;

    private void showOrderPopWindow(View parentView, int convertViewResource, String pic) {
        //创建一个popUpWindow
        View popLayout = LayoutInflater.from(getActivity()).inflate(convertViewResource, null);

        ImageView qr = (ImageView) popLayout.findViewById(R.id.windows_qr);
        Glide.with(getActivity()).load(pic).into(qr);
        if (orderWindow == null) {
            //实例化一个popupWindow
            orderWindow =
                    new PopupWindow(popLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //产生背景变暗效果
            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.alpha = 0.4f;
            getActivity().getWindow().setAttributes(lp);
            //点击外面popupWindow消失
            orderWindow.setOutsideTouchable(true);
            //popupWindow获取焦点
            orderWindow.setFocusable(true);
            //popupWindow设置背景图
            orderWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            //popupWindow设置开场动画风格
            //popupWindow.setAnimationStyle(R.style.popupWindow_anim);
            //刷新popupWindow
            //popupWindow.update();

            //设置popupWindow消失时的监听
            orderWindow.setOnDismissListener(() -> {
                WindowManager.LayoutParams lp1 = getActivity().getWindow().getAttributes();
                lp1.alpha = 1f;
                getActivity().getWindow().setAttributes(lp1);
            });
            orderWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        } else {
            //如果popupWindow正在显示，接下来隐藏
            if (orderWindow.isShowing()) {
                orderWindow.dismiss();
            } else {
                //产生背景变暗效果
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 0.4f;
                getActivity().getWindow().setAttributes(lp);
                orderWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
            }
        }
    }

    public void setHeight(ListView listview) {
        int height = 0;
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View temp = adapter.getView(i, null, listview);
            temp.measure(0, 0);
            height += temp.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listview.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.FILL_PARENT;
        params.height = height;
        listview.setLayoutParams(params);
    }
}
