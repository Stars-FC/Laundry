package com.wingfac.laundry.bean;


import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/16 0016.
 */

public class StoreBean extends Base {
    public List<Store> obj = new ArrayList<>();

    public class Store {
        public int s_id;
        public String s_logo;
        public String s_name;
        public String describe;
        public int s_praise;
        public int s_popularity;
        public String s_distribution;
        public double s_longitude;
        public double s_latitude;

    }
}
