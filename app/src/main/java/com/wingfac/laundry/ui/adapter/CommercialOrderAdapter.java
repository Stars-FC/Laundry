package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.CommercialOrderBean;
import com.wingfac.laundry.bean.ShoppingLogisticsBean;
import com.wingfac.laundry.weight.ListViewForScrollView;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class CommercialOrderAdapter extends BaseAdapter {
    private static final String TAG = "CommercialOrderAdapter";
    public Context context;
    public CommercialOrderBean list;

    public CommercialOrderAdapter(Context context, CommercialOrderBean list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.obj.size();
    }

    @Override
    public Object getItem(int i) {
        return list.obj.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    ShoppingLogisticsBean shoppingLogisticsBean = new ShoppingLogisticsBean();

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Holder viewHolder = null;
        if (view == null) {
            viewHolder = new Holder();
            view = LayoutInflater.from(context).inflate(
                    R.layout.activity_commercial_order_item, null);
            viewHolder.name = view.findViewById(R.id.item_commercial_fragment_order_name);
            viewHolder.price = view.findViewById(R.id.item_commercial_fragment_order_money);
            viewHolder.orderID = view.findViewById(R.id.item_commercial_fragment_order_id);
            viewHolder.mobile = view.findViewById(R.id.item_commercial_fragment_order_mobile);
            viewHolder.liuyan = view.findViewById(R.id.item_commercial_fragment_order_liuyan);
            viewHolder.wuliu = view.findViewById(R.id.activity_shopping_details_list);
            view.setTag(viewHolder);
        } else {
            viewHolder = (Holder) view.getTag();
        }
        ShoppingLogisticsAdapter shoppingLogisticsAdapter = new ShoppingLogisticsAdapter(context, shoppingLogisticsBean);
        viewHolder.wuliu.setAdapter(shoppingLogisticsAdapter);
        getCommodity(i, shoppingLogisticsAdapter);
        viewHolder.name.setText(list.obj.get(i).s_name);
        viewHolder.mobile.setText(list.obj.get(i).s_mobile);
        viewHolder.orderID.setText(list.obj.get(i).so_number);
        viewHolder.liuyan.setText(list.obj.get(i).guest_book );
        viewHolder.price.setText(list.obj.get(i).so_total +"元");

        return view;
    }

    public void setData(CommercialOrderBean list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public static class Holder {
        public TextView orderID;
        public TextView name;
        public TextView mobile;
        public TextView price;
        public TextView liuyan;
        public ListViewForScrollView wuliu;
    }

    void getCommodity(int i, ShoppingLogisticsAdapter shoppingLogisticsAdapter) {
        APPApi.getInstance().service
                .getLogistic(String.valueOf(list.obj.get(i).so_id))
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<ShoppingLogisticsBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ShoppingLogisticsBean value) {
                        shoppingLogisticsBean.obj.clear();
                        if (value.responseStatus.equals("0")) {
                            shoppingLogisticsBean.obj.addAll(value.obj);
                        }
                        shoppingLogisticsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
