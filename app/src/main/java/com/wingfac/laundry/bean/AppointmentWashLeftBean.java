package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/3 0003.
 */

public class AppointmentWashLeftBean extends Base {
    public List<AppointmentWashLeft> data = new ArrayList<>();

    public class AppointmentWashLeft {
        public String img;
        public String name;
        public boolean state;
        public int cc_id;
    }
}
