package com.wingfac.laundry.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.app.MyApplication;
import com.wingfac.laundry.bean.SearchHistoryBean;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.ui.adapter.SearchHistoryAdapter;
import com.wingfac.laundry.weight.MyGridView;
import com.wingfac.laundry.weight.SpinerPopWindow;

import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/5/17 0017.
 */

public class SearchActivity extends BaseActivity {
    @Bind(R.id.pull_layout)
    RelativeLayout pullLayout;
    @Bind(R.id.pull_text)
    TextView pullText;
    @Bind(R.id.activity_search_grid)
    MyGridView gridView;
    @Bind(R.id.history_layout)
    RelativeLayout historyLayout;
    @Bind(R.id.no_history_layout)
    RelativeLayout noHistoryLayout;
    @Bind(R.id.activity_search_et)
    EditText et;
    @Bind(R.id.head_activity_search_left)
    RelativeLayout returnLayout;
    @Bind(R.id.head_activity_search_right)
    RelativeLayout search;
    SearchHistoryBean list = new SearchHistoryBean();
    SearchHistoryAdapter adapter;
    private SpinerPopWindow mSpinerPopWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pullText.getText().toString().trim().equals("商品")) {
            if (MyApplication.getAcache().getAsObject("historyShop") != null) {
                SearchHistoryBean searchHistoryBean = (SearchHistoryBean) MyApplication.getAcache().getAsObject("historyShop");
                list.data.clear();
                list.data.addAll(searchHistoryBean.data);
                Collections.reverse(list.data);
                adapter.notifyDataSetChanged();
                if (list.data.size() > 0) {
                    historyLayout.setVisibility(View.VISIBLE);
                    noHistoryLayout.setVisibility(View.GONE);
                    gridView.setVisibility(View.VISIBLE);
                } else {
                    historyLayout.setVisibility(View.GONE);
                    noHistoryLayout.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.GONE);
                }
            } else {
                historyLayout.setVisibility(View.GONE);
                noHistoryLayout.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.GONE);
            }
        } else {
            if (MyApplication.getAcache().getAsObject("historyStore") != null) {
                SearchHistoryBean searchHistoryBean = (SearchHistoryBean) MyApplication.getAcache().getAsObject("historyStore");
                list.data.clear();
                list.data.addAll(searchHistoryBean.data);
                Collections.reverse(list.data);
                adapter.notifyDataSetChanged();
                if (list.data.size() > 0) {
                    historyLayout.setVisibility(View.VISIBLE);
                    noHistoryLayout.setVisibility(View.GONE);
                    gridView.setVisibility(View.VISIBLE);
                } else {
                    historyLayout.setVisibility(View.GONE);
                    noHistoryLayout.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.GONE);
                }
            } else {
                historyLayout.setVisibility(View.GONE);
                noHistoryLayout.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.GONE);
            }
        }

    }

    private void initData() {
        returnLayout.setOnClickListener(view -> finish());
        adapter = new SearchHistoryAdapter(getActivity(), list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (pullText.getText().toString().trim().equals("店铺")) {
                Intent intent = new Intent(getActivity(), ShopListActivity.class);
                intent.putExtra("word", list.data.get(i));
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), SearchResultShopActivtiy.class);
                intent.putExtra("word", list.data.get(i));
                startActivity(intent);
            }
        });
        search.setOnClickListener(view -> {
            if (et.getText().toString().trim().equals("") | et == null) {
                toastS("请输入搜索内容");
                return;
            } else {
                if (pullText.getText().equals("商品")) {
                    Boolean state = false;
                    for (int i = 0; i < list.data.size(); i++) {
                        if (list.data.get(i).equals(et.getText().toString().trim())) {
                            state = true;
                        }
                    }
                    if (!state) {
                        Collections.reverse(list.data);
                        if (list.data.size() == 10) {
                            list.data.remove(0);
                        }
                        list.data.add(et.getText().toString().trim());
                        MyApplication.getAcache().put("historyShop", list);
                        Collections.reverse(list.data);
                    }
                    Intent intent = new Intent(getActivity(), SearchResultShopActivtiy.class);
                    intent.putExtra("word", et.getText().toString().trim());
                    startActivity(intent);
                } else {
                    Boolean state = false;
                    for (int i = 0; i < list.data.size(); i++) {
                        if (list.data.get(i).equals(et.getText().toString().trim())) {
                            state = true;
                        }
                    }
                    if (!state) {
                        Collections.reverse(list.data);
                        if (list.data.size() == 10) {
                            list.data.remove(0);
                        }
                        list.data.add(et.getText().toString().trim());
                        MyApplication.getAcache().put("historyStore", list);
                        Collections.reverse(list.data);
                    }
                    Intent intent = new Intent(getActivity(), ShopListActivity.class);
                    intent.putExtra("word", et.getText().toString().trim());
                    startActivity(intent);
                }
            }
        });
        View.OnClickListener itemsOnClick = v -> {
            switch (v.getId()) {
                case R.id.windows_search_one:
                    if (mSpinerPopWindow.isShowing()) {
                        mSpinerPopWindow.dismiss();
                    }
                    if (MyApplication.getAcache().getAsObject("historyShop") != null) {
                        SearchHistoryBean searchHistoryBean = (SearchHistoryBean) MyApplication.getAcache().getAsObject("historyShop");
                        list.data.clear();
                        list.data.addAll(searchHistoryBean.data);
                        Collections.reverse(list.data);
                        adapter.notifyDataSetChanged();
                        if (list.data.size() > 0) {
                            historyLayout.setVisibility(View.VISIBLE);
                            noHistoryLayout.setVisibility(View.GONE);
                            gridView.setVisibility(View.VISIBLE);
                        } else {
                            historyLayout.setVisibility(View.GONE);
                            noHistoryLayout.setVisibility(View.VISIBLE);
                            gridView.setVisibility(View.GONE);
                        }
                    } else {
                        list = new SearchHistoryBean();
                        historyLayout.setVisibility(View.GONE);
                        noHistoryLayout.setVisibility(View.VISIBLE);
                        gridView.setVisibility(View.GONE);
                    }
                    pullText.setText("商品");
                    break;
                case R.id.windows_search_two:
                    if (mSpinerPopWindow.isShowing()) {
                        mSpinerPopWindow.dismiss();
                    }
                    if (MyApplication.getAcache().getAsObject("historyStore") != null) {
                        SearchHistoryBean searchHistoryBean = (SearchHistoryBean) MyApplication.getAcache().getAsObject("historyStore");
                        list.data.clear();
                        list.data.addAll(searchHistoryBean.data);
                        Collections.reverse(list.data);
                        adapter.notifyDataSetChanged();
                        if (list.data.size() > 0) {
                            historyLayout.setVisibility(View.VISIBLE);
                            noHistoryLayout.setVisibility(View.GONE);
                            gridView.setVisibility(View.VISIBLE);
                        } else {
                            historyLayout.setVisibility(View.GONE);
                            noHistoryLayout.setVisibility(View.VISIBLE);
                            gridView.setVisibility(View.GONE);
                        }
                    } else {
                        list = new SearchHistoryBean();
                        historyLayout.setVisibility(View.GONE);
                        noHistoryLayout.setVisibility(View.VISIBLE);
                        gridView.setVisibility(View.GONE);
                    }
                    pullText.setText("店铺");
                    break;
                default:
                    break;
            }

        };
        mSpinerPopWindow = new SpinerPopWindow(getActivity(), itemsOnClick);
        pullLayout.setOnClickListener(view -> showSpinWindow());
    }

    private void showSpinWindow() {
        mSpinerPopWindow.setWidth(pullLayout.getWidth());
        mSpinerPopWindow.showAsDropDown(pullLayout);
    }
}
