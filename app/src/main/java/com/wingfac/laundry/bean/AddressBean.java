package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/1 0001.
 */

public class AddressBean extends Base {
    public List<Address> obj = new ArrayList<>();

    public class Address implements Serializable{
        public String ua_name;//接收者姓名
        public String ua_mobile;//接收者电话
        public String ua_address;//地址信息
        public int ua_default;//默认地址
        public String ua_id;//地址ID
    }
}
