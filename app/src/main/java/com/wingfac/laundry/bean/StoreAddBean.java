package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/6 0006.
 */

public class StoreAddBean extends Base {
    public List<StoreAdd> obj1 = new ArrayList<>();

    public class StoreAdd {
        public String cc_name;
        public String cc_id;
        public String cc_picture;
    }
}
