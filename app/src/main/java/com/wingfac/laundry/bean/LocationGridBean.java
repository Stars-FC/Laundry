package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class LocationGridBean extends Base {
    public List<LocationGrid> data = new ArrayList<>();

    public class LocationGrid {
        public String name;
        public Boolean state;
        public int tlm_id;
    }
}
