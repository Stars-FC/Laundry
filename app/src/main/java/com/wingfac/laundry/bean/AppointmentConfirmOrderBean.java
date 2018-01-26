package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/3 0003.
 */

public class AppointmentConfirmOrderBean {
    public Appointment obj;
    public List<AppointmentConfirmOrder> obj1 = new ArrayList<>();
    public String responseStatus;
    public String msg;
    public class AppointmentConfirmOrder {
        public String goods_name;
        public int log_num;
        public Double price;
    }
    public class Appointment{
        public String lo_number;
        public long lo_id;
    }
}
