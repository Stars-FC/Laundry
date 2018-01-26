package com.wingfac.laundry.bean;

import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Administrator on 2017/7/26 0026.
 */

public class InfoBean implements Comparable<InfoBean> {
    private int id;
    private String name;
    private Drawable icon;
    private String iconUrl;
    private int iconID;
    private String describtion;
    private int type;
    // 排序标记
    private int order;

    private onGridViewItemClickListener onClickListener;


    public InfoBean(String name, String iconUrl, int id) {
        super();
        this.name = name;
        this.iconUrl = iconUrl;
        this.id = id;
    }

    public InfoBean() {
        super();
    }

    //得到排序的List
    public static ArrayList<InfoBean> getOrderList(ArrayList<InfoBean> list) {
        Collections.sort(list);
        return list;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    public String getDescribtion() {
        return describtion;
    }

    public void setDescribtion(String describtion) {
        this.describtion = describtion;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int compareTo(InfoBean another) {
        if (another != null) {
            if (this.getOrder() > another.getOrder()) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return 0;
        }
    }

    public onGridViewItemClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(onGridViewItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //item点击监听接口
    public interface onGridViewItemClickListener {
        public abstract void ongvItemClickListener(View v);
    }
}
