package com.wingfac.laundry.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.CommodityBean;
import com.wingfac.laundry.bean.StoreClassBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.support.SupportRecyclerView;
import com.wingfac.laundry.ui.activity.CommercialAddShopActivity;
import com.wingfac.laundry.ui.adapter.CommercialFragmentShopAdapter;
import com.wingfac.laundry.ui.adapter.YingkeAdapter;
import com.wingfac.laundry.weight.ALoadingDialog;
import com.wingfac.laundry.weight.LoadingDialog;
import com.yuyh.library.utils.toast.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/19 0019.
 */

public class CommercialActivityShopFragment extends Fragment {
    public static final String TAG = "CommercialActivityShopFragment";
    @Bind(R.id.head_layout_right)
    Button add;
    @Bind(R.id.activity_home_enclosed_rock_deformation_spinner1)
    Spinner spinner;
        @Bind(R.id.commercial_fragment_shop_grid)
        PullToRefreshGridView gridView;
//    @Bind(R.id.refresh)
//    MaterialRefreshLayout materialRefreshLayout;
//    @Bind(R.id.recyclerview)
//    SupportRecyclerView recyclerView;
//    @Bind(R.id.emptyView)
//    View emptyView;
    @Bind(R.id.commercial_fragment_shop_tx)
    TextView tx;
        CommodityBean list = new CommodityBean();
    CommercialFragmentShopAdapter adapter;
//    YingkeAdapter adapter;
//    private List<CommodityBean.Commodity> list = new ArrayList<>();
    int page = 0;
    @Bind(R.id.fragment_navigation_content)
    LinearLayout contentLayout;
    List<String> list1 = new ArrayList<>();
    int select = -1;
    StoreClassBean storeClassBean = new StoreClassBean();
    public ALoadingDialog mLoadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.commercial_fragment_shop, null, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }


    ArrayAdapter<String> SpAdapter;
    boolean isSpinnerFirst = true;

    void initData() {
        SpAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_spinner_defalut, R.id.text2, list1);
        SpAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner.setAdapter(SpAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (!isSpinnerFirst) {
                    tx.setVisibility(View.GONE);
                    for (int i = 0; i < storeClassBean.obj1.size(); i++) {
                        if (storeClassBean.obj1.get(i).cc_name.equals(list1.get(arg2))) {
                            select = storeClassBean.obj1.get(i).cc_id;
                            list.obj.clear();
                            gridView.setRefreshing();
//                            materialRefreshLayout.autoRefresh();
                        }
                    }
                }
                isSpinnerFirst = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                if (!isSpinnerFirst) {
                    tx.setVisibility(View.GONE);
                }
            }
        });
        add.setOnClickListener(view -> {
            if (select == -1) {
                ToastUtils.showToast("请选择分类");
                return;
            }
            Intent intent = new Intent(getActivity(), CommercialAddShopActivity.class);
            intent.putExtra("ccId", String.valueOf(select));
            startActivity(intent);
        });
//        adapter = new YingkeAdapter(list, getActivity());
//        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 2));
//        recyclerView.setAdapter(adapter);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        materialRefreshLayout.setMaterialRefreshListener(new RefreshListener());
        gridView.setMode(PullToRefreshBase.Mode.BOTH);
        gridView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
        gridView.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载中");
        gridView.getLoadingLayoutProxy(false, true).setReleaseLabel("加载完毕");
        gridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                page = 0;
                list.obj.clear();
                if (select == -1) {
                    LoadingDialog.showRoundProcessDialog(getActivity());
                    getGoods();
                } else {
                    LoadingDialog.showRoundProcessDialog(getActivity());
                    getGoodsForClass();
                }

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                page += 1;
                if (select == -1) {
                    LoadingDialog.showRoundProcessDialog(getActivity());
                    getGoods();
                } else {
                    LoadingDialog.showRoundProcessDialog(getActivity());
                    getGoodsForClass();
                }
            }
        });
        adapter = new CommercialFragmentShopAdapter(getActivity(),list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (UserBean.userStore == null) {
                Toast.makeText(getActivity(), "请先创建店铺", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getActivity(), CommercialAddShopActivity.class);
            intent.putExtra("Commodity", list.obj.get(i));
            intent.putExtra("ccId", String.valueOf(select));
            startActivity(intent);

        });
        getHomeCommodity();
        LoadingDialog.showRoundProcessDialog(getActivity());
        list.obj.clear();
        getGoods();
//        adapter.setOnItemClickListener((view, position, data) -> {
//            if (UserBean.userStore == null) {
//                Toast.makeText(getActivity(), "请先创建店铺", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Intent intent = new Intent(getActivity(), CommercialAddShopActivity.class);
//            intent.putExtra("Commodity", data);
//            intent.putExtra("ccId", String.valueOf(select));
//            startActivity(intent);
//        });

    }

//    private void complete() {
//        recyclerView.setEmptyView(emptyView);
//        adapter.notifyDataSetChanged();
//        materialRefreshLayout.finishRefresh();
//        materialRefreshLayout.finishRefreshLoadMore();
//        new Handler().postDelayed(() -> hideLoadingDialog(), 1000);
//    }

//    /**
//     * 隐藏刷新Loadding
//     */
//    public void hideLoadingDialog() {
//        try {
//            if (mLoadingDialog != null) {
//                if (mLoadingDialog.animation != null) {
//                    mLoadingDialog.animation.reset();
//                }
//                mLoadingDialog.dismiss();
//                mLoadingDialog = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private class RefreshListener extends MaterialRefreshListener {
//        @Override
//        public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
//            page = 0;
//            list.clear();
//            if (select == -1) {
//                LoadingDialog.showRoundProcessDialog(getActivity());
//                getGoods();
//            } else {
//                LoadingDialog.showRoundProcessDialog(getActivity());
//                getGoodsForClass();
//            }
//        }
//
//        @Override
//        public void onRefreshLoadMore(final MaterialRefreshLayout materialRefreshLayout) {
//            page += 1;
//            if (select == -1) {
//                LoadingDialog.showRoundProcessDialog(getActivity());
//                getGoods();
//            } else {
//                LoadingDialog.showRoundProcessDialog(getActivity());
//                getGoodsForClass();
//            }
//
//        }
//    }

    void getHomeCommodity() {
        APPApi.getInstance().service
                .getStoreClass(UserBean.user.id)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<StoreClassBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(StoreClassBean value) {
                        storeClassBean.obj1.clear();
                        storeClassBean.obj1.addAll(value.obj1);
                        for (int i = 0; i < storeClassBean.obj1.size(); i++) {
                            if (storeClassBean.obj1.get(i).cc_picture.equals(" ")) {
                                storeClassBean.obj1.get(i).cc_picture = "";
                            }
                            if (storeClassBean.obj1.get(i).cc_name.equals(" ")) {
                                storeClassBean.obj1.get(i).cc_name = "";
                            }
                        }
                        for (int i = 0; i < value.obj1.size(); i++) {
                            if (!value.obj1.get(i).cc_name.equals("")) {
                                list1.add(value.obj1.get(i).cc_name);
                            }
                        }
                        SpAdapter.notifyDataSetChanged();
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

    void getGoods() {
        APPApi.getInstance().service
                .getAllGoods(UserBean.user.id, "15", String.valueOf(page))
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<CommodityBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CommodityBean value) {
                        LoadingDialog.mDialog.dismiss();
//                        list.addAll(value.obj);
//                        complete();
                        list.obj.addAll(value.obj);
                        adapter.notifyDataSetChanged();
                        gridView.postDelayed(() -> gridView.onRefreshComplete(), 1000);
                    }

                    @Override
                    public void onError(Throwable e) {
//                        complete();
                        adapter.notifyDataSetChanged();
                        gridView.postDelayed(() -> gridView.onRefreshComplete(), 1000);
                        LoadingDialog.mDialog.dismiss();
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void getGoodsForClass() {
        APPApi.getInstance().service
                .getCommdityList(String.valueOf(UserBean.userStore.s_id), String.valueOf(select))
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<CommodityBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CommodityBean value) {
                        LoadingDialog.mDialog.dismiss();
                        list.obj.clear();
                        list.obj.addAll(value.obj);
                        adapter.notifyDataSetChanged();
                        gridView.postDelayed(() -> gridView.onRefreshComplete(), 1000);
//                        list.addAll(value.obj);
//                        complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.notifyDataSetChanged();
                        gridView.postDelayed(() -> gridView.onRefreshComplete(), 1000);
//                        complete();
                        LoadingDialog.mDialog.dismiss();
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
