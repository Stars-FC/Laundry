package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/2 0002.
 */

public class ShoppingLogisticsBean extends Base {
    public List<Logistics> obj = new ArrayList<>();

    public class Logistics {
        public String lc_content;
        public String lc_time;
    }
}
