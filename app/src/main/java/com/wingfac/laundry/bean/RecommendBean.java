package com.wingfac.laundry.bean;


import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/17 0017.
 */

public class RecommendBean extends Base {
    public List<Recommend> obj = new ArrayList<>();

    public class Recommend {
        public int rc_id;
        public String rc_name;
        public boolean state = false;
    }
}
