package com.wingfac.laundry.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.OrderBean;
import com.wingfac.laundry.bean.ShopOrderListBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.ui.activity.ShopOrderActivity;
import com.wingfac.laundry.ui.activity.ShoppingOrderActivity;
import com.wingfac.laundry.ui.adapter.FragmentOrderAdapter;
import com.wingfac.laundry.weight.LoadingDialog;
import com.yuyh.library.utils.toast.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/2 0002.
 */

public class OrderListShopFiveFragment extends Fragment {
    public FragmentOrderAdapter adapter;
    @Bind(R.id.fragment_cart_list)
    PullToRefreshListView listView;
    OrderBean list = new OrderBean();
    int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, null, false);
        ButterKnife.bind(this, view);
        initData();
        list.data.clear();
        getCart();
        return view;
    }

    void getCart() {
        APPApi.getInstance().service
                .getOrderList(UserBean.user.id, "15", String.valueOf(page), "3")
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<ShopOrderListBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ShopOrderListBean value) {
                        if (value.responseStatus.equals("0")) {
                            for (int i = 0; i < value.obj1.size(); i++) {
                                OrderBean.Order order = list.new Order();
                                order.name = value.obj1.get(i).get(0).c_name;
                                order.img = value.obj1.get(i).get(0).first_picture;
                                order.time = value.obj.get(i).order_time;
                                order.num = String.valueOf(value.obj1.get(i).get(0).so_total);
                                order.price = String.valueOf(value.obj1.get(i).get(0).unit_price);
                                order.id = value.obj1.get(i).get(0).so_id;
                                order.type = value.obj.get(i).so_state;
                                switch (value.obj.get(i).so_state) {
                                    case "0":
                                        order.state = "未付款";
                                        break;
                                    case "1":
                                        order.state = "已付款";
                                        break;
                                    case "2":
                                        order.state = "已发货";
                                        break;
                                    case "3":
                                        order.state = "未评价";
                                        break;
                                    case "4":
                                        order.state = "已评价";
                                        break;
                                }
                                list.data.add(order);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void initData() {
        ListView listView1 = listView.getRefreshableView();
        LinearLayout listViewFoot = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.foot_shop_layout, listView1, false);
        listView1.addFooterView(listViewFoot);
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
                list.data.clear();
                getCart();

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page += 1;
                getCart();
            }
        });
        adapter = new FragmentOrderAdapter(getActivity(), list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            if(i!=list.data.size()+1){
                if (list.data.get(i - 1).type.equals("0")) {
                    Intent intent = new Intent(getActivity(), ShopOrderActivity.class);
                    intent.putExtra("so_id", String.valueOf(list.data.get(i - 1).id));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ShoppingOrderActivity.class);
                    intent.putExtra("so_id", String.valueOf(list.data.get(i - 1).id));
                    startActivity(intent);
                }
            }



        });

    }

    public void delete() {
        String soId = "";
        if(adapter.getSelectedList().data.size()==1){
            soId = String.valueOf(adapter.getSelectedList().data.get(0).id);
        }else {
            for (int i = 0; i < adapter.getSelectedList().data.size(); i++) {
                if (i == adapter.getSelectedList().data.size() - 1) {
                    soId = soId + adapter.getSelectedList().data.get(i).id;
                } else {
                    soId = soId + adapter.getSelectedList().data.get(i).id + ",";
                }
            }
        }

        LoadingDialog.showRoundProcessDialog(getActivity());
        remove(soId);
    }

    void remove(String soId) {
        APPApi.getInstance().service
                .removeOrder(soId)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Base value) {
                        LoadingDialog.mDialog.dismiss();
                        ToastUtils.showToast(value.msg);
                        if (value.responseStatus.equals("0")) {
                            list.data.removeAll(adapter.getSelectedList().data);
                            adapter.notifyDataSetChanged();
                            listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialog.mDialog.dismiss();
                        adapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
