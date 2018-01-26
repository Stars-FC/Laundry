package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.bean.SearchHistoryBean;


/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class SearchHistoryAdapter extends BaseAdapter {
    public Context context;
    public SearchHistoryBean list;
    private Holder holder;

    public SearchHistoryAdapter(Context context, SearchHistoryBean list) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_search_history, null);
            holder.name = view.findViewById(R.id.item_search_history_text);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.name.setText(list.data.get(i));
        return view;
    }

    public static class Holder {
        public TextView name;

    }
}
