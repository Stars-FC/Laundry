package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.bean.RecommendBean;


/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class RecommendAdapter extends BaseAdapter {
    public Context context;
    public RecommendBean list;
    private Holder holder;

    public RecommendAdapter(Context context, RecommendBean list) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_shop_search, null);
            holder.name = view.findViewById(R.id.item_search_history_text);
            holder.root = view.findViewById(R.id.item_shop_search_root);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (list.obj.get(i).state) {
            holder.root.setBackgroundResource(R.drawable.shop_search_circlr_select);
            holder.name.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.root.setBackgroundResource(R.drawable.shop_search_circlr);
            holder.name.setTextColor(context.getResources().getColor(R.color.search_text));
        }
        holder.name.setText(list.obj.get(i).rc_name);
        return view;
    }

    public static class Holder {
        public TextView name;
        public RelativeLayout root;

    }
}
