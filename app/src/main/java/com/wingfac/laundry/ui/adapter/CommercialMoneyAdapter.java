package com.wingfac.laundry.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.CommercialMoneyBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.weight.LoadingDialog;
import com.yuyh.library.utils.toast.ToastUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class CommercialMoneyAdapter extends BaseAdapter {
    private static final String TAG = "CommercialOrderAdapter";
    public Activity context;
    public CommercialMoneyBean list;
    RelativeLayout contentLayout;
    public CommercialMoneyAdapter(Activity context, CommercialMoneyBean list, RelativeLayout contentLayout) {
        this.context = context;
        this.list = list;
        this.contentLayout = contentLayout;
    }

    @Override
    public int getCount() {
        return list.data.size();
    }

    @Override
    public Object getItem(int i) {
        return list.data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Holder viewHolder = null;
        if (view == null) {
            viewHolder = new Holder();
            view = LayoutInflater.from(context).inflate(
                    R.layout.item_commercial_fragment_order, null);
            viewHolder.one = (TextView) view.findViewById(R.id.item_commercial_fragment_order_one);
            viewHolder.two = (TextView) view.findViewById(R.id.item_commercial_fragment_order_two);
            viewHolder.three = (TextView) view.findViewById(R.id.item_commercial_fragment_order_three);
            viewHolder.four = (TextView) view.findViewById(R.id.item_commercial_fragment_order_four);
            viewHolder.fvie = (TextView) view.findViewById(R.id.item_commercial_fragment_order_five);
            viewHolder.button = (Button) view.findViewById(R.id.item_commercial_fragment_order_button);
            viewHolder.yue = view.findViewById(R.id.item_commercial_fragment_order_yue);
            viewHolder.ti = view.findViewById(R.id.item_commercial_fragment_order_ti);
            viewHolder.twoYue = view.findViewById(R.id.item_commercial_fragment_order_two_yue);
            view.setTag(viewHolder);
        } else {
            viewHolder = (Holder) view.getTag();
        }

        viewHolder.yue.setText(list.data.get(i).time.substring((list.data.get(i).time.indexOf("-")+1), list.data.get(i).time.length()) + "月销售额");
        viewHolder.twoYue.setText(list.data.get(i).time.substring(list.data.get(i).time.indexOf("-"), list.data.get(i).time.length()) + "月订单金额");
        viewHolder.one.setText(String.valueOf(list.data.get(i).total));
        viewHolder.two.setText(String.valueOf(list.data.get(i).total));
        viewHolder.fvie.setText(String.valueOf(UserBean.user.balance));
        viewHolder.button.setVisibility(View.VISIBLE);
        if (Double.parseDouble(UserBean.user.balance)>=1000) {
            viewHolder.button.setClickable(true);
            viewHolder.button.setBackgroundResource(R.drawable.consume_button);
            viewHolder.button.setText("申请提现");
            viewHolder.button.setOnClickListener(view1 -> {
                showTypeWindows(contentLayout, R.layout.windows_wallect_tixian);
            });
            viewHolder.ti.setVisibility(View.GONE);
        } else {
            viewHolder.button.setVisibility(View.GONE);
            viewHolder.ti.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public void setData(CommercialMoneyBean list) {
        this.list = list;
        notifyDataSetChanged();
    }
    PopupWindow typeWindow;
    private void showTypeWindows(View parentView, int convertViewResource) {
        //创建一个popUpWindow
        View popLayout = LayoutInflater.from(context).inflate(convertViewResource, null);
        TextView windows_name = popLayout.findViewById(R.id.windows_name);
        TextView two = popLayout.findViewById(R.id.windows_two);
        EditText three = popLayout.findViewById(R.id.windows_three);
        Button quXiao = popLayout.findViewById(R.id.windows_quxiao);
        Button queDing = popLayout.findViewById(R.id.windows_queding);
        EditText zhang = popLayout.findViewById(R.id.windows_three_zhang);
        windows_name.setText("提现余额");
        two.setText("请输入提现余额:");

        //给popUpWindow内的空间设置点击事件
        quXiao.findViewById(R.id.windows_quxiao).setOnClickListener(v -> {
            if (typeWindow.isShowing()) {
                typeWindow.dismiss();
            }
        });
        queDing.findViewById(R.id.windows_queding).setOnClickListener(view -> {
            if (typeWindow.isShowing()) {
                typeWindow.dismiss();
            }
            if (three.getText().equals("")) {
                ToastUtils.showToast("请输入金额");
                return;
            }
            if(Double.parseDouble(three.getText().toString())<1000){
                ToastUtils.showToast("最小提现金额为1000");
                return;
            }
            if(Double.parseDouble(three.getText().toString())>Double.parseDouble(UserBean.user.balance)){
                ToastUtils.showToast("可提现金额为"+Double.parseDouble(UserBean.user.balance));
                return;
            }
            if(zhang.getText().toString().equals("")){
                ToastUtils.showToast("请输入支付宝账号");
                return;
            }
            LoadingDialog.showRoundProcessDialog(context);
            getCommodity(three.getText().toString(),zhang.getText().toString());
        });
        if (typeWindow == null) {
            //实例化一个popupWindow
            typeWindow =
                    new PopupWindow(popLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //产生背景变暗效果
            WindowManager.LayoutParams lp = context.getWindow().getAttributes();
            lp.alpha = 0.4f;
            context.getWindow().setAttributes(lp);
            //点击外面popupWindow消失
            typeWindow.setOutsideTouchable(true);
            //popupWindow获取焦点
            typeWindow.setFocusable(true);
            //popupWindow设置背景图
            typeWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            //popupWindow设置开场动画风格
            //popupWindow.setAnimationStyle(R.style.popupWindow_anim);
            //刷新popupWindow
            //popupWindow.update();

            //设置popupWindow消失时的监听
            typeWindow.setOnDismissListener(() -> {
                WindowManager.LayoutParams lp1 = context.getWindow().getAttributes();
                lp1.alpha = 1f;
                context.getWindow().setAttributes(lp1);
            });
            typeWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        } else {
            //如果popupWindow正在显示，接下来隐藏
            if (typeWindow.isShowing()) {
                typeWindow.dismiss();
            } else {
                //产生背景变暗效果
                WindowManager.LayoutParams lp = context.getWindow().getAttributes();
                lp.alpha = 0.4f;
                context.getWindow().setAttributes(lp);
                typeWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
            }
        }
    }
    void getCommodity(String money,String zhanghao) {
        APPApi.getInstance().service
                .tiXian(UserBean.user.id,money,zhanghao,"0","ALIPAY_LOGONID")
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Base value) {
                        LoadingDialog.mDialog.dismiss();
                        if (value.responseStatus.equals("0")) {
                              getUser();
                        } else {
                            Toast.makeText(context, value.msg, Toast.LENGTH_SHORT).show();
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                        LoadingDialog.mDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void getUser() {
        APPApi.getInstance().service
                .getUser(UserBean.user.id)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<UserBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UserBean value) {
                        if (value.responseStatus.equals("0")) {
                            UserBean.user = value.obj;


                        } else {
                            Toast.makeText(context, value.msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public static class Holder {
        public TextView one;
        public TextView two;
        public TextView three;
        public TextView four;
        public TextView fvie;
        private Button button;
        private TextView yue;
        private TextView ti;
        private TextView twoYue;
    }
}
