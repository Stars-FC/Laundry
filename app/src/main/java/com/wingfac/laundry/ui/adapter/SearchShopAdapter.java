package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.CommodityBean;
import com.wingfac.laundry.bean.LocationMod;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.bean.base.Constant;
import com.wingfac.laundry.ui.activity.CommodityDetailsActivity;
import com.wingfac.laundry.weight.LoadingDialog;

import java.math.BigDecimal;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class SearchShopAdapter extends BaseAdapter {
    private static final String TAG = "SearchShopAdapter";
    private Context context;
    private CommodityBean list;

    public SearchShopAdapter(Context context, CommodityBean list) {
        this.context = context;
        if (list == null) {
            this.list = new CommodityBean();
        } else {
            this.list = list;
        }
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

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(
                    R.layout.item_fragment_search_shop, null);
            viewHolder.addCart = view.findViewById(R.id.item_cart_add_cart);
            viewHolder.img = view.findViewById(R.id.item_cart_img);
            viewHolder.name = view.findViewById(R.id.item_cart_name);
            viewHolder.nature = view.findViewById(R.id.item_cart_nature);
            viewHolder.price = view.findViewById(R.id.item_cart_price);
            viewHolder.buy = view.findViewById(R.id.item_cart_buy);
            viewHolder.device = view.findViewById(R.id.item_fragment_search_shop_device);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (list.obj.get(i).s_latitude > 1 && LocationMod.s_longitude != 0) {
            viewHolder.device.setText("\t" + "\t"+"当前距离" + getDistance(i));
        }
        viewHolder.name.setText(list.obj.get(i).c_name);
        viewHolder.nature.setText("描述" + "\t" + "\t" + list.obj.get(i).c_introduce);
        viewHolder.price.setText("价格" + "\t" + "\t" + "¥" + list.obj.get(i).unit_price);
        Glide.with(context)
                .load(Constant.BASE_IMG + list.obj.get(i).first_picture)
                .dontAnimate()
                .placeholder(R.drawable.erro_store)
                .into(viewHolder.img);
        viewHolder.addCart.setOnClickListener(view13 -> {
            LoadingDialog.showRoundProcessDialog(context);
            addCart(String.valueOf(list.obj.get(i).c_id));
        });
        viewHolder.buy.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, CommodityDetailsActivity.class);
            intent.putExtra("c_id", String.valueOf(list.obj.get(i).c_id));
            intent.putExtra("s_id",String.valueOf(list.obj.get(i).s_id));
            context.startActivity(intent);
        });
        return view;
    }

    void addCart(String c_id) {
        APPApi.getInstance().service
                .addCart(UserBean.user.id, c_id)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Base value) {
                        LoadingDialog.mDialog.dismiss();
                        Toast.makeText(context, value.msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialog.mDialog.dismiss();
                        Toast.makeText(context, "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    String getDistance(int i) {
        LatLng latLng1 = new LatLng(list.obj.get(i).s_latitude, list.obj.get(i).s_longitude);
        LatLng latLng2 = new LatLng(LocationMod.s_latitude, LocationMod.s_longitude);
        float distance = AMapUtils.calculateLineDistance(latLng1, latLng2);
        Integer dis = (int) Math.round(distance + 0.5);
        String dist = "";
        if (dis.toString().length() <= 3) {
            dist = dis + "m";
        } else if (dis.toString().length() > 3) {
            int b = 1000;
            double result = dis * 1.0 / b;
            BigDecimal decimal = new BigDecimal(result);
            dist = decimal.setScale(1, BigDecimal.ROUND_HALF_UP) + "km";
        }
        return dist;
    }

    public static class ViewHolder {
        public TextView name;
        public TextView price;
        public TextView nature;
        public ImageView img;
        public TextView addCart;
        public Button buy;
        public TextView device;
    }
}
