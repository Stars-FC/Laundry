package com.wingfac.laundry.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/26 0026.
 */

public class ViewPager_GridView_Adapter extends PagerAdapter {
    ArrayList<View> viewlist;

    public ViewPager_GridView_Adapter(ArrayList<View> viewlist) {
        this.viewlist = viewlist;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return viewlist.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        //return false;
        return arg0 == (arg1);
    }

    public Object instantiateItem(View arg0, int arg1) {
        try {
            //解决View只能滑动两屏的方法
            ViewGroup parent = (ViewGroup) viewlist.get(arg1).getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
            //container.addView(v);
            ((ViewPager) arg0).addView(viewlist.get(arg1), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return viewlist.get(arg1);
    }


    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        try {
            ((ViewPager) arg0).removeView(viewlist.get(arg1));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

}
