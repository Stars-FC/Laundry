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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.PayBean;
import com.wingfac.laundry.bean.ShopOrderBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.ui.adapter.OrderShopListAdapter;
import com.wingfac.laundry.utiil.PayResult;
import com.wingfac.laundry.weight.ListViewForScrollView;
import com.yuyh.library.utils.toast.ToastUtils;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/4 0004.
 */

public class ShopOrderActivity extends BaseActivity {
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.head_layout_right)
    Button right;
    @Bind(R.id.activity_shopping_details_goods_list)
    ListViewForScrollView listViewForScrollView;
    @Bind(R.id.activity_shop_order_mobile)
    TextView mobile;
    @Bind(R.id.activity_shop_order_order_number)
    TextView number;
    @Bind(R.id.activity_shop_order_start_time)
    TextView startTime;
    @Bind(R.id.activity_shop_order_address)
    TextView address;
    @Bind(R.id.activity_shop_order_address_layout)
    RelativeLayout addressLayout;
    @Bind(R.id.activity_shop_order_detail)
    TextView detail;
    @Bind(R.id.activity_shop_order_word)
    TextView word;
    @Bind(R.id.activity_shop_order_pay)
    Button pay;
    @Bind(R.id.content_layout)
    LinearLayout contentLayout;
    OrderShopListAdapter adapter;
    ShopOrderBean orderBean = new ShopOrderBean();
    public String c_id, s_id, num, price, so_id;
    PopupWindow payWindow, passwordWindow, infoWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_order);
        ButterKnife.bind(this);
        c_id = getIntent().getStringExtra("c_id");
        s_id = getIntent().getStringExtra("s_id");
        num = getIntent().getStringExtra("num");
        price = getIntent().getStringExtra("price");
        so_id = getIntent().getStringExtra("so_id");
        initData();
    }

    void initData() {
        left.setOnClickListener(view -> finish());
        title.setText("确认订单");
        right.setVisibility(View.GONE);
        if (so_id != null)
            getForOid();
        else
            getDate();
        adapter = new OrderShopListAdapter(getActivity(), orderBean);
        listViewForScrollView.setAdapter(adapter);
        pay.setOnClickListener(view -> {
            if (address.getText().toString().equals("") | address.getText().toString().equals("未设置")) {
                ToastUtils.showToast("请选择收货地址");
                return;
            }
            payWindow = null;
            showPopWindow(contentLayout, R.layout.windows_pay_way);
        });
    }

    void getForOid() {
        APPApi.getInstance().service
                .getOrderDetail(so_id)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<ShopOrderBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ShopOrderBean value) {
                        if (value.responseStatus.equals("0")) {
                            orderBean.obj = value.obj;
                            orderBean.obj1.addAll(value.obj2);
                            adapter.notifyDataSetChanged();
                            mobile.setText(value.obj.s_mobile);
                            number.setText(value.obj.so_number);
                            startTime.setText(value.obj.order_time);
                            address.setText(value.obj.delivery_address);
                            detail.setText("这个地方有问题");
                            addressLayout.setOnClickListener(view -> {
                                Intent intent = new Intent(getActivity(), MineAddressActivity.class);
                                intent.putExtra("state", "1");
                                startActivityForResult(intent, 1);
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

    void getDate() {
        APPApi.getInstance().service
                .getOneOrder(UserBean.user.id, s_id, c_id, num, price)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<ShopOrderBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ShopOrderBean value) {
                        if (value.responseStatus.equals("0")) {
                            orderBean.obj = value.obj;
                            orderBean.obj1.addAll(value.obj1);
                            adapter.notifyDataSetChanged();
                            mobile.setText(value.obj.s_mobile);
                            number.setText(value.obj.so_number);
                            startTime.setText(value.obj.order_time);
                            address.setText(value.obj.delivery_address);
                            detail.setText("这个地方有问题");
                            addressLayout.setOnClickListener(view -> {
                                Intent intent = new Intent(getActivity(), MineAddressActivity.class);
                                intent.putExtra("state", "1");
                                startActivityForResult(intent, 1);
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

    void setAddress(String uaId) {
        APPApi.getInstance().service
                .setAddress(String.valueOf(orderBean.obj.so_id), uaId)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Base value) {
                        if (value.responseStatus.equals("0")) {
                            address.setText(value.obj6);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            String uaId = data.getStringExtra("AddressId");
            setAddress(uaId);
        }
        if (resultCode == 2) {
            passwordWindow = null;
            showPasswordWindow(contentLayout, R.layout.windows_pay_password);
        }
    }

    int state = 0;

    private void showPopWindow(View parentView, int convertViewResource) {
        //创建一个popUpWindow
        View popLayout = LayoutInflater.from(getActivity()).inflate(convertViewResource, null);
        TextView ali = popLayout.findViewById(R.id.windows_pay_way_ali);
        TextView wei = popLayout.findViewById(R.id.windows_pay_way_wei);
        TextView vip = popLayout.findViewById(R.id.windows_pay_way_vip);
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
                Intent intent;
                switch (state) {
                    case 1:
                        pay("1");
                        break;
                    case 2:
                        pay("2");
                        break;
                    case 3:
                        passwordWindow = null;
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
                        Intent intent = new Intent(getActivity(),ShoppingOrderActivity.class);
                        intent.putExtra("so_id",String.valueOf(orderBean.obj.so_id));
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
                .payStore(String.valueOf(orderBean.obj.so_id), UserBean.user.id, typeStr,"1")
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<PayBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(PayBean value) {
                        if (value.responseStatus.equals("0")) {
                            if (typeStr.equals("0")) {
                                Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(),ShoppingOrderActivity.class);
                                intent.putExtra("so_id",String.valueOf(orderBean.obj.so_id));
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
            Intent intent1 = new Intent(getActivity(),ShoppingOrderActivity.class);
            intent1.putExtra("so_id",String.valueOf(orderBean.obj.so_id));
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

    private void showPasswordWindow(View parentView, int convertViewResource) {
        //创建一个popUpWindow
        View popLayout = LayoutInflater.from(getActivity()).inflate(convertViewResource, null);
        EditText password = popLayout.findViewById(R.id.windows_pay_password);
        popLayout.findViewById(R.id.windows_pay_way_confirm).setOnClickListener(view -> {
            if (UserBean.user.payPassword.equals("")) {
                ToastUtils.showToast("您还未设置支付密码");
                return;
            }
            if (password.getText().toString().equals("")) {
                ToastUtils.showToast("请输入支付密码");
                return;
            }
            if (!password.getText().toString().equals(UserBean.user.payPassword)) {
                ToastUtils.showToast("您输入的密码有误");
                return;
            }
            if (Double.parseDouble(price) > Double.parseDouble(UserBean.user.balance)) {
                if (passwordWindow.isShowing()) {
                    passwordWindow.dismiss();
                }
                showgoPopWindow(contentLayout, R.layout.windows_aaaaa);
                return;
            }
            if (passwordWindow.isShowing()) {
                passwordWindow.dismiss();
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

    private void showgoPopWindow(View parentView, int convertViewResource) {
        //创建一个popUpWindow
        View popLayout = LayoutInflater.from(getActivity()).inflate(convertViewResource, null);
        popLayout.findViewById(R.id.windows_pay_way_confirm).setOnClickListener(view -> {
            if (infoWindow.isShowing()) {
                infoWindow.dismiss();
                Intent intent = new Intent(getActivity(), MineRechargeNextActivity.class);
                startActivity(intent);
            }
        });
        if (infoWindow == null) {
            //实例化一个popupWindow
            infoWindow =
                    new PopupWindow(popLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //产生背景变暗效果
            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.alpha = 0.4f;
            getActivity().getWindow().setAttributes(lp);
            //点击外面popupWindow消失
            infoWindow.setOutsideTouchable(true);
            //popupWindow获取焦点
            infoWindow.setFocusable(true);
            //popupWindow设置背景图
            infoWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            //popupWindow设置开场动画风格
            //popupWindow.setAnimationStyle(R.style.popupWindow_anim);
            //刷新popupWindow
            //popupWindow.update();

            //设置popupWindow消失时的监听
            infoWindow.setOnDismissListener(() -> {
                WindowManager.LayoutParams lp1 = getActivity().getWindow().getAttributes();
                lp1.alpha = 1f;
                getActivity().getWindow().setAttributes(lp1);
            });
            infoWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        } else {
            //如果popupWindow正在显示，接下来隐藏
            if (infoWindow.isShowing()) {
                infoWindow.dismiss();
            } else {
                //产生背景变暗效果
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 0.4f;
                getActivity().getWindow().setAttributes(lp);
                infoWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
            }
        }
    }
}
