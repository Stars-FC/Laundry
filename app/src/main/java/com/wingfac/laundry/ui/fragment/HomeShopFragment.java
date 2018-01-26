package com.wingfac.laundry.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.HeadImgBean;
import com.wingfac.laundry.bean.InfoBean;
import com.wingfac.laundry.bean.RecommendBean;
import com.wingfac.laundry.bean.StoreBean;
import com.wingfac.laundry.bean.TitleClassBean;
import com.wingfac.laundry.bean.base.Constant;
import com.wingfac.laundry.ui.activity.SearchActivity;
import com.wingfac.laundry.ui.activity.ShopHomeActivity;
import com.wingfac.laundry.ui.activity.ShopNextActivity;
import com.wingfac.laundry.ui.adapter.RecommendAdapter;
import com.wingfac.laundry.ui.adapter.StoreAdapter;
import com.wingfac.laundry.ui.adapter.ViewPager_GV_ItemAdapter;
import com.wingfac.laundry.ui.adapter.ViewPager_GridView_Adapter;
import com.yuyh.library.utils.toast.ToastUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/7/31 0031.
 */

public class HomeShopFragment extends Fragment {
    @Bind(R.id.fragment_shop_list)
    PullToRefreshListView listView;
    ImageView img;
    @Bind(R.id.head_activity_search_content)
    RelativeLayout search;
    ViewPager viewPager;
    ImageView up;
    GridView searchGrid;
    LinearLayout ll_dots;
    //准备数据
    ArrayList<InfoBean> list = new ArrayList<>();
    InfoBean info = null;
    //gridView 页面item的数量
    int pageItemCount;
    //页面的宽高
    int width;
    int height;
    //保存每个GridView的视图
    ArrayList<View> viewlist = null;
    RecommendBean recommendBean = new RecommendBean();
    RecommendAdapter recommendAdapter;
    StoreAdapter storeAdapter;
    StoreBean storeBean = new StoreBean();
    int page = 0;
    private ImageView[] dots;
    /**
     * ViewPager页数
     */
    private int viewPager_size;
    //当前页
    private int currentIndex;
    private int recommendId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, null, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    void initData() {
        search.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });
        initHead();
        getHeadImg();
    }

    void initHead() {
        ListView listView1 = listView.getRefreshableView();
        LinearLayout listViewHeader = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.head_fragment_shop, listView1, false);
        LinearLayout listViewFoot = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.foot_shop_layout, listView1, false);
        img = listViewHeader.findViewById(R.id.head_fragment_shop_img);
        viewPager = listViewHeader.findViewById(R.id.vPager);
        ll_dots = listViewHeader.findViewById(R.id.ll_dots);
        up = listViewHeader.findViewById(R.id.head_fragment_shop_up);
        searchGrid = listViewHeader.findViewById(R.id.head_fragment_search_grid);
        listView1.addHeaderView(listViewHeader);
        listView1.addFooterView(listViewFoot);
        WindowManager wm = (WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        //初始化视图
        initView();
        recommendAdapter = new RecommendAdapter(getActivity(), recommendBean);
        searchGrid.setAdapter(recommendAdapter);
        setRecommendBean();
        up.setOnClickListener(view -> {
            if (searchGrid.getVisibility() == View.VISIBLE)
                searchGrid.setVisibility(View.GONE);
            else
                searchGrid.setVisibility(View.VISIBLE);

        });
        searchGrid.setOnItemClickListener((adapterView, view, i, l) -> {
            for (int j = 0; j < recommendBean.obj.size(); j++) {
                recommendBean.obj.get(j).state = false;
            }
            if (!recommendBean.obj.get(i).state) {
                recommendBean.obj.get(i).state = true;
                recommendAdapter.notifyDataSetChanged();
                recommendId = recommendBean.obj.get(i).rc_id;
                listView.setRefreshing();
            }
        });
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
        listView.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载中");
        listView.getLoadingLayoutProxy(false, true).setReleaseLabel("加载完毕");
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (recommendId == -1) {
                    page = 0;
                    storeBean.obj.clear();
                    setListData();
                } else {
                    page = 0;
                    storeBean.obj.clear();
                    setRecommendList();
                }

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (recommendId == -1) {
                    page += 1;
                    setListData();
                } else {
                    page += 1;
                    setRecommendList();
                }
            }
        });
        storeAdapter = new StoreAdapter(getActivity(), storeBean);
        listView1.setAdapter(storeAdapter);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i - 2 == 0) {

                }{
                    Intent intent = new Intent(getActivity(), ShopHomeActivity.class);
                    intent.putExtra("s_id", storeBean.obj.get(i - 2).s_id);
                    startActivity(intent);
                }
            }
        });
        setListData();
    }

    void setListData() {
        APPApi.getInstance().service
                .getOneAllStore(String.valueOf(page), "15", "1")
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<StoreBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(StoreBean value) {
                        if (value.responseStatus.equals("0")) {
                            storeBean.obj.addAll(value.obj);
                        } else {
                            ToastUtils.showToast(value.msg);
                        }
                        storeAdapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                        storeAdapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void setRecommendList() {
        APPApi.getInstance().service
                .getRecommendStore("1", String.valueOf(recommendId), String.valueOf(page), "15")
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<StoreBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(StoreBean value) {
                        if (value.responseStatus.equals("0")) {
                            storeBean.obj.addAll(value.obj);
                        } else {
                            Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                        }
                        storeAdapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                        storeAdapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void setRecommendBean() {
        APPApi.getInstance().service
                .recommend()
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<RecommendBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(RecommendBean value) {
                        if (value.responseStatus.equals("0")) {
                            recommendBean.obj.addAll(value.obj);
                            recommendAdapter.notifyDataSetChanged();
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

    private void initView() {
        getTitleClass();
    }

    void getHeadImg() {
        APPApi.getInstance().service
                .headImg("1")
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<HeadImgBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(HeadImgBean value) {
                        if (value.responseStatus.equals("0")) {
                            Glide.with(getActivity())
                                    .load(Constant.BASE_IMG + value.obj.picture_path)
                                    .dontAnimate()
                                    .placeholder(R.drawable.erro_store)
                                    .into(img);
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

    void getTitleClass() {
        APPApi.getInstance().service
                .getOneClass()
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<TitleClassBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(TitleClassBean value) {
                        if (value.responseStatus.equals("0")) {
                            for (int i = 0; i < value.obj.size(); i++) {
                                InfoBean infoBean = new InfoBean(value.obj.get(i).lm_name, value.obj.get(i).lm_picture_path, value.obj.get(i).lm_id);
                                list.add(infoBean);
                            }
                            initDots();
                            setAdapter();
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

    private void setAdapter() {
        viewlist = new ArrayList<View>();
        for (int i = 0; i < viewPager_size; i++) {
            viewlist.add(getGridViewItem(i));    //36
        }
        viewPager.setAdapter(new ViewPager_GridView_Adapter(viewlist));
    }

    //每个GridView页面中的布局
    private View getGridViewItem(int index) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.channel_viewpage_gridview, null);
        GridView gridView = layout.findViewById(R.id.vp_gv);

        int col = (width / 160) > 2 ? (width / 160) : 3;

        Log.e("col", col + "");//3   4
        gridView.setNumColumns(3);
        //每个页面的adapter
        ViewPager_GV_ItemAdapter adapter = new ViewPager_GV_ItemAdapter(getActivity(), list, index, pageItemCount);
        gridView.setAdapter(adapter);
        //设置gridView中点击Item事件
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getActivity(), ShopNextActivity.class);
            intent.putExtra("lm_id", list.get(position).getId());
            startActivity(intent);
            if (null != list.get(position + currentIndex * pageItemCount).getOnClickListener()) {
                list.get(position + currentIndex * pageItemCount).getOnClickListener().ongvItemClickListener(view);
            }
        });
        return gridView;
    }

    private void initDots() {
        int col = (width / 160) > 2 ? (width / 160) : 3;   //3    4
        int row = (height / 400) > 4 ? (height / 400) : 2;   //2     2
        pageItemCount = 6;  //每一页可装item   //6
        //gridView 的页数
        if (list.size() <= 6) {
            viewPager_size = 1;
        } else if (list.size() > 6 && list.size() <= 12) {
            viewPager_size = 2;
        } else {
            viewPager_size = 3;
        }

        if (0 < viewPager_size) {
            ll_dots.removeAllViews();
            if (1 == viewPager_size) {
                ll_dots.setVisibility(View.GONE);
            } else if (1 < viewPager_size) {
                ll_dots.setVisibility(View.VISIBLE);
                for (int j = 0; j < viewPager_size; j++) {
                    ImageView image = new ImageView(getActivity());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);  //dot的宽高
                    params.setMargins(3, 0, 3, 0);
                    image.setBackgroundResource(R.drawable.home_dot);
                    ll_dots.addView(image, params);
                }
            }
        }
        if (viewPager_size != 1) {
            dots = new ImageView[viewPager_size];
            for (int i = 0; i < viewPager_size; i++) {
                //从布局中填充dots数组
                dots[i] = (ImageView) ll_dots.getChildAt(i);
            }
            currentIndex = 0;  //当前页
            dots[currentIndex].setBackgroundResource(R.drawable.home_dot);
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int arg0) {
                    setCurDot(arg0);
                }


                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {

                }
            });
        }

    }

    private void setCurDot(int position) {
        if (position < 0 || position > viewPager_size - 1 || currentIndex == position) {
            return;
        }
        for (int i = 0; i < dots.length; i++) {
            dots[i].setBackgroundResource(R.drawable.home_dot);
        }
        dots[position].setBackgroundResource(R.drawable.home_dot);
        currentIndex = position;

    }
}
