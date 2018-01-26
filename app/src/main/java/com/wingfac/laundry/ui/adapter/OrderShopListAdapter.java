package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.bean.LocationMod;
import com.wingfac.laundry.bean.ShopOrderBean;
import com.wingfac.laundry.bean.StoreBean;
import com.wingfac.laundry.bean.base.Constant;
import com.wingfac.laundry.ui.activity.ShopHomeActivity;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/5/16 0016.
 */

public class OrderShopListAdapter extends BaseAdapter {
    private Context context;
    private ShopOrderBean list;

    public OrderShopListAdapter(Context context, ShopOrderBean list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.obj1.size();
    }

    @Override
    public Object getItem(int i) {
        return list.obj1.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder viewHolder = null;
        if (view == null) {
            viewHolder = new Holder();
            view = LayoutInflater.from(context).inflate(
                    R.layout.item_activity_order_shop_list, null);
            viewHolder.name = view.findViewById(R.id.item_activity_shop_list_name);
            viewHolder.good = view.findViewById(R.id.item_activity_shop_list_good);
            viewHolder.img = view.findViewById(R.id.item_activity_shop_list_img);
            viewHolder.distance = view.findViewById(R.id.item_activity_shop_list_distance);
            viewHolder.look = view.findViewById(R.id.item_activity_shop_list_look);
            view.setTag(viewHolder);
        } else {
            viewHolder = (Holder) view.getTag();
        }
        viewHolder.name.setText(list.obj1.get(i).c_name);
        viewHolder.good.setText(String.valueOf(list.obj1.get(i).quantity_purchased));
        viewHolder.look.setText("￥"+list.obj1.get(i).unit_price);
        Double num = Double.parseDouble(String.valueOf(list.obj1.get(i).quantity_purchased));
        viewHolder.distance.setText("总价 ￥"+num*list.obj1.get(i).unit_price);
        Glide.with(context)
                .load(Constant.BASE_IMG + list.obj1.get(i).first_picture)
                .dontAnimate()
                .placeholder(R.drawable.erro_store)
                .into(viewHolder.img);
        return view;
    }

    private class Holder {
        public ImageView img;
        public TextView name;
        public TextView good;
        public TextView person;
        public TextView distance;
        public TextView look;
    }
}
