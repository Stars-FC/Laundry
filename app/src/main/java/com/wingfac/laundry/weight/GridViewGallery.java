package com.wingfac.laundry.weight;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wingfac.laundry.R;
import com.wingfac.laundry.bean.InfoBean;
import com.wingfac.laundry.ui.adapter.ViewPager_GV_ItemAdapter;
import com.wingfac.laundry.ui.adapter.ViewPager_GridView_Adapter;
import com.yuyh.library.utils.DimenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/26 0026.
 */

public class GridViewGallery extends LinearLayout {
    private Context context;
    /**
     * 保存实体对象链表
     */
    private List<InfoBean> list;
    private ViewPager viewPager;
    private LinearLayout ll_dot;
    private ImageView[] dots;
    /**
     * ViewPager当前页
     */
    private int currentIndex;
    /**
     * ViewPager页数
     */
    private int viewPager_size;
    /**
     * 默认一页12个item
     */
    private int pageItemCount = 12;

    /**
     * 保存每个页面的GridView视图
     */
    private List<View> list_Views;

    public GridViewGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.list = null;
        initView();
    }

    @SuppressWarnings("unchecked")
    public GridViewGallery(Context context, List<?> list) {
        super(context);
        this.context = context;
        this.list = (List<InfoBean>) list;
        initView();
        initDots();
        setAdapter();
    }

    private void setAdapter() {
        list_Views = new ArrayList<View>();
        for (int i = 0; i < viewPager_size; i++) {
            list_Views.add(getViewPagerItem(i));
        }
        viewPager.setAdapter(new ViewPager_GridView_Adapter((ArrayList<View>) list_Views));
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.chanel_activity, null);
        viewPager = (ViewPager) view.findViewById(R.id.vPager);
        ll_dot = (LinearLayout) view.findViewById(R.id.ll_dots);
        addView(view);
    }

    // 初始化底部小圆点
    private void initDots() {

        // 根据屏幕宽度高度计算pageItemCount

        int width = DimenUtils.getScreenWidth();
        int high = DimenUtils.getScreenHeight();

        int col = (width / 160) > 2 ? (width / 160) : 3;
        int row = (high / 200) > 4 ? (high / 200) : 4;

        pageItemCount = col * row;
        viewPager_size = list.size() / pageItemCount + 1;

        if (0 < viewPager_size) {
            ll_dot.removeAllViews();
            if (1 == viewPager_size) {
                ll_dot.setVisibility(View.GONE);
            } else if (1 < viewPager_size) {
                ll_dot.setVisibility(View.VISIBLE);
                for (int j = 0; j < viewPager_size; j++) {
                    ImageView image = new ImageView(context);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
                    params.setMargins(3, 0, 3, 0);
                    image.setBackgroundResource(R.drawable.chanel_dot);
                    ll_dot.addView(image, params);
                }
            }
        }
        if (viewPager_size != 1) {
            dots = new ImageView[viewPager_size];
            for (int i = 0; i < viewPager_size; i++) {
                dots[i] = (ImageView) ll_dot.getChildAt(i);
                dots[i].setEnabled(true);
                dots[i].setTag(i);
            }
            currentIndex = 0;
            dots[currentIndex].setEnabled(false);
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int arg0) {
                    setCurDot(arg0);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                    // TODO Auto-generated method stub

                }
            });
        }
    }

    /**
     * 当前底部小圆点
     */
    private void setCurDot(int positon) {
        if (positon < 0 || positon > viewPager_size - 1 || currentIndex == positon) {
            return;
        }
        dots[positon].setEnabled(false);
        dots[currentIndex].setEnabled(true);
        currentIndex = positon;
    }

    private View getViewPagerItem(int index) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.channel_viewpage_gridview, null);
        GridView gridView = (GridView) layout.findViewById(R.id.vp_gv);

        int width = DimenUtils.getScreenWidth();
        int col = (width / 160) > 2 ? (width / 160) : 3;
        gridView.setNumColumns(col);

        ViewPager_GV_ItemAdapter adapter = new ViewPager_GV_ItemAdapter(context, list, index, pageItemCount);

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            if (null != list.get(position + currentIndex * pageItemCount).getOnClickListener())
                list.get(position + currentIndex * pageItemCount).getOnClickListener().ongvItemClickListener(view);
        });
        return gridView;
    }
}
