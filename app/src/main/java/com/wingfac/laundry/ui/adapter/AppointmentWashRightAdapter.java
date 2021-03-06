package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.bean.AppointmentWashRightBean;
import com.wingfac.laundry.bean.CloseBean;
import com.wingfac.laundry.bean.CommodityBean;
import com.wingfac.laundry.bean.base.Constant;
import com.yuyh.library.utils.DimenUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class AppointmentWashRightAdapter extends BaseAdapter {
    public Context context;
    public AppointmentWashRightBean list;
    private Holder holder;
    private String state;
    public Map<Integer, CommodityBean> rightData = new HashMap<>();
    private Integer ccId;
    private TextView all, num;

    public AppointmentWashRightAdapter(Context context, AppointmentWashRightBean list, Map<Integer, CommodityBean> rightData, String state, TextView all, TextView num) {
        this.context = context;
        this.list = list;
        this.rightData = rightData;
        this.state = state;
        this.all = all;
        this.num = num;
    }

    public void setCcId(Integer ccId) {
        this.ccId = ccId;
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
            view = LayoutInflater.from(context).inflate(R.layout.item_appointment_wash_right, null);
            holder.img = view.findViewById(R.id.item_appointment_wash_right_img);
            holder.name = view.findViewById(R.id.item_appointment_wash_right_text);
            holder.price = view.findViewById(R.id.item_appointment_wash_right_price);
            holder.subtract = view.findViewById(R.id.item_appointment_wash_right_subtract);
            holder.num = view.findViewById(R.id.item_appointment_wash_right_num);
            holder.add = view.findViewById(R.id.item_appointment_wash_right_add);
            holder.good = view.findViewById(R.id.item_activity_shop_list_good);
            holder.person = view.findViewById(R.id.item_activity_shop_list_person);
            holder.song = view.findViewById(R.id.item_fragment_home_distribution);
            holder.two = view.findViewById(R.id.item_appointemt_two);
            holder.three = view.findViewById(R.id.item_appointemt_three);
            holder.oneTx = view.findViewById(R.id.one);
            holder.twoTx = view.findViewById(R.id.two);
            holder.threeTx = view.findViewById(R.id.three);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (state.equals("1")) {
            holder.three.setVisibility(View.GONE);
            holder.oneTx.setText("￥"+list.data.get(i).price);
            holder.twoTx.setVisibility(View.GONE);
            holder.threeTx.setVisibility(View.GONE);
            holder.person.setVisibility(View.GONE);
            holder.good.setVisibility(View.GONE);
        } else {
            holder.price.setText(list.data.get(i).price);
            holder.three.setVisibility(View.VISIBLE);
            holder.good.setText(list.data.get(i).good);
            holder.person.setText(list.data.get(i).person);
        }
        Glide.with(context)
                .load(Constant.BASE_IMG + list.data.get(i).img)
                .dontAnimate()
                .placeholder(R.drawable.erro_store)
                .into(holder.img);
        holder.name.setText(list.data.get(i).name);
        holder.num.setText(String.valueOf(list.data.get(i).num));
        if (list.data.get(i).num <= 0) {
            holder.subtract.setVisibility(View.GONE);
            holder.num.setVisibility(View.GONE);
        } else {
            holder.subtract.setVisibility(View.VISIBLE);
            holder.num.setVisibility(View.VISIBLE);
        }

        holder.add.setOnClickListener(view12 -> {
            list.data.get(i).state = true;
            rightData.get(ccId).obj.get(i).state = list.data.get(i).state;
            list.data.get(i).num += 1;
            rightData.get(ccId).obj.get(i).num = list.data.get(i).num;
            int Inum = 0;
            Double price = 0.0;
            List<CommodityBean> mapValuesList = new ArrayList(rightData.values());
            for(int x = 0;x<mapValuesList.size();x++){
                for (int j = 0; j < mapValuesList.get(x).obj.size(); j++) {
                    Inum += mapValuesList.get(x).obj.get(j).num;
                    price += (mapValuesList.get(x).obj.get(j).num * mapValuesList.get(x).obj.get(j).unit_price);
                }
            }

            all.setText(String.valueOf(price));
            num.setText(String.valueOf(Inum));
            notifyDataSetChanged();
        });
        holder.subtract.setOnClickListener(view1 -> {
            if (list.data.get(i).num > 0) {
                list.data.get(i).num -= 1;
                rightData.get(ccId).obj.get(i).num = list.data.get(i).num;
                int Inum = 0;
                Double price = 0.0;
                List<CommodityBean> mapValuesList = new ArrayList(rightData.values());
                for(int x = 0;x<mapValuesList.size();x++){
                    for (int j = 0; j < mapValuesList.get(x).obj.size(); j++) {
                        Inum += mapValuesList.get(x).obj.get(j).num;
                        price += (mapValuesList.get(x).obj.get(j).num * mapValuesList.get(x).obj.get(j).unit_price);
                    }
                }
                all.setText(String.valueOf(price));
                num.setText(String.valueOf(Inum));
                notifyDataSetChanged();
            }
            if (list.data.get(i).num == 0) {
                list.data.get(i).state = false;
                rightData.get(ccId).obj.get(i).state = list.data.get(i).state;
            }
        });
        return view;
    }

    public static class Holder {
        public ImageView img;
        public TextView name;
        public TextView price;
        public ImageView subtract;
        public TextView num;
        public ImageView add;
        public TextView good;
        public TextView person;
        public TextView song;
        public LinearLayout two;
        public LinearLayout three;
        public TextView oneTx;
        public TextView twoTx;
        public TextView threeTx;

    }
}
