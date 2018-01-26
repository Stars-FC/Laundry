package com.wingfac.laundry.ui.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.AppointmentConfirmOrderBean;
import com.wingfac.laundry.bean.PayBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.WashDetailBean;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.ui.adapter.AppointmentWashConfirmOrderAdapter;
import com.wingfac.laundry.utiil.PayResult;
import com.wingfac.laundry.weight.LoadingDialog;
import com.yuyh.library.utils.toast.ToastUtils;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/3 0003.
 */

public class AppointmentWashConfirmOrderActivity extends BaseActivity {
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.head_layout_right)
    Button right;
    @Bind(R.id.activity_appointment_wash_confirm_order_list)
    ListView listView;
    @Bind(R.id.activity_appointment_wash_confirm_order_scroll)
    ScrollView scrollView;
    @Bind(R.id.activity_appointment_wash_confirm_order_pay)
    Button pay;
    @Bind(R.id.activity_appointment_wash_confirm_order_number)
    TextView number;
    @Bind(R.id.activity_appointment_wash_confirm_order_all)
    TextView all;
    @Bind(R.id.content)
    LinearLayout contentLayout;
    @Bind(R.id.activity_appointment_wash_confirm_order_send)
    Button send;
    @Bind(R.id.activity_appointment_wash_confirm_order_content)
    EditText content;
    AppointmentConfirmOrderBean list = new AppointmentConfirmOrderBean();
    AppointmentWashConfirmOrderAdapter adapter;
    PopupWindow payWindow, passwordWindow;
    int state = 0;
    String num, cId, price, eId, loId, vipPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_wash_confirm_order);
        ButterKnife.bind(this);
        num = getIntent().getStringExtra("num");
        cId = getIntent().getStringExtra("c_id");
        price = getIntent().getStringExtra("total");
        eId = getIntent().getStringExtra("eId");
        loId = getIntent().getStringExtra("loId");
        vipPrice = getIntent().getStringExtra("vip");
        initData();
        scrollView.post(() -> scrollView.smoothScrollTo(0, 0));
    }

    public void setHeight(ListView listview) {
        int height = 0;
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View temp = adapter.getView(i, null, listview);
            temp.measure(0, 0);
            height += temp.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listview.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.FILL_PARENT;
        params.height = height;
        listview.setLayoutParams(params);
    }

    void initData() {
        left.setOnClickListener(view -> finish());
        title.setText("确认订单");
        right.setVisibility(View.GONE);
        adapter = new AppointmentWashConfirmOrderAdapter(getActivity(), list);
        listView.setAdapter(adapter);
        if (loId != null) {
            numberStr = loId;
            getForOid();
        } else {
            LoadingDialog.showRoundProcessDialog(getActivity());
            setData();
        }

    }

    void getForOid() {
        APPApi.getInstance().service
                .getWashOrderDetail(loId)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<WashDetailBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(WashDetailBean value) {
                        if (value.responseStatus.equals("0")) {
                            for (int i = 0; i < value.obj1.size(); i++) {
                                AppointmentConfirmOrderBean.AppointmentConfirmOrder appointmentConfirmOrder = list.new AppointmentConfirmOrder();
                                appointmentConfirmOrder.log_num = value.obj1.get(i).log_num;
                                appointmentConfirmOrder.price = value.obj1.get(i).price;
                                appointmentConfirmOrder.goods_name = value.obj1.get(i).goods_name;
                                list.obj1.add(appointmentConfirmOrder);
                            }
                            number.setText(value.obj.lo_number);
                            all.setText(price);
                            adapter.notifyDataSetChanged();
                            setHeight(listView);
                            pay.setOnClickListener(view -> {
                                if (payWindow != null)
                                    payWindow = null;
                                showPopWindow(contentLayout, R.layout.windows_pay_way);
                            });
                        } else {
                            Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    String numberStr;

    void setData() {
        APPApi.getInstance().service
                .getWashOrder(UserBean.user.id, cId, num, eId, price)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<AppointmentConfirmOrderBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(AppointmentConfirmOrderBean value) {
                        LoadingDialog.mDialog.dismiss();
                        if (value.responseStatus.equals("0")) {
                            list.obj1.addAll(value.obj1);
                            numberStr = String.valueOf(value.obj.lo_id);
                            number.setText(value.obj.lo_number);
                            all.setText(price + "（会员价" + vipPrice + "）");
                            adapter.notifyDataSetChanged();
                            setHeight(listView);
                            pay.setOnClickListener(view -> {
                                if (payWindow != null)
                                    payWindow = null;
                                showPopWindow(contentLayout, R.layout.windows_pay_way);
                            });
                        } else {
                            Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialog.mDialog.dismiss();
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void showPopWindow(View parentView, int convertViewResource) {
        //创建一个popUpWindow
        View popLayout = LayoutInflater.from(getActivity()).inflate(convertViewResource, null);
        TextView ali = popLayout.findViewById(R.id.windows_pay_way_ali);
        TextView wei = popLayout.findViewById(R.id.windows_pay_way_wei);
        TextView vip = popLayout.findViewById(R.id.windows_pay_way_vip);
        if (Double.parseDouble(UserBean.user.balance) > 0) {
            vip.setText("会员卡支付(尊享价" + vipPrice + ")");
        }
        state = 0;
        ali.setOnClickListener(v -> {
            wei.setTextColor(getResources().getColor(R.color.default_text));
            vip.setTextColor(getResources().getColor(R.color.default_text));
            ali.setTextColor(getResources().getColor(R.color.them));
            state = 1;

        });
        wei.setOnClickListener(view -> {
            ali.setTextColor(getResources().getColor(R.color.default_text));
            vip.setTextColor(getResources().getColor(R.color.default_text));
            wei.setTextColor(getResources().getColor(R.color.them));
            state = 2;
        });
        vip.setOnClickListener(v -> {
            wei.setTextColor(getResources().getColor(R.color.default_text));
            ali.setTextColor(getResources().getColor(R.color.default_text));
            vip.setTextColor(getResources().getColor(R.color.them));
            state = 3;
        });
        popLayout.findViewById(R.id.windows_pay_way_confirm).setOnClickListener(view -> {
            if (state == 0) {
                ToastUtils.showToast("请选择支付方式");
                return;
            }
            if (payWindow.isShowing()) {
                payWindow.dismiss();
                switch (state) {
                    case 1:
                        LoadingDialog.showRoundProcessDialog(getActivity());
                        pay("1");
                        break;
                    case 2:
                        LoadingDialog.showRoundProcessDialog(getActivity());
                        pay("2");
                        break;
                    case 3:
                        showPasswordWindow(contentLayout, R.layout.windows_pay_password);
                        break;
                }
            }
        });
        if (payWindow == null) {
            //实例化一个popupWindow
            payWindow =
                    new PopupWindow(popLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //产生背景变暗效果
            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.alpha = 0.4f;
            getActivity().getWindow().setAttributes(lp);
            //点击外面popupWindow消失
            payWindow.setOutsideTouchable(true);
            //popupWindow获取焦点
            payWindow.setFocusable(true);
            //popupWindow设置背景图
            payWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            //popupWindow设置开场动画风格
            //popupWindow.setAnimationStyle(R.style.popupWindow_anim);
            //刷新popupWindow
            //popupWindow.update();

            //设置popupWindow消失时的监听
            payWindow.setOnDismissListener(() -> {
                WindowManager.LayoutParams lp1 = getActivity().getWindow().getAttributes();
                lp1.alpha = 1f;
                getActivity().getWindow().setAttributes(lp1);
            });
            payWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        } else {
            //如果popupWindow正在显示，接下来隐藏
            if (payWindow.isShowing()) {
                payWindow.dismiss();
            } else {
                //产生背景变暗效果
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 0.4f;
                getActivity().getWindow().setAttributes(lp);
                payWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
            }
        }
    }

    private void showPasswordWindow(View parentView, int convertViewResource) {
        //创建一个popUpWindow
        View popLayout = LayoutInflater.from(getActivity()).inflate(convertViewResource, null);
        EditText password = popLayout.findViewById(R.id.windows_pay_password);
        popLayout.findViewById(R.id.windows_pay_way_confirm).setOnClickListener(view -> {
            if (password.getText().toString().equals("")) {
                ToastUtils.showToast("请输入支付密码");
                return;
            }
            if (!password.getText().toString().equals(UserBean.user.payPassword)) {
                ToastUtils.showToast("密码错误");
                return;
            }
            if (passwordWindow.isShowing()) {
                passwordWindow.dismiss();
                LoadingDialog.showRoundProcessDialog(getActivity());
                pay("0");
            }
        });
        if (passwordWindow == null) {
            //实例化一个popupWindow
            passwordWindow =
                    new PopupWindow(popLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //产生背景变暗效果
            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.alpha = 0.4f;
            getActivity().getWindow().setAttributes(lp);
            //点击外面popupWindow消失
            passwordWindow.setOutsideTouchable(true);
            //popupWindow获取焦点
            passwordWindow.setFocusable(true);
            //popupWindow设置背景图
            passwordWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            //popupWindow设置开场动画风格
            //popupWindow.setAnimationStyle(R.style.popupWindow_anim);
            //刷新popupWindow
            //popupWindow.update();

            //设置popupWindow消失时的监听
            passwordWindow.setOnDismissListener(() -> {
                WindowManager.LayoutParams lp1 = getActivity().getWindow().getAttributes();
                lp1.alpha = 1f;
                getActivity().getWindow().setAttributes(lp1);
            });
            passwordWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        } else {
            //如果popupWindow正在显示，接下来隐藏
            if (passwordWindow.isShowing()) {
                passwordWindow.dismiss();
            } else {
                //产生背景变暗效果
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 0.4f;
                getActivity().getWindow().setAttributes(lp);
                passwordWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
            }
        }
    }

    private static final int SDK_PAY_FLAG = 1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(getActivity(), "支付成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), WashOrderActivity.class);
                        intent.putExtra("loId", String.valueOf(numberStr));
                        startActivity(intent);
                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(getActivity(), "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };
    private MyReceiver receiver;

    void pay(String typeStr) {
        APPApi.getInstance().service
                .payWash(numberStr, typeStr, "1", content.getText().toString(), "1")
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<PayBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(PayBean value) {
                        LoadingDialog.mDialog.dismiss();
                        if (value.responseStatus.equals("0")) {
                            if (typeStr.equals("0")) {
                                Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), WashOrderActivity.class);
                                intent.putExtra("loId", String.valueOf(numberStr));
                                startActivity(intent);
                                finish();
                            } else if (typeStr.equals("1")) {
                                Runnable payRunnable = () -> {
                                    PayTask alipay = new PayTask(getActivity());
                                    Map<String, String> result = alipay.payV2(value.data.alSign, true);
                                    Log.i("msp", result.toString());
                                    Message msg = new Message();
                                    msg.what = SDK_PAY_FLAG;
                                    msg.obj = result;
                                    mHandler.sendMessage(msg);
                                };

                                Thread payThread = new Thread(payRunnable);
                                payThread.start();
                            } else {
                                IWXAPI api = WXAPIFactory.createWXAPI(getActivity(), value.data.appid);
                                api.registerApp(value.data.appid);
                                PayReq payReq = new PayReq();
                                payReq.appId = value.data.appid;
                                payReq.partnerId = value.data.partnerid;
                                payReq.prepayId = value.data.prepayid;
                                payReq.packageValue = value.data.packageValue;
                                payReq.nonceStr = value.data.noncestr;
                                payReq.timeStamp = value.data.timestamp;
                                payReq.sign = value.data.sign;
                                api.sendReq(payReq);
                                receiver = new MyReceiver();
                                registerReceiver(receiver, new IntentFilter("com.itheima.pay.change"));
                            }
                            login();
                        } else {
                            Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialog.mDialog.dismiss();
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intent1 = new Intent(getActivity(), WashOrderActivity.class);
            intent1.putExtra("loId", numberStr);
            startActivity(intent1);
            finish();
            context.unregisterReceiver(this);
        }
    }

    void login() {
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
                            ToastUtils.showToast(value.msg);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showToast("请检查您的网络设置");
                        finish();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
