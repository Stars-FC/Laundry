package com.wingfac.laundry.ui.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wingfac.laundry.app.MyApplication;
import com.wingfac.laundry.ui.activity.MainActivity;
import com.zhy.autolayout.AutoLayoutActivity;

import io.reactivex.disposables.CompositeDisposable;


/**
 * 当前项目所有的Activity的父类 作用:把共性的属性抽取 统一规范
 *
 * @author Snow
 */
public abstract class BaseActivity extends AutoLayoutActivity {

    private static final String TAG = "Log";
    public CompositeDisposable compositeDisposable;
    private MyApplication application;
    private BaseActivity oContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MyApplication.flag == -1) {//flag为-1说明程序被杀掉
            protectApp();
        }
        if (application == null) {
            // 得到Application对象
            application = (MyApplication) getApplication();
        }
        oContext = this;// 把当前的上下文对象赋值给BaseActivity
        addActivity();// 调用添加方法
        compositeDisposable = new CompositeDisposable();
    }

//    protected abstract void initData();

    protected void protectApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//清空栈里MainActivity之上的所有activty
        startActivity(intent);
        finish();
    }

    // 添加Activity方法
    public void addActivity() {
        application.addActivity_(oContext);// 调用myApplication的添加Activity方法
    }

    //销毁当个Activity方法
    public void removeActivity() {
        application.removeActivity_(oContext);// 调用myApplication的销毁单个Activity方法
    }

    //销毁所有Activity方法
    public void removeALLActivity() {
        application.removeALLActivity_();// 调用myApplication的销毁所有Activity方法
    }

    /**
     * 根据id获取一个TextView
     *
     * @param id ：控件的id
     * @return TextView对象
     */
    public TextView tvById(int id) {
        return (TextView) findViewById(id);
    }

    /**
     * 根据id获取一个EditText
     *
     * @param id ：控件的id
     * @return EditText对象
     */
    public EditText editById(int id) {
        return (EditText) findViewById(id);
    }

    /**
     * 根据id获取一个Button
     *
     * @param id ：控件的id
     * @return Button对象
     */
    public Button butById(int id) {
        return (Button) findViewById(id);
    }

    /**
     * 根据id获取一个ImageView
     *
     * @param id ：控件的id
     * @return ImageView对象
     */
    public ImageView imgById(int id) {
        return (ImageView) findViewById(id);
    }

    /**
     * 根据id获取一个LinearLayout
     *
     * @param id ：控件的id
     * @return LinearLayout对象
     */
    public LinearLayout linearById(int id) {
        return (LinearLayout) findViewById(id);
    }

    /**
     * 短弹窗提示
     *
     * @param text
     */
    public void toastS(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长弹窗提示
     *
     * @param text
     */
    public void toastL(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    /**
     * @param text Log测试
     */
    public void logI(String text) {
        Log.i(TAG, text);
    }

    /**
     * @return 拿到上下文
     */
    public BaseActivity getActivity() {
        return this;
    }

    /**
     * @return 拿到文本框内容
     */
    public String getText(EditText et) {
        return et.getText().toString().trim();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
