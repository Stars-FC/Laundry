package com.wingfac.laundry.bean;


import com.wingfac.laundry.bean.base.Base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public class OrderBean extends Base {
    public List<Order> data = new ArrayList<>();

    public class Order implements Serializable {
        public String img;
        public String time;
        public String price;
        public String name;
        public String num;
        public String state;
        public long id;
        public String type;
    }
}
