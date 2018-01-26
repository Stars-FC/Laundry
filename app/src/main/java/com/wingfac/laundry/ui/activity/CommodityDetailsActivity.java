package com.wingfac.laundry.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.OneCommodityBean;
import com.wingfac.laundry.bean.PhotoInfo;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.bean.base.Constant;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.ui.adapter.CommercialAddShopAdapter;
import com.wingfac.laundry.weight.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class CommodityDetailsActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "CommodityDetailsActivity";
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.head_layout_right)
    Button right;
    @Bind(R.id.activity_commodity_details_banner)
    ImageView banner;
    @Bind(R.id.activity_commodity_details_subtract)
    Button subtract;
    @Bind(R.id.activity_commodity_details_num)
    TextView num;
    @Bind(R.id.activity_commodity_details_add)
    Button add;
    @Bind(R.id.activity_commodity_details_buy)
    Button buy;
    @Bind(R.id.activity_commodity_details_name)
    TextView name;
    @Bind(R.id.activity_commodity_details_price)
    TextView price;
    @Bind(R.id.activity_commodity_details_details)
    TextView details;
    @Bind(R.id.activity_commodity_details_add_cart)
    Button addCart;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    int intNum = 1;
    String c_id, s_id;
    @Bind(R.id.multiImagView)
    ListView listView;
    List<PhotoInfo> photoInfoList = new ArrayList<>();
    CommercialAddShopAdapter adapter;
    OneCommodityBean commodityBean = new OneCommodityBean();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        ButterKnife.bind(this);
        c_id = getIntent().getStringExtra("c_id");
        s_id = getIntent().getStringExtra("s_id");
        initData();
    }

    private void initData() {
        scrollView.post(() -> scrollView.smoothScrollTo(0, 0));
        left.setOnClickListener(view -> finish());
        title.setText("商品详情");
        subtract.setOnClickListener(this);
        add.setOnClickListener(this);
        addCart.setOnClickListener(this);
        buy.setOnClickListener(this);
        getDate();
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_commodity_details_subtract:
                intNum = Integer.parseInt(num.getText().toString());
                if (intNum > 1) {
                    subtract.setClickable(true);
                    intNum -= 1;
                    num.setText(intNum + "");
                }
                break;
            case R.id.activity_commodity_details_add:
                intNum = Integer.parseInt(num.getText().toString());
                intNum += 1;
                num.setText(intNum + "");
                break;
            case R.id.activity_commodity_details_buy:
                Intent intent = new Intent(getActivity(), ShopOrderActivity.class);
                intent.putExtra("s_id",s_id);
                intent.putExtra("c_id",c_id);
                intent.putExtra("num",String.valueOf(intNum));
                Double num = Double.parseDouble(String.valueOf(intNum));
                intent.putExtra("price",String.valueOf(num*commodityBean.obj.unit_price));
                startActivity(intent);
                break;
            case R.id.activity_commodity_details_add_cart:
                LoadingDialog.showRoundProcessDialog(getActivity());
                addCart();
                break;
        }
    }

    void addCart() {
        APPApi.getInstance().service
                .addCart(UserBean.user.id, String.valueOf(c_id))
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Base value) {
                        LoadingDialog.mDialog.dismiss();
                        Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
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

    void getDate() {
        APPApi.getInstance().service
                .getCommend(String.valueOf(c_id))
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<OneCommodityBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(OneCommodityBean value) {
                        if (value.responseStatus.equals("0")) {
                            commodityBean = value;
                            Glide.with(getActivity())
                                    .load(Constant.BASE_IMG + value.obj.first_picture)
                                    .dontAnimate()
                                    .placeholder(R.drawable.erro_store)
                                    .into(banner);
                            name.setText("商品名称\r" + value.obj.c_name);
                            price.setText("商品单价: ￥" + value.obj.unit_price);
                            details.setText(value.obj.c_introduce);
                            if (!value.obj.picture_one.equals(" ")) {
                                PhotoInfo photoInfo = new PhotoInfo();
                                photoInfo.h = 50;
                                photoInfo.w = 50;
                                photoInfo.url = Constant.BASE_IMG + value.obj.picture_one;
                                photoInfoList.add(photoInfo);
                            }
                            if (!value.obj.picture_two.equals(" ")) {
                                PhotoInfo photoInfo = new PhotoInfo();
                                photoInfo.h = 50;
                                photoInfo.w = 50;
                                photoInfo.url = Constant.BASE_IMG + value.obj.picture_two;
                                photoInfoList.add(photoInfo);
                            }
                            if (!value.obj.picture_three.equals(" ")) {
                                PhotoInfo photoInfo = new PhotoInfo();
                                photoInfo.h = 50;
                                photoInfo.w = 50;
                                photoInfo.url = Constant.BASE_IMG + value.obj.picture_three;
                                photoInfoList.add(photoInfo);
                            }
                            List<String> urlList = new ArrayList<String>();
                            List<String> cache = new ArrayList<String>();
                            for (int i = 0; i < photoInfoList.size(); i++) {
                                urlList.add(photoInfoList.get(i).url);
                                cache.add(photoInfoList.get(i).url);
                            }
                            adapter = new CommercialAddShopAdapter(getActivity(), urlList, cache, false);
                            listView.setAdapter(adapter);
                            setListViewHeightBasedOnChildren(listView);
                            listView.setOnItemClickListener((adapterView, view, i, l) -> {
                                ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());

                                List<String> photoUrls = new ArrayList<String>();
                                for (PhotoInfo photoInfo : photoInfoList) {
                                    photoUrls.add(photoInfo.url);
                                }
                                ImagePagerActivity.startImagePagerActivity((getActivity()), photoUrls, i, imageSize);
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
}
