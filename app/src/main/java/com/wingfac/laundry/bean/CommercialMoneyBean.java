package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/19 0019.
 */

public class CommercialMoneyBean extends Base {
    public List<CommercialOrder> data = new ArrayList<>();

    public static class CommercialOrder {
        public Double total;
        public String time;
    }
}
