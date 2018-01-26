package com.wingfac.laundry.bean;

import com.google.gson.annotations.SerializedName;
import com.wingfac.laundry.bean.base.Base;

/**
 * Created by Administrator on 2017/10/27 0027.
 */

public class PayBean extends Base {
    public Pay data;

    public class Pay {
        public String alSign;
        public String sign;
        public String timestamp;
        public String noncestr;
        public String partnerid;
        public String prepayid;
        @SerializedName("package")
        public String packageValue;
        public String appid;
    }
}
