package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.MessageBean;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.weight.LoadingDialog;
import com.yuyh.library.utils.toast.ToastUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class MessageAdapter extends BaseAdapter {
    private Context context;
    private MessageBean list;
    private String state;
    public MessageAdapter(Context context, MessageBean list,String state) {
        this.context = context;
        if (list == null) {
            this.list = new MessageBean();
        } else {
            this.list = list;
        }
        this.state = state;
    }

    @Override
    public int getCount() {
        return list.obj.size();
    }

    @Override
    public Object getItem(int i) {
        return list.obj.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(
                    R.layout.item_home_message, null);
            viewHolder.msg = view.findViewById(R.id.item_home_message_msg);
            viewHolder.time = view.findViewById(R.id.item_home_message_time);
            viewHolder.delete = view.findViewById(R.id.item_home_message_delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if(state.equals("0")){
            viewHolder.delete.setVisibility(View.GONE);
        }
        viewHolder.msg.setText(list.obj.get(i).content);
        viewHolder.time.setText(list.obj.get(i).time);
        viewHolder.delete.setOnClickListener(view1 -> {
            LoadingDialog.showRoundProcessDialog(context);
            remvce(i);
        });
        return view;
    }

    void remvce(int i) {
        APPApi.getInstance().service
                .removeMessage(String.valueOf(list.obj.get(i).um_id))
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Base value) {
                        LoadingDialog.mDialog.dismiss();
                        ToastUtils.showToast(value.msg);
                        if (value.responseStatus.equals("0")) {
                            list.obj.remove(i);
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialog.mDialog.dismiss();
                        notifyDataSetChanged();
                        ToastUtils.showToast("请检查您的网络设置");
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

    public static class ViewHolder {
        public TextView msg;
        public TextView time;
        public Button delete;
    }
}
