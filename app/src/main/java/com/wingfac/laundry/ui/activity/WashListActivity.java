package com.wingfac.laundry.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.MessageBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.WashMessageBean;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.ui.adapter.MessageAdapter;
import com.wingfac.laundry.ui.fragment.HomeHomeFragment;
import com.wingfac.laundry.utiil.TimeUtils;
import com.yuyh.library.utils.toast.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class WashListActivity extends BaseActivity {
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.head_layout_right)
    Button right;
    @Bind(R.id.fragment_cart_list)
    PullToRefreshListView listView;
    MessageBean list = new MessageBean();
    MessageAdapter adapter;
    int page = 0;
    WashMessageBean washMessageBean = new WashMessageBean();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_cart);
        ButterKnife.bind(this);
        initData();
    }

    void initData() {
        left.setOnClickListener(view -> finish());
        title.setText("取衣列表");
        right.setVisibility(View.GONE);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
        listView.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载中");
        listView.getLoadingLayoutProxy(false, true).setReleaseLabel("加载完毕");
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                page = 0;
                list.obj.clear();
                washMessageBean.data.clear();
                getMessage();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page += 1;
                getMessage();
            }
        });
        adapter = new MessageAdapter(getActivity(), list,"0");
        listView.setAdapter(adapter);
        getMessage();
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getActivity(), DeliveryDetailsActivity.class);
            intent.putExtra("washMessage",washMessageBean.data.get(i-1));
            startActivity(intent);
        });
    }
    void getMessage() {
        APPApi.getInstance().service
                .getWashMessage(String.valueOf(page), "10", UserBean.user.id)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<WashMessageBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(WashMessageBean value) {
                        if (value.responseStatus.equals("0")) {
                            washMessageBean.data.addAll(value.data);
                            for (int i = 0; i < value.data.size(); i++) {
                                MessageBean.Message message = list.new Message();
                                message.content = "您有一份快件";
                                message.time = TimeUtils.getStrTime(value.data.get(i).order_time);
                                list.obj.add(message);
                            }
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
