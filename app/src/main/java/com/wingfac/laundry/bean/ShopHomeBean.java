package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/16 0016.
 */

public class ShopHomeBean{
    public CommodityBean.Commodity obj;
    public String responseStatus;
    public String msg;
    public List<Left> obj1 = new ArrayList<>();

    public class Left {
        public int cc_id;
        public String cc_name;
        public String cc_picture;
    }
}
