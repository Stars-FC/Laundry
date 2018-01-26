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
import com.wingfac.laundry.bean.AppointmentWashRightBean;
import com.wingfac.laundry.bean.CloseBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.base.Constant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class AppointmentWashTwoRightAdapter extends BaseAdapter {
    public Context context;
    public AppointmentWashRightBean list;
    private Holder holder;
    private String state;
    public Map<Integer, CloseBean> rightData = new HashMap<>();
    private Integer ccId;
    private TextView all, num;
    private Double vipPrice;

    public AppointmentWashTwoRightAdapter(Context context, AppointmentWashRightBean list,
                                          Map<Integer, CloseBean> rightData, String state,
                                          TextView all, TextView num,
                                          Double vipPrice) {
        this.context = context;
        this.list = list;
        this.rightData = rightData;
        this.state = state;
        this.all = all;
        this.num = num;
        this.vipPrice = vipPrice;
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
            view = LayoutInflater.from(context).inflate(R.layout.item_appointment_wash_right_two, null);
            holder.img = view.findViewById(R.id.item_appointment_wash_right_img);
            holder.name = view.findViewById(R.id.item_appointment_wash_right_text);
            holder.price = view.findViewById(R.id.item_appointment_wash_right_price);
            holder.subtract = view.findViewById(R.id.item_appointment_wash_right_subtract);
            holder.num = view.findViewById(R.id.item_appointment_wash_right_num);
            holder.add = view.findViewById(R.id.item_appointment_wash_right_add);
            holder.vipPrice = view.findViewById(R.id.item_appointment_wash_right_vip_price);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        String ze = String.valueOf(div(list.data.get(i).vip_percent, 100.0));
        holder.vipPrice.setText("会员享" + ze.substring(0, ze.indexOf(".")) + "折");
        holder.price.setText("￥ " + list.data.get(i).price);
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
            double price = 0.0;
            List<CloseBean> mapValuesList = new ArrayList(rightData.values());
            for (int x = 0; x < mapValuesList.size(); x++) {
                for (int j = 0; j < mapValuesList.get(x).obj.size(); j++) {
                    Inum += mapValuesList.get(x).obj.get(j).num;
                    vipPrice += (mapValuesList.get(x).obj.get(j).num * (mul(mapValuesList.get(x).obj.get(j).price, div(mapValuesList.get(x).obj.get(j).vip_percent, 100.0))));
                    price += (mapValuesList.get(x).obj.get(j).num * mapValuesList.get(x).obj.get(j).price);

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
                List<CloseBean> mapValuesList = new ArrayList(rightData.values());
                for (int x = 0; x < mapValuesList.size(); x++) {
                    for (int j = 0; j < mapValuesList.get(x).obj.size(); j++) {
                        Inum += mapValuesList.get(x).obj.get(j).num;
                        vipPrice += (mapValuesList.get(x).obj.get(j).num * (mul(mapValuesList.get(x).obj.get(j).price, div(mapValuesList.get(x).obj.get(j).vip_percent, 100.0))));
                        price += (mapValuesList.get(x).obj.get(j).num * mapValuesList.get(x).obj.get(j).price);
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

    /**
     * * 两个Double数相乘 *
     *
     * @param v1 *
     * @param v2 *
     * @return Double
     */
    public static Double mul(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return new Double(b1.multiply(b2).doubleValue());
    }

    private static final int DEF_DIV_SCALE = 10;

    /**
     * * 两个Double数相除 *
     *
     * @param v1 *
     * @param v2 *
     * @return Double
     */
    public static Double div(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return new Double(b1.divide(b2, DEF_DIV_SCALE, BigDecimal.ROUND_HALF_UP)
                .doubleValue());
    }

    public static class Holder {
        public ImageView img;
        public TextView name;
        public TextView price;
        public TextView vipPrice;
        public ImageView subtract;
        public TextView num;
        public ImageView add;

    }
}
