package com.wingfac.laundry.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.CommercialMoneyBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.ui.adapter.CommercialMoneyAdapter;
import com.wingfac.laundry.utiil.TimeUtils;
import com.wingfac.laundry.weight.CustomDatePickerNo;
import com.wingfac.laundry.weight.LoadingDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/19 0019.
 */

public class CommercialActivityMoneyFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "CommercialActivityMoneyFragment";
    @Bind(R.id.head_layout_left)
    RelativeLayout returnLayout;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.commercial_fragment_order_list)
    PullToRefreshListView listView;
    @Bind(R.id.commercial_fragment_order_start)
    TextView start;
    @Bind(R.id.commercial_fragment_order_end)
    TextView end;
    @Bind(R.id.fragment_navigation_content)
    RelativeLayout contentLayout;
    CommercialMoneyAdapter adapter;
    CommercialMoneyBean list = new CommercialMoneyBean();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    int page = 0;
    private CustomDatePickerNo customDatePicker, customDatePicker1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.commercial_fragment_money, null, false);
        ButterKnife.bind(this, view);
        initData();
        initDatePicker();
        initDatePicker1();
        return view;
    }

    void initData() {
        returnLayout.setVisibility(View.GONE);
        title.setText("财务管理");
        start.setOnClickListener(this);
        end.setOnClickListener(this);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
        listView.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载中");
        listView.getLoadingLayoutProxy(false, true).setReleaseLabel("加载完毕");
//        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
//                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
//
//                // Update the LastUpdatedLabel
//                start.setText("");
//                end.setText("");
//                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
//                page = 0;
//                list.data.clear();
////                LoadingDialog.showRoundProcessDialog(getActivity());
//                getCommodity();
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                start.setText("");
//                end.setText("");
//                page += 1;
////                LoadingDialog.showRoundProcessDialog(getActivity());
//                getCommodity();
//            }
//        });
        adapter = new CommercialMoneyAdapter(getActivity(), list,contentLayout);
        ListView listView1 = listView.getRefreshableView();
        listView1.setAdapter(adapter);
        if (UserBean.userStore == null) {
            Toast.makeText(getActivity(), "请先创建店铺", Toast.LENGTH_SHORT).show();
        } else {
            LoadingDialog.showRoundProcessDialog(getActivity());
            getCommodity();
        }
    }

    void getCommodity() {
        APPApi.getInstance().service
                .getMoney(UserBean.user.id, null, null)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<CommercialMoneyBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CommercialMoneyBean value) {
                        LoadingDialog.mDialog.dismiss();
                        if (value.responseStatus.equals("0")) {
                            list.data.addAll(value.data);
                        } else {
                            Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                        }
                        adapter.setData(list);
                        listView.postDelayed(() -> listView.onRefreshComplete(), 100);
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.setData(list);
                        listView.postDelayed(() -> listView.onRefreshComplete(), 100);
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                        LoadingDialog.mDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void getDate(String firstTime, String lastTime) {
        APPApi.getInstance().service
                .getMoney(String.valueOf(UserBean.user.id), firstTime, lastTime)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<CommercialMoneyBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CommercialMoneyBean value) {
                        LoadingDialog.mDialog.dismiss();
                        list.data.clear();
                        if (value.responseStatus.equals("0")) {
                            list.data.addAll(value.data);
                        } else {
                            Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                        }
                        adapter.setData(list);
                        listView.postDelayed(() -> listView.onRefreshComplete(), 100);
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.setData(list);
                        listView.postDelayed(() -> listView.onRefreshComplete(), 100);
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                        LoadingDialog.mDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commercial_fragment_order_start:
                customDatePicker.show(sdf.format(new Date()));
                break;
            case R.id.commercial_fragment_order_end:
                customDatePicker1.show(sdf.format(new Date()));
                break;
        }
    }

    private void initDatePicker() {
        customDatePicker = new CustomDatePickerNo(getActivity(), time -> { // 回调接口，获得选中的时间
            if (!end.getText().toString().equals("")) {
                if (Long.parseLong(TimeUtils.getTime(end.getText().toString().trim())) < Long.parseLong(TimeUtils.getTime(time))) {
                    start.setText(end.getText().toString());
                    end.setText(time);
                } else {
                    start.setText(time);
                }
                if (UserBean.userStore == null) {
                    Toast.makeText(getActivity(), "请先创建店铺", Toast.LENGTH_SHORT).show();
                } else {
                    LoadingDialog.showRoundProcessDialog(getActivity());
                    getDate(start.getText().toString(), end.getText().toString());
                }
            } else {
                start.setText(time);
            }
        }, "2010-01-01", "2500-01-01"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker.setIsLoop(true); // 允许循环滚动
    }

    private void initDatePicker1() {
        customDatePicker1 = new CustomDatePickerNo(getActivity(), time -> { // 回调接口，获得选中的时间
            if (!start.getText().toString().equals("")) {
                if (Long.parseLong(TimeUtils.getTime(start.getText().toString())) > Long.parseLong(TimeUtils.getTime(time))) {
                    end.setText(start.getText().toString());
                    start.setText(time);
                } else {
                    end.setText(time);
                }
                if (UserBean.userStore == null) {
                    Toast.makeText(getActivity(), "请先创建店铺", Toast.LENGTH_SHORT).show();
                } else {
                    LoadingDialog.showRoundProcessDialog(getActivity());
                    getDate(start.getText().toString(), end.getText().toString());
                }
            } else {
                end.setText(time);
            }
        }, "2010-01-01", "2500-01-01"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.setIsLoop(true); // 允许循环滚动
    }
}
