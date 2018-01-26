package com.wingfac.laundry.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.MessageBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.ui.adapter.MessageAdapter;
import com.yuyh.library.utils.toast.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/7 0007.
 */

public class MessageFragment extends Fragment {
    public String stateName = "1";
    @Bind(R.id.fragment_cart_list)
    PullToRefreshListView listView;
    MessageBean list = new MessageBean();
    MessageAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, null, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    void initData() {
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        adapter = new MessageAdapter(getActivity(), list,"1");
        listView.setAdapter(adapter);
        setData();
    }

    public void getType(String stateName) {
        this.stateName = stateName;
    }

    void setData() {
        APPApi.getInstance().service
                .getMessage(UserBean.user.id, "1", stateName,"2")
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<MessageBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(MessageBean value) {
                        if (value.responseStatus.equals("0")) {
                            list.obj.addAll(value.obj);
                        } else {
                            ToastUtils.showToast(value.msg);
                        }
                        adapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.notifyDataSetChanged();
                        listView.postDelayed(() -> listView.onRefreshComplete(), 1000);
                        ToastUtils.showToast("请检查您的网络设置");
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }
}
