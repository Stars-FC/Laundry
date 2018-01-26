package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/16 0016.
 */

public class TwoClassBean extends Base {
    public List<TwoClass> obj = new ArrayList<>();

    public class TwoClass {
        public int tlm_id;
        public String tlm_picture_path;
        public String tlm_name;
    }
}
