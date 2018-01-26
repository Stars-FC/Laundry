package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.bean.CommodityBean;
import com.wingfac.laundry.bean.base.Constant;
import com.wingfac.laundry.support.OnListItemClickListener;
import com.wingfac.laundry.utiil.ItemAnimHelper;
import com.yuyh.easyadapter.recyclerview.EasyRVAdapter;
import com.yuyh.easyadapter.recyclerview.EasyRVHolder;
import com.yuyh.library.utils.DimenUtils;

import java.util.List;

/**
 * Created by Kyrie.Y on 2016/6/6.
 */
public class YingkeAdapter extends EasyRVAdapter<CommodityBean.Commodity> {
    private Context context;
    private OnListItemClickListener<CommodityBean.Commodity> mOnItemClickListener = null;
    private ItemAnimHelper helper = new ItemAnimHelper();

    public YingkeAdapter(List<CommodityBean.Commodity> data, Context context) {
        super(context, data, R.layout.item_commercial_fragment_shop_grid);
        this.context = context;
    }

    public void setOnItemClickListener(OnListItemClickListener<CommodityBean.Commodity> mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public int getLayoutIndex(int position, CommodityBean.Commodity item) {
        return 0;
    }

    @Override
    protected void onBindData(final EasyRVHolder viewHolder, final int position, final CommodityBean.Commodity item) {
        final ImageView videoPlayer = viewHolder.getView(R.id.item_commercial_fragment_shop_gird_img);
        ViewGroup.LayoutParams params = videoPlayer.getLayoutParams();
        params.height = DimenUtils.getScreenWidth() / 2;
        params.width = DimenUtils.getScreenWidth() / 2;
        videoPlayer.setLayoutParams(params);
        Glide.with(context)
                .load(Constant.BASE_IMG + item.first_picture)
                .dontAnimate()
                .placeholder(R.drawable.erro_store)
                .into(videoPlayer);
        viewHolder.itemView.setOnClickListener(view -> {
            if(mOnItemClickListener!=null){
                mOnItemClickListener.onItemClick(view,position,item);
            }
        });

        helper.showItemAnim(viewHolder.getItemView(), position);
    }

}
