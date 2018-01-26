package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/7 0007.
 */

public class StoreClassBean{
    public String obj;
    public String responseStatus;
    public String msg;
    public List<StoreClass> obj1 = new ArrayList<>();
    public class StoreClass {
        public int cc_id;
        public int s_id;
        public String cc_name;
        public String cc_picture;

    }
}
