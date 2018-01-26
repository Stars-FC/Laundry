package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.bean.WashLogisticsBean;


/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class WashLogisticsAdapter extends BaseAdapter {
    public Context context;
    public WashLogisticsBean list;
    private Holder holder;

    public WashLogisticsAdapter(Context context, WashLogisticsBean list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.data.size();
    }

    @Override
    public Object getItem(int i) {
        return list.data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(context).inflate(R.layout.item_wash_details, null);
            holder.info = view.findViewById(R.id.item_wash_details_info);
            holder.time = view.findViewById(R.id.item_wash_details_time);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.info.setText(list.data.get(i).info);
        holder.time.setText(list.data.get(i).time);
        return view;
    }

    public static class Holder {
        public TextView info;
        public TextView time;
    }
}
