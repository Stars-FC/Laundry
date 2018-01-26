package com.wingfac.laundry.bean;

import com.wingfac.laundry.bean.base.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/25 0025.
 */

public class CanCloseBean extends Base {
    public List<CanClose> obj = new ArrayList<>();

    public class CanClose {
        public int a_id;
    }
}
