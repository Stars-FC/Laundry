package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.bean.ShoppingLogisticsBean;


/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class ShoppingLogisticsAdapter extends BaseAdapter {
    public Context context;
    public ShoppingLogisticsBean list;
    private Holder holder;

    public ShoppingLogisticsAdapter(Context context, ShoppingLogisticsBean list) {
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
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(context).inflate(R.layout.item_shopping_logistics, null);
            holder.info = view.findViewById(R.id.item_wash_logistics_info);
            holder.time = view.findViewById(R.id.item_wash_logistics_time);
            holder.view = view.findViewById(R.id.item_shopping_logistics_view);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.info.setText(list.obj.get(i).lc_content);
        holder.time.setText(list.obj.get(i).lc_time);
        if (i == 0) {
            holder.info.setTextColor(context.getResources().getColor(R.color.them));
            holder.time.setTextColor(context.getResources().getColor(R.color.them));
            holder.view.setVisibility(View.GONE);
        } else {
            holder.info.setTextColor(context.getResources().getColor(R.color.default_text));
            holder.time.setTextColor(context.getResources().getColor(R.color.default_text));
            holder.view.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public static class Holder {
        public TextView info;
        public TextView time;
        public View view;
    }
}
