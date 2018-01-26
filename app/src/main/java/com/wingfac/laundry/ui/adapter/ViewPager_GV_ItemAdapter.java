package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.bean.InfoBean;
import com.wingfac.laundry.bean.base.Constant;
import com.yuyh.library.utils.DimenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/26 0026.
 */

public class ViewPager_GV_ItemAdapter extends BaseAdapter {
    private ArrayList<InfoBean> infolist;
    private Context context;
    /**
     * ViewPager页码
     */
    private int index;
    /**
     * 根据屏幕大小计算得到的每页item个数
     */
    private int pageItemCount;
    /**
     * 传进来的List的总长度
     */
    private int totalSize;

    public ViewPager_GV_ItemAdapter(Context context, ArrayList<InfoBean> list) {
        this.context = context;
        infolist = list;
    }

    public ViewPager_GV_ItemAdapter(Context context, List<?> list, int index, int pageItemCount) {
        this.context = context;
        this.index = index;
        this.pageItemCount = pageItemCount;
        infolist = new ArrayList<InfoBean>();
        totalSize = list.size();  //36   40
        // itemRealNum=list.size()-index*pageItemCount;
        // 当前页的item对应的实体在List<?>中的其实下标
        int list_index = index * pageItemCount;
        for (int i = list_index; i < list.size(); i++) {
            infolist.add((InfoBean) list.get(i));
        }

    }

    @Override
    public int getCount() {
        int size = totalSize / pageItemCount;
        if (index == size) {
            return totalSize - pageItemCount * index;
        } else {
            return pageItemCount;
        }
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return infolist.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoler vh;
        if (convertView == null) {
            vh = new ViewHoler();
            convertView = LayoutInflater.from(context).inflate(R.layout.channel_gridview_item, null);
            vh.iv_icon = convertView.findViewById(R.id.iv_gv_item_icon);
            vh.tv_mame = convertView.findViewById(R.id.tv_gv_item_Name);
            vh.rootLayout = convertView.findViewById(R.id.channel_gridview_item_root);
            convertView.setTag(vh);
        } else {
            vh = (ViewHoler) convertView.getTag();
        }
        vh.updateViews(position, null);
        return convertView;
    }

    class ViewHoler {
        ImageView iv_icon;
        TextView tv_mame;
        RelativeLayout rootLayout;

        public void updateViews(int position, Object inst) {
            Glide.with(context)
                    .load(Constant.BASE_IMG + infolist.get(position).getIconUrl())
                    .dontAnimate()
                    .placeholder(R.drawable.erro_store)
                    .into(iv_icon);
            tv_mame.setText(infolist.get(position).getName());
            int width = DimenUtils.getScreenWidth() / 3;
            ViewGroup.LayoutParams params = rootLayout.getLayoutParams();
            params.height = width;
            params.width = width;
            rootLayout.setLayoutParams(params);
            ViewGroup.LayoutParams params1 = iv_icon.getLayoutParams();
            params1.height = width - 150;
            params1.width = width - 150;
            iv_icon.setLayoutParams(params1);
        }
    }

}
