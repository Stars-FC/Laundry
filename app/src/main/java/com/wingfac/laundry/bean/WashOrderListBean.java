package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/4 0004.
 */

public class WashOrderListBean extends Base {
    public List<WashOrderList> data = new ArrayList<>();
    public class WashOrderList {
        public String guest_book;
        public String arrival_time;
        public String address;
        public String to_subdivide;
        public String eqpt_id;
        public String payment_time;
        public String num;
        public String pickup_password;
        public String lo_type;
        public String order_time;
        public String delivery_time;
        public String pick_up_time;
        public String lo_state;
        public double lo_total;
        public String lo_number;
        public long lo_id;
        public String pickup_code;
        public int a_id;
        public String nickname;
        public int c_id;
        public int e_id;
        public String tel;
        public int id;
        public List<Wash> goods = new ArrayList<>();
    }
    public class Wash{
        public String goods_name;
        public String pic;
        public int log_num;
        public double price;
    }
}
