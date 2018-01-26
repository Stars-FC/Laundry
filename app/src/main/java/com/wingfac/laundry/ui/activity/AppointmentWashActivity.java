package com.wingfac.laundry.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.AppointmentWashLeftBean;
import com.wingfac.laundry.bean.AppointmentWashRightBean;
import com.wingfac.laundry.bean.CloseBean;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.ui.adapter.AppointmentWashLeftAdapter;
import com.wingfac.laundry.ui.adapter.AppointmentWashTwoRightAdapter;
import com.yuyh.library.utils.toast.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class AppointmentWashActivity extends BaseActivity {
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.head_layout_right)
    Button right;
    @Bind(R.id.activity_appointment_wash_left_list)
    ListView leftListView;
    @Bind(R.id.activity_appointment_wash_right_list)
    ListView rightListView;
    @Bind(R.id.activity_appointment_wash_settlement)
    Button settlement;
    @Bind(R.id.activity_appointment_wash_all)
    TextView all;
    @Bind(R.id.activity_appointment_wash_num)
    TextView num;
    Double vipPrice = 0.0;
    AppointmentWashRightBean rightList = new AppointmentWashRightBean();
    AppointmentWashTwoRightAdapter rightAdapter;
    AppointmentWashLeftBean leftList = new AppointmentWashLeftBean();
    AppointmentWashLeftAdapter leftAdapter;
    public Map<Integer, CloseBean> rightData = new HashMap<>();
    private String aId, eId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_wash_two);
        ButterKnife.bind(this);
        aId = getIntent().getStringExtra("aId");
        eId = getIntent().getStringExtra("eId");
        initData();
    }

    void initData() {
        left.setOnClickListener(view -> finish());
        title.setText("预约洗衣");
        right.setVisibility(View.GONE);
        settlement.setOnClickListener(view -> {
            if (num.getText().toString().equals("0")) {
                ToastUtils.showToast("请选择商品");
                return;
            }
            String c_id = "";
            String price = "";
            int x = 0;
            int j = 0;
            int yy = 0;
            List<CloseBean> mapValuesList = new ArrayList(rightData.values());
            for (int y = 0; y < mapValuesList.size(); y++) {
                for (int i = 0; i < mapValuesList.get(y).obj.size(); i++) {
                    if (mapValuesList.get(y).obj.get(i).state) {
                        x++;
                        j = i;
                        yy = y;
                    }
                }
            }

            if (x == 1) {
                c_id = mapValuesList.get(yy).obj.get(j).g_id + "";
                price = mapValuesList.get(yy).obj.get(j).num + "";
            } else {
                for (int y = 0; y < mapValuesList.size(); y++) {
                    for (int i = 0; i < mapValuesList.get(y).obj.size(); i++) {
                        if (mapValuesList.get(y).obj.get(i).state) {
                            if (y == mapValuesList.size() - 1 && i == mapValuesList.get(y).obj.size() - 1) {
                                c_id = c_id + mapValuesList.get(y).obj.get(i).g_id;
                            } else {
                                c_id = c_id + mapValuesList.get(y).obj.get(i).g_id + ",";
                            }
                            if (y == mapValuesList.size() - 1 && i == mapValuesList.get(y).obj.size() - 1) {
                                price = price + mapValuesList.get(y).obj.get(i).num;
                            } else {
                                price = price + mapValuesList.get(y).obj.get(i).num + ",";
                            }
                        }
                    }
                }

            }
            Intent intent = new Intent(getActivity(), AppointmentWashConfirmOrderActivity.class);
            intent.putExtra("c_id", c_id);
            intent.putExtra("num", price);
            intent.putExtra("total", all.getText().toString());
            intent.putExtra("eId", eId);
            intent.putExtra("vip", String.valueOf(vipPrice));
            startActivity(intent);
        });
        leftAdapter = new AppointmentWashLeftAdapter(getActivity(), leftList);
        leftListView.setAdapter(leftAdapter);
        setLeftData();
        leftListView.setOnItemClickListener((adapterView, view, i, l) -> {
            getRight(leftList.data.get(i).cc_id);
            for (int j = 0; j < leftList.data.size(); j++) {
                if (j == i) {
                    leftList.data.get(j).state = true;
                } else {
                    leftList.data.get(j).state = false;
                }
            }
            leftAdapter.notifyDataSetChanged();
        });
        rightAdapter = new AppointmentWashTwoRightAdapter(getActivity(), rightList, rightData, "1", all, num, vipPrice);
        rightListView.setAdapter(rightAdapter);
    }

    void getRight(int cc_id) {
        rightList.data.clear();
        if (rightData.get(cc_id) != null && rightData.get(cc_id).obj.size() > 0) {
            for (int i = 0; i < rightData.get(cc_id).obj.size(); i++) {
                AppointmentWashRightBean.AppointmentWashRight appointmentWashRight = rightList.new AppointmentWashRight();
                appointmentWashRight.num = rightData.get(cc_id).obj.get(i).num;
                appointmentWashRight.price = String.valueOf(rightData.get(cc_id).obj.get(i).price);
                appointmentWashRight.name = rightData.get(cc_id).obj.get(i).goods_name;
                appointmentWashRight.img = rightData.get(cc_id).obj.get(i).pic;
                appointmentWashRight.c_id = rightData.get(cc_id).obj.get(i).g_id;
                appointmentWashRight.state = rightData.get(cc_id).obj.get(i).state;
                appointmentWashRight.vip_percent = rightData.get(cc_id).obj.get(i).vip_percent;
                rightList.data.add(appointmentWashRight);
            }
            rightAdapter.setCcId(cc_id);
            rightAdapter.notifyDataSetChanged();
        } else {
            APPApi.getInstance().service
                    .getClose(String.valueOf(cc_id), aId)
                    .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                    .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                    .subscribe(new Observer<CloseBean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(CloseBean value) {
                            if (value.responseStatus.equals("0")) {
                                for (int i = 0; i < value.obj.size(); i++) {
                                    AppointmentWashRightBean.AppointmentWashRight appointmentWashRight = rightList.new AppointmentWashRight();
                                    appointmentWashRight.num = 0;
                                    appointmentWashRight.price = String.valueOf(value.obj.get(i).price);
                                    appointmentWashRight.name = value.obj.get(i).goods_name;
                                    appointmentWashRight.img = value.obj.get(i).pic;
                                    appointmentWashRight.c_id = value.obj.get(i).g_id;
                                    appointmentWashRight.vip_percent = value.obj.get(i).vip_percent;
                                    rightList.data.add(appointmentWashRight);
                                }
                                for (int i = 0; i < value.obj.size(); i++) {
                                    value.obj.get(i).num = 0;
                                    value.obj.get(i).state = false;
                                }
                                rightData.put(cc_id, value);
                                rightAdapter.setCcId(cc_id);
                                rightAdapter.notifyDataSetChanged();
                            } else {
                                rightAdapter.setCcId(cc_id);
                                rightAdapter.notifyDataSetChanged();
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

    }

    void setLeftData() {
        AppointmentWashLeftBean.AppointmentWashLeft appointmentWashLeft = leftList.new AppointmentWashLeft();
        appointmentWashLeft.img = "1";
        appointmentWashLeft.name = "上衣";
        appointmentWashLeft.state = false;
        appointmentWashLeft.cc_id = 1;
        leftList.data.add(appointmentWashLeft);

        AppointmentWashLeftBean.AppointmentWashLeft appointmentWashLeft1 = leftList.new AppointmentWashLeft();
        appointmentWashLeft1.img = "2";
        appointmentWashLeft1.name = "大衣外套";
        appointmentWashLeft1.state = false;
        appointmentWashLeft1.cc_id = 2;
        leftList.data.add(appointmentWashLeft1);

        AppointmentWashLeftBean.AppointmentWashLeft appointmentWashLeft2 = leftList.new AppointmentWashLeft();
        appointmentWashLeft2.img = "3";
        appointmentWashLeft2.name = "裤装裙装";
        appointmentWashLeft2.state = false;
        appointmentWashLeft2.cc_id = 3;
        leftList.data.add(appointmentWashLeft2);

        AppointmentWashLeftBean.AppointmentWashLeft appointmentWashLeft3 = leftList.new AppointmentWashLeft();
        appointmentWashLeft3.img = "4";
        appointmentWashLeft3.name = "鞋子";
        appointmentWashLeft3.state = false;
        appointmentWashLeft3.cc_id = 4;
        leftList.data.add(appointmentWashLeft3);

        AppointmentWashLeftBean.AppointmentWashLeft appointmentWashLeft4 = leftList.new AppointmentWashLeft();
        appointmentWashLeft4.img = "5";
        appointmentWashLeft4.name = "靴子";
        appointmentWashLeft4.state = false;
        appointmentWashLeft4.cc_id = 5;
        leftList.data.add(appointmentWashLeft4);

        AppointmentWashLeftBean.AppointmentWashLeft appointmentWashLeft5 = leftList.new AppointmentWashLeft();
        appointmentWashLeft5.img = "6";
        appointmentWashLeft5.name = "家纺";
        appointmentWashLeft5.state = false;
        appointmentWashLeft5.cc_id = 6;
        leftList.data.add(appointmentWashLeft5);

        AppointmentWashLeftBean.AppointmentWashLeft appointmentWashLeft6 = leftList.new AppointmentWashLeft();
        appointmentWashLeft6.img = "7";
        appointmentWashLeft6.name = "配件";
        appointmentWashLeft6.state = false;
        appointmentWashLeft6.cc_id = 7;
        leftList.data.add(appointmentWashLeft6);

        leftList.data.get(0).state = true;
        leftAdapter.notifyDataSetChanged();
        getRight(leftList.data.get(0).cc_id);
    }
}
