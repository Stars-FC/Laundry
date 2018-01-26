package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.bean.AppointmentConfirmOrderBean;


/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class AppointmentWashConfirmOrderAdapter extends BaseAdapter {
    public Context context;
    public AppointmentConfirmOrderBean list;
    private Holder holder;

    public AppointmentWashConfirmOrderAdapter(Context context, AppointmentConfirmOrderBean list) {
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
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(context).inflate(R.layout.item_appointment_wash_confirm_order, null);
            holder.name = view.findViewById(R.id.item_appointment_wash_confirm_order_name);
            holder.num = view.findViewById(R.id.item_appointment_wash_confirm_order_num);
            holder.price = view.findViewById(R.id.item_appointment_wash_confirm_order_price);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.price.setText("￥" + list.obj1.get(i).price);
        holder.num.setText("x\t" + list.obj1.get(i).log_num);
        holder.name.setText(list.obj1.get(i).goods_name);
        return view;
    }

    public static class Holder {
        public TextView name;
        public TextView num;
        public TextView price;
    }
}
