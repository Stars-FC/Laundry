package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.RechargeHistoryBean;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.utiil.TimeUtils;
import com.wingfac.laundry.weight.LoadingDialog;
import com.yuyh.library.utils.toast.ToastUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lenovo on 2017/8/4.
 */

public class RechargeHistoryAdapter extends BaseAdapter {


    private Context context;
    private RechargeHistoryBean rechargeHistories;

    public RechargeHistoryAdapter(Context context, RechargeHistoryBean rechargeHistories) {
        this.context = context;
        this.rechargeHistories = rechargeHistories;
    }

    @Override
    public int getCount() {
        return rechargeHistories.obj.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.list_item_recharge_history, null);
            holder = new ViewHolder();
            holder.amount = convertView.findViewById(R.id.tv_amount);
            holder.status = convertView.findViewById(R.id.tv_status);
            holder.date = convertView.findViewById(R.id.tv_date);
            holder.delete = convertView.findViewById(R.id.btn_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.amount.setText(String.valueOf(rechargeHistories.obj.get(position).balance));
        holder.status.setText("正确");
        holder.date.setText(TimeUtils.getStrTime(String.valueOf(rechargeHistories.obj.get(position).createTime)));
        holder.delete.setOnClickListener(view -> {
            LoadingDialog.showRoundProcessDialog(context);
            delete(position);
        });
        return convertView;
    }

    void delete(int i) {
        APPApi.getInstance().service
                .deleteRechargeHistory(String.valueOf(rechargeHistories.obj.get(i).boId))
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
                            rechargeHistories.obj.remove(i);
                            notifyDataSetChanged();
                        } else {
                            ToastUtils.showToast(value.msg);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialog.mDialog.dismiss();
                        ToastUtils.showToast("请检查您的网络设置");
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public Object getItem(int position) {
        return rechargeHistories.obj.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView amount;
        TextView status;
        TextView date;
        TextView delete;
    }
}
