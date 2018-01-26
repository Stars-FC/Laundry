package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/27 0027.
 */

public class WashDetailBean {
    public String responseStatus;
    public String msg;
    public Wash obj;
    public List<WashDetail> obj1 = new ArrayList<>();

    public class Wash {
        public String lo_number;
        public String address;
        public String pickup_code;
        public String pickup_password;
        public String guest_book;
    }

    public class WashDetail {
        public String goods_name;
        public Double price;
        public int log_num;
        public String pic;
    }
}
