package com.wingfac.laundry.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/7 0007.
 */

public class ShopOrderListBean {
    public List<StoreOrder> obj = new ArrayList<>();
    public List<List<ShopOrderBean.ShopOrder>> obj1 = new ArrayList<>();
    public String responseStatus;
    public String msg;

    public class StoreOrder {
        public long so_id;
        public int s_id;
        public int au_id;
        public String so_number;
        public String nickname;
        public String tel;
        public String s_name;
        public String s_mobile;
        public int so_total;
        public String order_time;
        public String payment_time;
        public String delivery_address;
        public String so_state;
        public String guest_book;
        public String so_distribution;
    }

    public class ShopOrder {
        public int soc_id;
        public int so_id;
        public int c_id;
        public int so_total;
        public String first_picture;
        public String c_name;
        public Double unit_price;
        public String c_introduce;
        public int quantity_purchased;

    }
}
