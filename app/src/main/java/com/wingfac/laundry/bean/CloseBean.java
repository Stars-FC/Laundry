package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/6 0006.
 */

public class CloseBean extends Base {
    public List<Close> obj = new ArrayList<>();
    public class Close{
        public int g_id;
        public String goods_name;
        public String number;
        public String pic;
        public String describes;
        public Double vip_percent;
        public Double vip_price;
        public Double price;
        public int num;
        public boolean state;
    }
}
