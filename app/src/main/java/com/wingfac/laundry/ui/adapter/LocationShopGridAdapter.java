package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.bean.LocationGridBean;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class LocationShopGridAdapter extends BaseAdapter {
    private Context context;
    private LocationGridBean list;
    private Holder holder;

    public LocationShopGridAdapter(Context context, LocationGridBean list) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_location_shop_grid, null);
            holder.name = view.findViewById(R.id.item_location_shop_grid_name);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (list.data.get(i).state) {
            holder.name.setBackgroundResource(R.drawable.circular_button);
            holder.name.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.name.setBackgroundResource(R.drawable.circular_button_white);
            holder.name.setTextColor(context.getResources().getColor(R.color.default_text));
        }
        holder.name.setText(list.data.get(i).name);
        return view;
    }

    public static class Holder {
        public TextView name;

    }
}
