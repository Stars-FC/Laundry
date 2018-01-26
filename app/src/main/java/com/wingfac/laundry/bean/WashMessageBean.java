package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/4 0004.
 */

public class WashMessageBean extends Base {
    public List<WashMessage> data = new ArrayList<>();

    public class WashMessage implements Serializable{
        public String address;
        public String pickup_password;
        public String goods_name;
        public String guest_book;
        public String pickup_code;
        public String order_time;
    }
}
