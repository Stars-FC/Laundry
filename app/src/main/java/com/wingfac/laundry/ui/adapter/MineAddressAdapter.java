package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.bean.AddressBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.ui.activity.AddressAlterActivity;
import com.wingfac.laundry.weight.LoadingDialog;
import com.yuyh.library.utils.toast.ToastUtils;

import java.io.Serializable;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 个人中心 地址适配器
 * Created by Snow on 2016/11/24 0024.
 */

public class MineAddressAdapter extends BaseAdapter {
    private Context context;
    private AddressBean list;
    private Holder holder;

    public MineAddressAdapter(Context context, AddressBean list) {
        this.context = context;
        this.list = list;

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
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(context).inflate(R.layout.item_home_page_mine_address, null);
            holder.address = view.findViewById(R.id.item_home_page_mine_address_address);
            holder.mobile = view.findViewById(R.id.item_home_page_mine_address_phoneNumber);
            holder.name = view.findViewById(R.id.item_home_page_mine_address_name);
            holder.defaultAddress = view.findViewById(R.id.item_home_page_mine_address_select);
            holder.edit = view.findViewById(R.id.item_home_page_mine_address_edit);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.name.setText(list.obj.get(i).ua_name);
        holder.mobile.setText(list.obj.get(i).ua_mobile);
        holder.address.setText(list.obj.get(i).ua_address);
        if (list.obj.get(i).ua_default == 1) {
            holder.defaultAddress.setImageResource(R.drawable.button_selected);
        }else {
            holder.defaultAddress.setImageResource(R.drawable.button_unchecked);
        }
        holder.defaultAddress.setOnClickListener(view1 -> {
            if (list.obj.get(i).ua_default != 1) {
                LoadingDialog.showRoundProcessDialog(context);
                setDefault(i);
            }
        });
        holder.edit.setOnClickListener(view12 -> {
            Intent intent = new Intent(context, AddressAlterActivity.class);
            intent.putExtra("address", list.obj.get(i));
            context.startActivity(intent);
        });

        return view;
    }

    void setDefault(int i) {
        APPApi.getInstance().service
                .defaultAddress(UserBean.user.id, list.obj.get(i).ua_id, "1")
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
                            for (int j = 0; j < list.obj.size(); j++) {
                                list.obj.get(j).ua_default = 2;
                                list.obj.get(i).ua_default = 1;
                                notifyDataSetChanged();
                            }
                        }
                        ToastUtils.showToast(value.msg);
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

    class Holder {
        public TextView name;//名字
        public TextView mobile;//手机号
        public TextView address;//地址
        public ImageView defaultAddress;//默认地址
        public ImageView edit;//编辑
    }
}
