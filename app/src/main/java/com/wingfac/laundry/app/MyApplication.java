package com.wingfac.laundry.app;

import android.app.Activity;
import android.app.Application;
import android.os.Environment;

import com.wingfac.laundry.utiil.ACache;
import com.yuyh.library.AppUtils;
import com.zhy.autolayout.config.AutoLayoutConifg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/5/9 0009.
 */

public class MyApplication extends Application {
    // 默认存放图片的路径
    public final static String DEFAULT_SAVE_IMAGE_PATH = Environment.getExternalStorageDirectory() + File.separator + "Wallet" + File.separator + "Images"
            + File.separator;
    public static int flag = -1;
    private static MyApplication instance;
    private static ACache mCache;//缓存管理对象
    private List<Activity> oList;

    /*
   获得缓存类
   */
    public static ACache getAcache() {
        return mCache;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //获取设备尺寸
        AutoLayoutConifg.getInstance().useDeviceSize().init(this);
        oList = new ArrayList<Activity>();
        //配置缓存
        mCache = ACache.get(this);
        AppUtils.init(instance);
    }

    /**
     * 添加Activity
     */
    public void addActivity_(Activity activity) {
        // 判断当前集合中不存在该Activity
        if (!oList.contains(activity)) {
            oList.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 添加Activity
     */
    public Activity getActivity() {
        return oList.get(0);
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity_(Activity activity) {
        //判断当前集合中存在该Activity
        if (oList.contains(activity)) {
            oList.remove(activity);//从集合中移除
            activity.finish();//销毁当前Activity
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity_() {
        //通过循环，把集合中的所有Activity销毁
        for (Activity activity : oList) {
            activity.finish();
        }
    }
}
