package com.wingfac.laundry.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.ShopOrderBean;
import com.wingfac.laundry.bean.ShoppingLogisticsBean;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.ui.adapter.OrderShopListAdapter;
import com.wingfac.laundry.ui.adapter.ShoppingLogisticsAdapter;
import com.wingfac.laundry.weight.ListViewForScrollView;
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

public class ShoppingOrderActivity extends BaseActivity {
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.head_layout_right)
    Button right;
    @Bind(R.id.activity_shopping_details_list)
    ListView listView;
    @Bind(R.id.activity_shopping_details_scroll)
    ScrollView scrollView;
    @Bind(R.id.activity_shopping_details_goods_list)
    ListViewForScrollView goodsList;
    @Bind(R.id.activity_shopping_details_mobile)
    TextView mobile;
    @Bind(R.id.activity_shopping_details_number)
    TextView number;
    @Bind(R.id.activity_shopping_details_start_time)
    TextView startTime;
    @Bind(R.id.activity_shopping_details_end_time)
    TextView endTime;
    @Bind(R.id.activity_shopping_details_good)
    ImageView good;
    @Bind(R.id.activity_shopping_details_word)
    TextView word;
    ShoppingLogisticsBean list = new ShoppingLogisticsBean();
    ShoppingLogisticsAdapter adapter;
    ShopOrderBean shopOrderBean = new ShopOrderBean();
    OrderShopListAdapter shopListAdapter;
    private String so_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_details);
        ButterKnife.bind(this);
        so_id = getIntent().getStringExtra("so_id");
        initData();
        scrollView.post(() -> scrollView.smoothScrollTo(0, 0));
    }

    void initData() {
        left.setOnClickListener(view -> finish());
        title.setText("订单详情");
        right.setVisibility(View.GONE);
        adapter = new ShoppingLogisticsAdapter(getActivity(), list);
        listView.setAdapter(adapter);
        getCommodity();
        setHeight(listView);
        shopListAdapter = new OrderShopListAdapter(getActivity(), shopOrderBean);
        goodsList.setAdapter(shopListAdapter);
        getDate();
    }

    void addGood() {
        APPApi.getInstance().service
                .addGood(so_id)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Base value) {
                        if (value.responseStatus.equals("0")) {
                            good.setImageResource(R.drawable.goodselext);
                            good.setOnClickListener(view -> ToastUtils.showToast("您已经点过赞了"));
                        }
                        LoadingDialog.mDialog.dismiss();
                        ToastUtils.showToast(value.msg);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialog.mDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void getCommodity() {
        APPApi.getInstance().service
                .getLogistic(so_id)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<ShoppingLogisticsBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ShoppingLogisticsBean value) {
                        list.obj.clear();
                        if (value.responseStatus.equals("0")) {
                            list.obj.addAll(value.obj);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });
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

    void getDate() {
        APPApi.getInstance().service
                .getOrderDetail(so_id)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<ShopOrderBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ShopOrderBean value) {
                        if (value.responseStatus.equals("0")) {
                            if (value.obj.point_like_state.equals("0")) {
                                good.setOnClickListener(view -> {
                                    LoadingDialog.showRoundProcessDialog(getActivity());
                                    addGood();
                                    good.setImageResource(R.drawable.dianzan);
                                });
                            } else {
                                good.setImageResource(R.drawable.goodselext);
                                good.setOnClickListener(view -> ToastUtils.showToast("您已经点过赞了"));
                            }
                            shopOrderBean.obj1.clear();
                            shopOrderBean.obj1.addAll(value.obj2);
                            shopListAdapter.notifyDataSetChanged();
                            mobile.setText(value.obj.s_mobile);
                            number.setText(value.obj.so_number);
                            startTime.setText(value.obj.order_time);
                            endTime.setText(value.obj.payment_time);
                            word.setText(value.obj.guest_book);
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
}
