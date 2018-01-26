package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/19 0019.
 */

public class CommercialOrderBean extends Base {
    public List<CommercialOrder> obj = new ArrayList<>();
    public static class CommercialOrder {
        public long so_id;
        public int s_id;
        public int c_id;
        public int au_id;
        public String so_number;
        public String nickname;
        public String tel;
        public String s_name;
        public String s_mobile;
        public String first_picture;
        public String c_name;
        public Double unit_price;
        public String c_introduce;
        public int quantity_purchased;
        public int so_total;
        public String order_time;
        public String payment_time;
        public String delivery_address;
        public String so_state;
        public String guest_book;
        public String so_distribution;

    }
}
