package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/7 0007.
 */

public class MessageBean  {
    public String responseStatus;
    public String msg;
    public List<Message> obj = new ArrayList<>();
    public Integer obj1;
    public class Message {
        public String content;
        public String time;
        public String um_id;
    }
}
