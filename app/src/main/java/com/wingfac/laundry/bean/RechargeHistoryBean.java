package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class RechargeHistoryBean extends Base {
    public List<RechargeHistory> obj = new ArrayList<>();

    public class RechargeHistory {
        public Double balance;
        public Long createTime;
        public String boId;
    }
}
