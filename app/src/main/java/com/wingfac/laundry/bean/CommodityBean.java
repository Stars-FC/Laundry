package com.wingfac.laundry.bean;


import com.wingfac.laundry.bean.base.Base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public class CommodityBean {
    public List<Commodity> obj = new ArrayList<>();
    public String responseStatus;
    public String msg;
    public class Commodity implements Serializable {
        public int cc_id;
        public int c_id;
        public int s_id;
        public String first_picture;
        public double unit_price;
        public String picture;
        public String c_name;
        public String c_standard;
        public String c_size;
        public String c_unit;
        public double s_longitude;
        public int s_praise;
        public int s_popularity;
        public double s_latitude;
        public String c_introduce;
        public String picture_one;
        public String picture_two;
        public int num;
        public String picture_three;
        public Boolean state;
    }
}
