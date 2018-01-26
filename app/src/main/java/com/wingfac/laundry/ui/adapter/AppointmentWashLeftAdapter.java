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
import com.wingfac.laundry.bean.AppointmentWashLeftBean;
import com.wingfac.laundry.bean.base.Constant;


/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class AppointmentWashLeftAdapter extends BaseAdapter {
    public Context context;
    public AppointmentWashLeftBean list;
    private Holder holder;

    public AppointmentWashLeftAdapter(Context context, AppointmentWashLeftBean list) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_appointment_wash_left, null);
            holder.img = view.findViewById(R.id.item_appointment_wash_left_img);
            holder.name = view.findViewById(R.id.item_appointment_wash_left_name);
            holder.leftView = view.findViewById(R.id.item_appointment_wash_left_left_view);
            holder.rightView = view.findViewById(R.id.item_appointment_wash_left_right_view);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (list.data.get(i).state) {
            holder.leftView.setVisibility(View.VISIBLE);
            holder.rightView.setVisibility(View.GONE);
        } else {
            holder.leftView.setVisibility(View.GONE);
            holder.rightView.setVisibility(View.VISIBLE);
        }
        if (list.data.get(i).img.equals("1")) {
            Glide.with(context)
                    .load(R.drawable.shangyi)
                    .dontAnimate()
                    .placeholder(R.drawable.erro_store)
                    .into(holder.img);
        } else if (list.data.get(i).img.equals("2")) {
            Glide.with(context)
                    .load(R.drawable.dayi)
                    .dontAnimate()
                    .placeholder(R.drawable.erro_store)
                    .into(holder.img);
        } else if (list.data.get(i).img.equals("3")) {
            Glide.with(context)
                    .load(R.drawable.kuzi)
                    .dontAnimate()
                    .placeholder(R.drawable.erro_store)
                    .into(holder.img);
        } else if (list.data.get(i).img.equals("4")) {
            Glide.with(context)
                    .load(R.drawable.xiezi)
                    .dontAnimate()
                    .placeholder(R.drawable.erro_store)
                    .into(holder.img);
        } else if (list.data.get(i).img.equals("5")) {
            Glide.with(context)
                    .load(R.drawable.xuezi)
                    .dontAnimate()
                    .placeholder(R.drawable.erro_store)
                    .into(holder.img);
        } else if (list.data.get(i).img.equals("6")) {
            Glide.with(context)
                    .load(R.drawable.jiafang)
                    .dontAnimate()
                    .placeholder(R.drawable.erro_store)
                    .into(holder.img);
        } else if (list.data.get(i).img.equals("7")) {
            Glide.with(context)
                    .load(R.drawable.peijian)
                    .dontAnimate()
                    .placeholder(R.drawable.erro_store)
                    .into(holder.img);
        } else {
            Glide.with(context)
                    .load(Constant.BASE_IMG + list.data.get(i).img)
                    .dontAnimate()
                    .placeholder(R.drawable.erro_store)
                    .into(holder.img);
        }
        holder.name.setText(list.data.get(i).name);
        return view;
    }

    public static class Holder {
        public ImageView img;
        public TextView name;
        public View leftView;
        public View rightView;
    }
}
