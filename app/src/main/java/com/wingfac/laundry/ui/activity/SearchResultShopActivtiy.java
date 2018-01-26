package com.wingfac.laundry.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.CommodityBean;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.ui.adapter.SearchShopAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/17 0017.
 */

public class SearchResultShopActivtiy extends BaseActivity {
    private static final String TAG = "SearchResultShopActivity";
    @Bind(R.id.head_layout_left)
    RelativeLayout returnImg;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.fragment_cart_list)
    PullToRefreshListView listView;
    SearchShopAdapter adapter;
    CommodityBean list = new CommodityBean();
    String word;
    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_shop);
        word = getIntent().getStringExtra("word");
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        returnImg.setOnClickListener(view -> finish());
        title.setText("商品");
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
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
                listView.setMode(PullToRefreshBase.Mode.BOTH);
                page = 0;
                list.obj.clear();
                getCommodity();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page += 1;
                getCommodity();
            }
        });
        adapter = new SearchShopAdapter(getActivity(), list);
        ListView listView1 = listView.getRefreshableView();
        listView1.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), CommodityDetailsActivity.class);
                intent.putExtra("c_id", String.valueOf(list.obj.get(i-1).c_id));
                intent.putExtra("s_id",String.valueOf(list.obj.get(i-1).s_id));
                startActivity(intent);
            }
        });
        getCommodity();
    }

    void getCommodity() {
        APPApi.getInstance().service
                .getCommentForWord(word, String.valueOf(page), "15")
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<CommodityBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CommodityBean value) {
                        if (value.responseStatus.equals("0")) {
                            list.obj.addAll(value.obj);
                        } else {
                            Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
