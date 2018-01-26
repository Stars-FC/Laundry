package com.wingfac.laundry.ui.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.CommodityBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.ui.adapter.CartAdapter;
import com.wingfac.laundry.weight.LoadingDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MineCartActivity extends BaseActivity {
    private static final String TAG = "HomePageActivityCartFragment";
    @Bind(R.id.head_layout_left)
    RelativeLayout returnImg;
    @Bind(R.id.head_layout_right)
    Button rightImg;
    @Bind(R.id.title_img)
    ImageView titleImg;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.fragment_cart_list)
    PullToRefreshListView listView;
    @Bind(R.id.fragment_navigation_content)
    RelativeLayout contentLayout;
    CartAdapter adapter;
    CommodityBean cartBean = new CommodityBean();
    PopupWindow orderWindow;
    int page = 0;
    private float touchY, touchX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_cart);
        ButterKnife.bind(this);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        page = 0;
        cartBean.obj.clear();
        getCart();
    }

    void getCart() {
        APPApi.getInstance().service
                .getCart(UserBean.user.id, String.valueOf(page), "10")
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<CommodityBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CommodityBean value) {
                        if (value.responseStatus.equals("0")) {
                            cartBean.obj.addAll(value.obj);
                        }
                        adapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                        Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

    void initData() {
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
                page = 0;
                cartBean.obj.clear();
                getCart();

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page += 1;
                getCart();
            }
        });
        adapter = new CartAdapter(getActivity(), cartBean);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, position, arg3) -> {
            CartAdapter.ViewHolder viewHolder = (CartAdapter.ViewHolder) view.getTag();
            viewHolder.cbChecked.performClick();
            adapter.toggleChecked(position - 1);
        });
        returnImg.setOnClickListener(view -> {
            CommodityBean selectList = adapter.getSelectedList();
            if (selectList.obj.size() == 0) {
                Toast.makeText(getActivity(), "请勾选", Toast.LENGTH_SHORT).show();
                return;
            }
            if (orderWindow != null) {
                orderWindow = null;
            }
            showPopWindow(contentLayout, R.layout.windows_delete_cart, selectList);
        });
        titleImg.setImageResource(R.drawable.icon_delete);
        title.setText("我的购物车");
    }

    void removeCar(String scId, final CommodityBean selectList) {
        APPApi.getInstance().service
                .removeCar(UserBean.user.id, scId)
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
                            cartBean.obj.removeAll(selectList.obj);
                        }
                        adapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                        Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialog.mDialog.dismiss();
                        adapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void showPopWindow(View parentView, int convertViewResource, final CommodityBean selectList) {
        //创建一个popUpWindow
        View popLayout = LayoutInflater.from(getActivity()).inflate(convertViewResource, null);
        RelativeLayout rootLayout = popLayout.findViewById(R.id.root_layout);
        final LinearLayout pLayout = popLayout.findViewById(R.id.center_layout);
        TextView name = popLayout.findViewById(R.id.windows_name);
        TextView detail = popLayout.findViewById(R.id.windows_detail);
        name.setText("刪除");
        detail.setText("您确定要刪除此商品吗?");
        popLayout.findViewById(R.id.windows_addirm).setOnClickListener(v -> {
            if (orderWindow.isShowing()) {
                orderWindow.dismiss();
            }
            String scId = "";
            if (selectList.obj.size() == 1) {
                scId = scId + selectList.obj.get(0).cc_id;
            } else {
                for (int i = 0; i < selectList.obj.size(); i++) {
                    if (i == selectList.obj.size() - 1) {
                        scId = scId + selectList.obj.get(i).cc_id;
                    } else {
                        scId = scId + selectList.obj.get(i).cc_id + ",";
                    }
                }
            }
            LoadingDialog.showRoundProcessDialog(getActivity());
            removeCar(scId, selectList);
        });
        popLayout.findViewById(R.id.windows_cancel).setOnClickListener(view -> {
            if (orderWindow.isShowing()) {
                orderWindow.dismiss();
            }
        });
        if (orderWindow == null) {
            //实例化一个popupWindow
            orderWindow =
                    new PopupWindow(popLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //产生背景变暗效果
            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.alpha = 1f;
            getActivity().getWindow().setAttributes(lp);
            //点击外面popupWindow消失
            orderWindow.setOutsideTouchable(true);
            rootLayout.setBackgroundResource(R.color.black);
            rootLayout.getBackground().setAlpha(150);
            rootLayout.setOnTouchListener((view, motionEvent) -> {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchY = motionEvent.getY();
                        touchX = motionEvent.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        if (touchY < pLayout.getTop() || touchY > pLayout.getBottom() || touchX < pLayout.getLeft() || touchX > pLayout.getRight()) {
                            orderWindow.dismiss();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            });
            //popupWindow获取焦点
            orderWindow.setFocusable(true);
            //popupWindow设置背景图
            orderWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            //popupWindow设置开场动画风格
            //popupWindow.setAnimationStyle(R.style.popupWindow_anim);
            //刷新popupWindow
            //popupWindow.update();

            //设置popupWindow消失时的监听
            orderWindow.setOnDismissListener(() -> {
                WindowManager.LayoutParams lp1 = getActivity().getWindow().getAttributes();
                lp1.alpha = 1f;
                getActivity().getWindow().setAttributes(lp1);
            });
            orderWindow.showAtLocation(parentView, Gravity.TOP | Gravity.RIGHT, 0, 0);
        } else {
            //如果popupWindow正在显示，接下来隐藏
            if (orderWindow.isShowing()) {
                orderWindow.dismiss();
            } else {
                //产生背景变暗效果
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
                orderWindow.showAtLocation(parentView, Gravity.TOP | Gravity.RIGHT, 0, 0);
            }
        }
    }
}
