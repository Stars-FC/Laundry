package com.wingfac.laundry.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.AppointmentWashLeftBean;
import com.wingfac.laundry.bean.AppointmentWashRightBean;
import com.wingfac.laundry.bean.CommodityBean;
import com.wingfac.laundry.bean.ShopHomeBean;
import com.wingfac.laundry.bean.base.Constant;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.ui.adapter.AppointmentWashLeftAdapter;
import com.wingfac.laundry.ui.adapter.AppointmentWashRightAdapter;
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

public class ShopHomeActivity extends BaseActivity implements View.OnClickListener{
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
    @Bind(R.id.activity_appointment_wash_img)
    ImageView img;
    @Bind(R.id.activity_appointment_wash_all)
    TextView all;
    @Bind(R.id.activity_appointment_wash_num)
    TextView num;
    AppointmentWashRightBean rightList = new AppointmentWashRightBean();
    AppointmentWashRightAdapter rightAdapter;
    AppointmentWashLeftBean leftList = new AppointmentWashLeftBean();
    AppointmentWashLeftAdapter leftAdapter;
    public Map<Integer, CommodityBean> rightData = new HashMap<>();
    private int s_id = -1;

    @Bind(R.id.main_left_two_level_title_line)
    View mainLeftTwoLevelTitleLine;
    @Bind(R.id.main_left_two_level_title)
    Button mainLeftTwoLevelTitle;
    @Bind(R.id.main_right_two_level_title_line)
    View mainRightTwoLevelTitleLine;
    @Bind(R.id.main_right_two_level_title)
    Button mainRightTwoLevelTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_wash);
        ButterKnife.bind(this);
        s_id = getIntent().getIntExtra("s_id", -1);
        initData();
        bindEvent();
    }

    void initData() {
        left.setOnClickListener(view -> finish());
        title.setText("店铺首页");
        right.setText("店铺详情");
        right.setVisibility(View.VISIBLE);
        right.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), StoreDetailActivity.class);
            intent.putExtra("s_id", s_id);
            startActivity(intent);
        });
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
            List<CommodityBean> mapValuesList = new ArrayList(rightData.values());
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
                c_id = mapValuesList.get(yy).obj.get(j).c_id + "";
                price = mapValuesList.get(yy).obj.get(j).num + "";
            } else {
                for (int y = 0; y < mapValuesList.size(); y++) {
                    for (int i = 0; i < mapValuesList.get(y).obj.size(); i++) {
                        if (mapValuesList.get(y).obj.get(i).state) {
                            if (y == mapValuesList.size() - 1 && i == mapValuesList.get(y).obj.size() - 1) {
                                c_id = c_id + mapValuesList.get(y).obj.get(i).c_id;
                            } else {
                                c_id = c_id + mapValuesList.get(y).obj.get(i).c_id + ",";
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

            Intent intent = new Intent(getActivity(), ShopOrderActivity.class);
            intent.putExtra("s_id", String.valueOf(s_id));
            intent.putExtra("c_id", c_id);
            intent.putExtra("num", price);
            intent.putExtra("price", all.getText().toString());
            startActivity(intent);
        });
        leftAdapter = new AppointmentWashLeftAdapter(getActivity(), leftList);
        leftListView.setAdapter(leftAdapter);
        getDate();
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
        rightAdapter = new AppointmentWashRightAdapter(getActivity(), rightList, rightData, "2", all, num);
        rightListView.setAdapter(rightAdapter);
        rightListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getActivity(), CommodityDetailsActivity.class);
            intent.putExtra("c_id", String.valueOf(rightList.data.get(i).c_id));
            intent.putExtra("s_id", String.valueOf(s_id));
            startActivity(intent);
        });


        mainLeftTwoLevelTitle.setText("" + getResources().getString(R.string.group_buy));
        mainRightTwoLevelTitle.setText("" + getResources().getString(R.string.takeaway));
        mainLeftTwoLevelTitle.setOnClickListener(this);
        mainRightTwoLevelTitle.setOnClickListener(this);


    }

    void getDate() {
        APPApi.getInstance().service
                .getShopHome(String.valueOf(s_id))
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<ShopHomeBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ShopHomeBean value) {
                        if (value.responseStatus.equals("0")) {
                            Glide.with(getActivity())
                                    .load(Constant.BASE_IMG + value.obj.picture)
                                    .dontAnimate()
                                    .placeholder(R.drawable.erro_store)
                                    .into(img);
                            if (value.obj1.size() > 0) {
                                for (int i = 0; i < value.obj1.size(); i++) {
                                    AppointmentWashLeftBean.AppointmentWashLeft appointmentWashLeft = leftList.new AppointmentWashLeft();
                                    appointmentWashLeft.img = value.obj1.get(i).cc_picture;
                                    appointmentWashLeft.name = value.obj1.get(i).cc_name;
                                    appointmentWashLeft.state = false;
                                    appointmentWashLeft.cc_id = value.obj1.get(i).cc_id;
                                    leftList.data.add(appointmentWashLeft);
                                }
                                leftList.data.get(0).state = true;
                                leftAdapter.notifyDataSetChanged();
                                getRight(value.obj1.get(0).cc_id);
                            }

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

    void getRight(int cc_id) {
        rightList.data.clear();
        if (rightData.get(cc_id) != null && rightData.get(cc_id).obj.size() > 0) {
            for (int i = 0; i < rightData.get(cc_id).obj.size(); i++) {
                AppointmentWashRightBean.AppointmentWashRight appointmentWashRight = rightList.new AppointmentWashRight();
                appointmentWashRight.num = rightData.get(cc_id).obj.get(i).num;
                appointmentWashRight.price = String.valueOf(rightData.get(cc_id).obj.get(i).unit_price);
                appointmentWashRight.name = rightData.get(cc_id).obj.get(i).c_name;
                appointmentWashRight.img = rightData.get(cc_id).obj.get(i).first_picture;
                appointmentWashRight.c_id = rightData.get(cc_id).obj.get(i).c_id;
                appointmentWashRight.good = String.valueOf(rightData.get(cc_id).obj.get(i).s_praise);
                appointmentWashRight.person = String.valueOf(rightData.get(cc_id).obj.get(i).s_popularity);
                appointmentWashRight.state = rightData.get(cc_id).obj.get(i).state;
                rightList.data.add(appointmentWashRight);
            }
            rightAdapter.setCcId(cc_id);
            rightAdapter.notifyDataSetChanged();
        } else {
            APPApi.getInstance().service
                    .getCommdityList(String.valueOf(s_id), String.valueOf(cc_id))
                    .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                    .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                    .subscribe(new Observer<CommodityBean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(CommodityBean value) {
                            if (value.responseStatus.equals("0")) {
                                for (int i = 0; i < value.obj.size(); i++) {
                                    AppointmentWashRightBean.AppointmentWashRight appointmentWashRight = rightList.new AppointmentWashRight();
                                    appointmentWashRight.num = 0;
                                    appointmentWashRight.price = String.valueOf(value.obj.get(i).unit_price);
                                    appointmentWashRight.name = value.obj.get(i).c_name;
                                    appointmentWashRight.img = value.obj.get(i).first_picture;
                                    appointmentWashRight.c_id = value.obj.get(i).c_id;
                                    appointmentWashRight.good = String.valueOf(value.obj.get(i).s_praise);
                                    appointmentWashRight.person = String.valueOf(value.obj.get(i).s_popularity);
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

    private void bindEvent() {
        mainLeftTwoLevelTitle.setOnClickListener(this);
        mainRightTwoLevelTitleLine.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mainLeftTwoLevelTitle) {
            mainLeftTwoLevelTitleLine.setVisibility(View.VISIBLE);
            mainRightTwoLevelTitleLine.setVisibility(View.INVISIBLE);
        } else if (view == mainRightTwoLevelTitle) {
            mainRightTwoLevelTitleLine.setVisibility(View.VISIBLE);
            mainLeftTwoLevelTitleLine.setVisibility(View.INVISIBLE);
        }else {

        }
    }
}
