package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

/**
 * Created by Administrator on 2017/6/2 0002.
 */

public class UserBean {
    public String responseStatus;
    public String msg;
    public static User user;
    public static UserStore userStore;
    public User obj;
    public UserStore obj1;
    public class UserStore{
        public int s_id;
        public int id;
        public int tlm_id;
        public String first_picture;
        public String picture;
        public String s_logo;
        public String s_name;
        public String open_time;
        public String s_mobile;
        public String s_address;
        public String describe;
        public Double s_longitude;
        public Double s_latitude;
        public int s_praise;
        public int s_popularity;
        public int rc_id;
        public String s_distribution;

    }
    public class User {
        public String id;
        public String username;
        public String password;
        public String headPortrait;
        public String nickname;
        public String tel;
        public String cardNumber;
        public String balance;
        public String grade;
        public String payPassword;
        public String registerId;
        public int type;
        public String createTime;
    }
}
