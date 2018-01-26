package com.wingfac.laundry.ui.adapter;

import android.content.Context;
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
import com.wingfac.laundry.bean.WashDetailBean;
import com.wingfac.laundry.bean.base.Constant;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/5/16 0016.
 */

public class WashDetailAdapter extends BaseAdapter {
    private Context context;
    private WashDetailBean list;

    public WashDetailAdapter(Context context, WashDetailBean list) {
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
                    R.layout.item_activity_wash_details, null);
            viewHolder.name = view.findViewById(R.id.item_activity_wash_details_name);
            viewHolder.num = view.findViewById(R.id.item_activity_wash_details_num);
            viewHolder.img = view.findViewById(R.id.activity_wash_details_photo);
            viewHolder.price = view.findViewById(R.id.item_activity_wash_details_price);
            viewHolder.all = view.findViewById(R.id.item_activity_wash_details_all);
            view.setTag(viewHolder);
        } else {
            viewHolder = (Holder) view.getTag();
        }
        viewHolder.name.setText(list.obj1.get(i).goods_name);
        viewHolder.num.setText(String.valueOf(list.obj1.get(i).log_num));
        viewHolder.price.setText("￥" + String.valueOf(list.obj1.get(i).price));
        viewHolder.all.setText("￥" + String.valueOf(list.obj1.get(i).price * list.obj1.get(i).log_num));
        Glide.with(context)
                .load(Constant.BASE_IMG + list.obj1.get(i).pic)
                .dontAnimate()
                .placeholder(R.drawable.erro_store)
                .into(viewHolder.img);
        return view;
    }

    private class Holder {
        public ImageView img;
        public TextView name;
        public TextView price;
        public TextView num;
        public TextView all;
    }
}
