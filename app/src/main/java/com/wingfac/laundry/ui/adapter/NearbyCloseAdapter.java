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
import com.wingfac.laundry.bean.NearbyCloseBean;
import com.wingfac.laundry.bean.base.Constant;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class NearbyCloseAdapter extends BaseAdapter {
    private Context context;
    private NearbyCloseBean list;

    public NearbyCloseAdapter(Context context, NearbyCloseBean list) {
        this.context = context;
        if (list == null) {
            this.list = new NearbyCloseBean();
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
                    R.layout.item_fragment_location_colse, null);
            viewHolder.details = view.findViewById(R.id.item_fragment_location_close_details);
            viewHolder.name = view.findViewById(R.id.item_fragment_location_close_name);
            viewHolder.use = view.findViewById(R.id.item_fragment_location_close_use);
            viewHolder.surplus = view.findViewById(R.id.item_fragment_location_close_surplus);
            viewHolder.address = view.findViewById(R.id.item_fragment_location_close_address);
            viewHolder.all = view.findViewById(R.id.item_fragment_location_close_all);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.address.setText(list.obj.get(i).cabinet_adderss);
        viewHolder.name.setText(list.obj.get(i).deviceName);
        viewHolder.all.setText(list.obj.get(i).cabinet_total);
        viewHolder.use.setText(list.obj.get(i).total_used);
        viewHolder.surplus.setText(list.obj.get(i).unused_total);
        viewHolder.details.setText(getDistance(i));
        return view;
    }

    String getDistance(int i) {
        LatLng latLng1 = new LatLng(list.obj.get(i).eLatitude, list.obj.get(i).eLongitude);
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
        public TextView details;
        public TextView use;
        public TextView surplus;
        public TextView address;
        public TextView all;
    }
}
