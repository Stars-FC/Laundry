package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wingfac.laundry.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/22 0022.
 */

public class CommercialAddShopAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;
    private List<String> cache = new ArrayList<>();
    private boolean isStater;

    public CommercialAddShopAdapter(Context context, List<String> list, List<String> cache, boolean isStater) {
        this.context = context;
        this.list = list;
        this.cache = cache;
        this.isStater = isStater;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Holder holder = null;
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(context).inflate(
                    R.layout.item_activity_shop_add_shop, null);
            holder.img = view.findViewById(R.id.item_activity_shop_add_shop_img);
            holder.clear = view.findViewById(R.id.clear_button);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (list.get(i).toString().equals("000000")) {
            holder.img.setImageResource(R.drawable.add_big);
            holder.clear.setVisibility(View.GONE);
        } else {
            if (isStater) {
                holder.clear.setVisibility(View.VISIBLE);
                holder.clear.setOnClickListener(view1 -> {
                    cache.remove(i);
                    list.remove(i);
                    notifyDataSetChanged();
                });
            } else {
                holder.clear.setVisibility(View.GONE);
            }
            Glide.with(context)
                    .load(list.get(i))
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.erro_store)
                    .into(holder.img);
        }
        return view;
    }

    private class Holder {
        public ImageView img;
        public ImageView clear;
    }
}
