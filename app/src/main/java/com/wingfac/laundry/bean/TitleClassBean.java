package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/15 0015.
 */

public class TitleClassBean extends Base {
    public List<TitleClass> obj = new ArrayList<>();

    public class TitleClass {
        public int lm_id;
        public String lm_picture_path;
        public String lm_name;
    }
}
