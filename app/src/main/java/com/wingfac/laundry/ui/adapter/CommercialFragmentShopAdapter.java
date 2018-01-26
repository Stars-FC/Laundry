package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.bean.CommodityBean;
import com.wingfac.laundry.bean.base.Constant;
import com.yuyh.library.utils.DimenUtils;

/**
 * Created by Administrator on 2017/5/19 0019.
 */

public class CommercialFragmentShopAdapter extends BaseAdapter {
    private Context context;
    private CommodityBean list;

    public CommercialFragmentShopAdapter(Context context, CommodityBean list) {
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
                    R.layout.item_commercial_fragment_shop_grid, null);
            viewHolder.img =  view.findViewById(R.id.item_commercial_fragment_shop_gird_img);
            view.setTag(viewHolder);
        } else {
            viewHolder = (Holder) view.getTag();
        }
        ViewGroup.LayoutParams params = viewHolder.img.getLayoutParams();
        params.height = DimenUtils.getScreenWidth() / 2;
        params.width = DimenUtils.getScreenWidth() / 2;
        viewHolder.img.setLayoutParams(params);
        if(!list.obj.get(i).c_introduce.equals("0")){
            Glide.with(context)
                    .load(Constant.BASE_IMG + list.obj.get(i).first_picture)
                    .dontAnimate()
                    .placeholder(R.drawable.erro_store)
                    .into(viewHolder.img);
        }


        return view;
    }

    private class Holder {
        public ImageView img;
    }
}
