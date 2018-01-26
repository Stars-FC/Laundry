package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.bean.OrderBean;
import com.wingfac.laundry.bean.base.Constant;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class FragmentOrderAdapter extends BaseAdapter {
    private Context context;
    private OrderBean list;
    private OrderBean selectedList;

    public FragmentOrderAdapter(Context context, OrderBean list) {
        this.context = context;
        selectedList = new OrderBean();
        if (list == null) {
            this.list = new OrderBean();
        } else {
            this.list = list;
        }
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(
                    R.layout.item_fragment_order_list, null);
            viewHolder.cbChecked = view.findViewById(R.id.item_fragment_order_list_check);
            viewHolder.img = view.findViewById(R.id.item_fragment_order_list_img);
            viewHolder.name = view.findViewById(R.id.item_fragment_order_list_name);
            viewHolder.num = view.findViewById(R.id.item_fragment_order_list_num);
            viewHolder.price = view.findViewById(R.id.item_fragment_order_list_price);
            viewHolder.time = view.findViewById(R.id.item_fragment_order_list_time);
            viewHolder.state = view.findViewById(R.id.item_fragment_order_list_state);
            viewHolder.all = view.findViewById(R.id.item_fragment_order_list_all);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.time.setText("下单时间:" + list.data.get(i).time);
        viewHolder.name.setText("商品名称:" + list.data.get(i).name);
        viewHolder.num.setText(list.data.get(i).num);
        viewHolder.price.setText("¥" + list.data.get(i).price);
        viewHolder.all.setText("￥" + (Double.parseDouble(list.data.get(i).price) * Double.parseDouble(list.data.get(i).num)));
        Glide.with(context)
                .load(Constant.BASE_IMG + list.data.get(i).img)
                .dontAnimate()
                .placeholder(R.drawable.erro_store)
                .into(viewHolder.img);
        viewHolder.cbChecked.setChecked(isSelected(list.data.get(i)));
        viewHolder.cbChecked.setOnClickListener(view1 -> toggleChecked(i));
        viewHolder.state.setText(list.data.get(i).state);
        return view;
    }

    private boolean isSelected(OrderBean.Order cart) {
        return selectedList.data.contains(cart);
        //return true;
    }

    public void toggleChecked(int position) {
        if (isSelected(list.data.get(position))) {
            removeSelected(position);
        } else {
            setSelected(position);
        }

    }

    private void setSelected(int position) {
        if (!selectedList.data.contains(list.data.get(position))) {
            selectedList.data.add(list.data.get(position));
        }
    }

    private void removeSelected(int position) {
        if (selectedList.data.contains(list.data.get(position))) {
            selectedList.data.remove(list.data.get(position));
        }
    }

    public OrderBean getSelectedList() {
        return selectedList;
    }

    public static class ViewHolder {
        public TextView name;
        public TextView price;
        public TextView num;
        public ImageView img;
        public CheckBox cbChecked;
        public TextView time;
        public TextView state;
        public TextView all;
    }
}
