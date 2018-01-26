package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/3 0003.
 */

public class WashLogisticsBean extends Base {
    public List<WashLogistics> data = new ArrayList<>();

    public class WashLogistics {
        public String info;
        public String time;
    }
}
