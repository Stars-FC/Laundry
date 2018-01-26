package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.bean.CommodityBean;
import com.wingfac.laundry.bean.base.Constant;
import com.wingfac.laundry.ui.activity.CommodityDetailsActivity;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class CartAdapter extends BaseAdapter {
    private Context context;
    private CommodityBean list;
    private CommodityBean selectedList;

    public CartAdapter(Context context, CommodityBean list) {
        this.context = context;
        selectedList = new CommodityBean();
        if (list == null) {
            this.list = new CommodityBean();
        } else {
            this.list = list;
        }
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(
                    R.layout.item_mine_cart, null);
            viewHolder.cbChecked = view.findViewById(R.id.item_cart_check);
            viewHolder.img = view.findViewById(R.id.item_cart_img);
            viewHolder.name = view.findViewById(R.id.item_cart_name);
            viewHolder.nature = view.findViewById(R.id.item_cart_nature);
            viewHolder.price = view.findViewById(R.id.item_cart_price);
            viewHolder.buy = view.findViewById(R.id.item_cart_buy);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.name.setText(list.obj.get(i).c_name);
        viewHolder.nature.setText("描述" + "\t" + "\t" + list.obj.get(i).c_introduce);
        viewHolder.price.setText("价格" + "\t" + "\t" + "¥" + list.obj.get(i).unit_price);
        Glide.with(context)
                .load(Constant.BASE_IMG + list.obj.get(i).first_picture)
                .dontAnimate()
                .placeholder(R.drawable.erro_store)
                .into(viewHolder.img);
        viewHolder.cbChecked.setChecked(isSelected(list.obj.get(i)));
        viewHolder.buy.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, CommodityDetailsActivity.class);
            intent.putExtra("c_id",String.valueOf(list.obj.get(i).c_id));
            intent.putExtra("s_id",String.valueOf(list.obj.get(i).s_id));
            context.startActivity(intent);
        });
        return view;
    }

    private boolean isSelected(CommodityBean.Commodity cart) {
        return selectedList.obj.contains(cart);
        //return true;
    }

    public void toggleChecked(int position) {
        if (isSelected(list.obj.get(position))) {
            removeSelected(position);
        } else {
            setSelected(position);
        }

    }

    private void setSelected(int position) {
        if (!selectedList.obj.contains(list.obj.get(position))) {
            selectedList.obj.add(list.obj.get(position));
        }
    }

    private void removeSelected(int position) {
        if (selectedList.obj.contains(list.obj.get(position))) {
            selectedList.obj.remove(list.obj.get(position));
        }
    }

    public CommodityBean getSelectedList() {
        return selectedList;
    }

    public static class ViewHolder {
        public TextView name;
        public TextView price;
        public TextView nature;
        public ImageView img;
        public CheckBox cbChecked;
        public Button buy;
    }
}
