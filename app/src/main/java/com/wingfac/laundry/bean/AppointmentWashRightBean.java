package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/3 0003.
 */

public class AppointmentWashRightBean extends Base {
    public List<AppointmentWashRight> data = new ArrayList<>();

    public class AppointmentWashRight {
        public String img;
        public String name;
        public int num = 0;
        public String price;
        public Double vip_percent;
        public int c_id;
        public String good;
        public String person;
        public Boolean state = false;
    }
}
