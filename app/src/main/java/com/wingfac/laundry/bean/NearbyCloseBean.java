package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/7 0007.
 */

public class NearbyCloseBean extends Base {
    public List<NearbyClose> obj = new ArrayList<>();

    public class NearbyClose {
        public int a_id;
        public String img;
        public String deviceName;
        public String cabinet_total;
        public String total_used;
        public String unused_total;
        public String cabinet_adderss;
        public Double eLongitude;
        public Double eLatitude;
        public int e_id;
    }
}
