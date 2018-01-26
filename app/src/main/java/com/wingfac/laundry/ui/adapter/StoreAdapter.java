package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.bean.StoreBean;
import com.wingfac.laundry.bean.base.Constant;

/**
 * Created by Administrator on 2017/5/16 0016.
 */

public class StoreAdapter extends BaseAdapter {
    private Context context;
    private StoreBean list;

    public StoreAdapter(Context context, StoreBean list) {
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
                    R.layout.item_fragment_shop, null);
            viewHolder.name = view.findViewById(R.id.item_fragment_home_name);
            viewHolder.describe = view.findViewById(R.id.item_fragment_home_describe);
            viewHolder.img = view.findViewById(R.id.item_fragment_home_img);
            viewHolder.good_reputation = view.findViewById(R.id.item_fragment_home_good_reputation);
            viewHolder.popularity = view.findViewById(R.id.item_fragment_home_popularity);
            viewHolder.distribution = view.findViewById(R.id.item_fragment_home_distribution);
            view.setTag(viewHolder);
        } else {
            viewHolder = (Holder) view.getTag();
        }
        viewHolder.name.setText(list.obj.get(i).s_name);
        viewHolder.describe.setText("商品描述:" + list.obj.get(i).describe);
        viewHolder.good_reputation.setText(String.valueOf(list.obj.get(i).s_praise));
        viewHolder.popularity.setText(String.valueOf(list.obj.get(i).s_popularity));
        viewHolder.distribution.setText(list.obj.get(i).s_distribution);
        Glide.with(context)
                .load(Constant.BASE_IMG + list.obj.get(i).s_logo)
                .dontAnimate()
                .placeholder(R.drawable.erro_store)
                .into(viewHolder.img);
        return view;
    }

    private class Holder {
        public ImageView img;
        public TextView name;
        public TextView describe;
        public TextView good_reputation;
        public TextView popularity;
        public TextView distribution;
    }
}
