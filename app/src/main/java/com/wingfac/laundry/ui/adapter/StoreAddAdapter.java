package com.wingfac.laundry.ui.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.bean.StoreClassBean;
import com.wingfac.laundry.bean.base.Constant;


/**
 * 商品添加Adapter
 * Created by asus on 2017/8/26.
 */

public class StoreAddAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private StoreClassBean mData;
    private CallBack mCallBack;

    public StoreAddAdapter(Context context, StoreClassBean mData, CallBack callBack) {
        this.context = context;
        this.mData = mData;
        mCallBack = callBack;
    }

    @Override
    public int getCount() {
        return mData.obj1.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.obj1.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(context, R.layout.activity_dian_pu_shou_ye_item, null);
        EditText editText = convertView.findViewById(R.id.edit_name);
        ImageView imageView = convertView.findViewById(R.id.img);
        String s =  mData.obj1.get(position).cc_picture;
        if(!mData.obj1.get(position).cc_picture.equals("")){
            if (mData.obj1.get(position).cc_picture.substring(0, 4).equals("pict")) {
                s = Constant.BASE_IMG + mData.obj1.get(position).cc_picture;
            } else {
                s = mData.obj1.get(position).cc_picture;
            }

        }
        Glide.with(context)
                .load(s)
                .dontAnimate()
                .placeholder(R.drawable.add_smore)
                .into(imageView);
        imageView.setTag(position);
        imageView.setOnClickListener(this);
        editText.setText(mData.obj1.get(position).cc_name);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mData.obj1.get(position).cc_name = s.toString();
            }
        });

        return convertView;

    }

    public interface CallBack {
        public void click(View view);
    }

    @Override
    public void onClick(View v) {
        mCallBack.click(v);
    }
}
