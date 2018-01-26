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
import com.wingfac.laundry.bean.StoreBean;
import com.wingfac.laundry.bean.base.Constant;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class LocationShopListAdapter extends BaseAdapter {
    private Context context;
    private StoreBean list;

    public LocationShopListAdapter(Context context, StoreBean list) {
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder viewHolder = null;
        if (view == null) {
            viewHolder = new Holder();
            view = LayoutInflater.from(context).inflate(
                    R.layout.item_location_shop_list, null);
            viewHolder.name = view.findViewById(R.id.item_fragment_home_name);
            viewHolder.good = view.findViewById(R.id.item_fragment_home_good_reputation);
            viewHolder.img = view.findViewById(R.id.item_fragment_home_img);
            viewHolder.person = view.findViewById(R.id.item_fragment_home_popularity);
            viewHolder.distance = view.findViewById(R.id.item_location_shop_list_distance);
            viewHolder.kuai = view.findViewById(R.id.item_fragment_home_distribution);
            view.setTag(viewHolder);
        } else {
            viewHolder = (Holder) view.getTag();
        }
        viewHolder.name.setText(list.obj.get(i).s_name);
        viewHolder.good.setText(String.valueOf(list.obj.get(i).s_praise));
        viewHolder.person.setText(String.valueOf(list.obj.get(i).s_popularity));
        viewHolder.kuai.setText(list.obj.get(i).s_distribution);
        if (list.obj.get(i).s_latitude > 1 && LocationMod.s_longitude != 0) {
            viewHolder.distance.setText("当前距离" + getDistance(i));
        }
        Glide.with(context)
                .load(Constant.BASE_IMG + list.obj.get(i).s_logo)
                .dontAnimate()
                .placeholder(R.drawable.erro_store)
                .into(viewHolder.img);
        return view;
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

    private class Holder {
        public ImageView img;
        public TextView name;
        public TextView good;
        public TextView person;
        public TextView distance;
        public TextView kuai;
    }
}
