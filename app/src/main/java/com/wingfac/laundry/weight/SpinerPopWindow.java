package com.wingfac.laundry.weight;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.wingfac.laundry.R;


public class SpinerPopWindow extends PopupWindow {

    private Context mContext;
    private RelativeLayout oneLayout, twoLayout;
    private View.OnClickListener itemsOnClick;

    public SpinerPopWindow(Context context, View.OnClickListener itemsOnClick) {
        super(context);

        mContext = context;
        this.itemsOnClick = itemsOnClick;
        init();
    }


    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.window_search, null);
        setContentView(view);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);

        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);


        oneLayout = view.findViewById(R.id.windows_search_one);
        twoLayout = view.findViewById(R.id.windows_search_two);
        oneLayout.setOnClickListener(itemsOnClick);
        twoLayout.setOnClickListener(itemsOnClick);
    }


}
