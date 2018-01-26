package com.wingfac.laundry.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.app.MyApplication;
import com.wingfac.laundry.bean.CommodityBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.bean.base.Constant;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.ui.adapter.CommercialAddShopAdapter;
import com.wingfac.laundry.weight.LoadingDialog;
import com.wingfac.laundry.weight.SelectPicPopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/5/22 0022.
 */

public class CommercialAddShopActivity extends BaseActivity {
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    private static final String TAG = "CommercialAddShopActivity";
    protected File cameraFile;
    @Bind(R.id.head_layout_left)
    RelativeLayout returnImg;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.head_layout_right)
    Button right;
    @Bind(R.id.activity_shop_add_shop_list)
    ListView listView;
    @Bind(R.id.content_layout)
    LinearLayout contentLayout;
    EditText name, price, detail,chicun,guige,danwei;
    ImageView banner;
    CommercialAddShopAdapter adapter;
    List<String> img = new ArrayList<>();
    List<String> cache = new ArrayList<>();
    CommodityBean.Commodity commodity;
    private SelectPicPopupWindow menuWindow;
    private String iconPath = "";
    private String headPath = "";
    private Boolean state = false;
    private String ccId;
    /**
     * check if sdcard exist
     *
     * @return
     */
    public static boolean isSdcardExist() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MyApplication.floatView.removeFromWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_add_shop);
        ButterKnife.bind(this);
        ccId = getIntent().getStringExtra("ccId");
        if (getIntent().getSerializableExtra("Commodity") != null) {
            commodity = (CommodityBean.Commodity) getIntent().getSerializableExtra("Commodity");
            if (commodity.picture_one != null && !commodity.picture_one.toString().trim().equals("")) {
                img.add(Constant.BASE_IMG + commodity.picture_one);
                cache.add(Constant.BASE_IMG + commodity.picture_one);
            }
            if (commodity.picture_two != null && !commodity.picture_two.toString().trim().equals("")) {
                img.add(Constant.BASE_IMG + commodity.picture_two);
                cache.add(Constant.BASE_IMG + commodity.picture_two);
            }
            if (commodity.picture_three != null && !commodity.picture_three.toString().trim().equals("")) {
                img.add(Constant.BASE_IMG + commodity.picture_three);
                cache.add(Constant.BASE_IMG + commodity.picture_three);
            }
        }
        new Handler().postDelayed(() -> initData(), 200);
    }

    private void initData() {
        right.setVisibility(View.VISIBLE);
        right.setText("确定");
        right.setOnClickListener(view -> {
            if (img.size() == 0) {
                toastS("请选择至少一张图片");
                return;
            } else if (name.getText().toString().equals("")) {
                toastS("请输入名称");
                return;
            } else if (price.getText().toString().equals("")) {
                toastS("请输入单价");
                return;
            } else if (detail.getText().toString().equals("")) {
                toastS("请输入介绍");
                return;
            } else if (headPath.equals("")) {
                toastS("请选择一张展示图");
                return;
            }
            if (commodity == null) {
                LoadingDialog.showRoundProcessDialog(getActivity());
                addCommodity();
            } else {
                LoadingDialog.showRoundProcessDialog(getActivity());
                upDataCommodity();
            }
        });
        returnImg.setOnClickListener(view -> finish());
        title.setText("添加商品");
        initHead();
        adapter = new CommercialAddShopAdapter(getActivity(), img, cache, true);
        listView.setAdapter(adapter);
        getCache();
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (l != -1) {
                if (img.get(i - 1).equals("000000")) {
                    if (img.size() == 4) {
                        toastS("最多选择三张");
                        return;
                    }
                    state = false;
                    showPopupWindow();
                }
            }

        });
    }

    void getCache() {
        cache.clear();
        if (commodity != null) {
            if (commodity.picture_one != null && !commodity.picture_one.toString().trim().equals("")) {
                new getImageCacheAsyncTask(this).execute(Constant.BASE_IMG + commodity.picture_one);
            }
            if (commodity.picture_two != null && !commodity.picture_two.toString().trim().equals("")) {
                new getImageCacheAsyncTask(this).execute(Constant.BASE_IMG + commodity.picture_two);
            }
            if (commodity.picture_three != null && !commodity.picture_three.toString().trim().equals("")) {
                new getImageCacheAsyncTask(this).execute(Constant.BASE_IMG + commodity.picture_three);
            }
        }
    }

    void addCommodity() {
        Map<String, RequestBody> bodyMap = new HashMap<>();
        for (int i = 0; i < img.size() - 1; i++) {
            File file = new File(img.get(i));
            File compressedImageFile = Compressor.getDefault(this).compressToFile(file);
            bodyMap.put("files" + "\";filename=\"" + compressedImageFile.getName(), RequestBody.create(MediaType.parse("cover/pen"), compressedImageFile));
        }
        File file1 = new File(headPath);
        File compressedImageFile1 = Compressor.getDefault(this).compressToFile(file1);
        bodyMap.put("file1" + "\";filename=\"" + compressedImageFile1.getName(), RequestBody.create(MediaType.parse("commodity/pen"), compressedImageFile1));

        APPApi.getInstance().service
                .addCommodity(RequestBody.create(null,String.valueOf(UserBean.userStore.s_id) ),
                        RequestBody.create(null, ccId),
                        RequestBody.create(null, name.getText().toString()),
                        RequestBody.create(null, price.getText().toString()),
                        RequestBody.create(null, guige.getText().toString()),
                        RequestBody.create(null, chicun.getText().toString()),
                        RequestBody.create(null, danwei.getText().toString()),
                        RequestBody.create(null, detail.getText().toString()), bodyMap)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Base value) {
                        LoadingDialog.mDialog.dismiss();
                        if (value.responseStatus.equals("0")) {

                            toastS(value.msg);
                            finish();
                        } else {
                            toastS(value.msg);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        toastS("请检查您的网络设置");
                        LoadingDialog.mDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void upDataCommodity() {
        Map<String, RequestBody> bodyMap = new HashMap<>();
        for (int i = 0; i < cache.size(); i++) {
            File file = new File(cache.get(i));
            File compressedImageFile = Compressor.getDefault(this).compressToFile(file);
            bodyMap.put("files" + "\";filename=\"" + compressedImageFile.getName(), RequestBody.create(MediaType.parse("cover/pen"), compressedImageFile));
        }
        if (!headPath.substring(0, 4).equals("http")) {
            File file1 = new File(headPath);
            File compressedImageFile1 = Compressor.getDefault(this).compressToFile(file1);
            bodyMap.put("file1" + "\";filename=\"" + compressedImageFile1.getName(), RequestBody.create(MediaType.parse("commodity/pen"), compressedImageFile1));
        }
        APPApi.getInstance().service
                .upDataCommodity(RequestBody.create(null, String.valueOf(commodity.c_id)),
                        RequestBody.create(null, name.getText().toString()),
                        RequestBody.create(null, price.getText().toString()),
                        RequestBody.create(null, guige.getText().toString()),
                        RequestBody.create(null, chicun.getText().toString()),
                        RequestBody.create(null, danwei.getText().toString()),
                        RequestBody.create(null, detail.getText().toString()), bodyMap)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Base value) {
                        LoadingDialog.mDialog.dismiss();
                        if (value.responseStatus.equals("0")) {
                            toastS(value.msg);
                            finish();
                        } else {
                            toastS(value.msg);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        toastS("请检查您的网络设置");
                        LoadingDialog.mDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void initHead() {
        LinearLayout headLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.head_activity_shop_add_shop, listView, false);
        LinearLayout footLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.foot_activity_shop_add_shop, listView, false);
        banner =  headLayout.findViewById(R.id.head_activity_shop_add_shop_banner);
        name =  headLayout.findViewById(R.id.head_activity_shop_add_shop_name);
        price =  headLayout.findViewById(R.id.head_activity_shop_add_shop_price);
        detail =  headLayout.findViewById(R.id.head_activity_shop_add_shop_details);
        guige = headLayout.findViewById(R.id.head_activity_shop_add_shop_guige);
        chicun = headLayout.findViewById(R.id.head_activity_shop_add_shop_chicun);
        danwei = headLayout.findViewById(R.id.head_activity_shop_add_shop_danwei);
        if (commodity != null) {
            if (!commodity.first_picture.equals("")) {
                headPath = Constant.BASE_IMG + commodity.first_picture;
            }
            price.setText(String.valueOf(commodity.unit_price));
            name.setText(commodity.c_name);
            detail.setText(commodity.c_introduce);
            guige.setText(commodity.c_standard);
            chicun.setText(commodity.c_size);
            danwei.setText(commodity.c_unit);
            img.add("000000");
        } else {
            img.add("000000");
        }

        listView.addHeaderView(headLayout);
        listView.addFooterView(footLayout);
        if (!headPath.equals("")) {
            Glide.with(getActivity())
                    .load(headPath)
                    .dontAnimate()
                    .placeholder(R.drawable.erro_store)
                    .into(banner);
        } else {
            banner.setImageResource(R.drawable.add_big);
        }
        banner.setOnClickListener(view -> {
            state = true;
            showPopupWindow();
        });
    }

    /**
     * capture new image
     */
    protected void selectPicFromCamera() {
        if (!isSdcardExist()) {
            Toast.makeText(getActivity(), "SD卡不存在，不能拍照", Toast.LENGTH_SHORT).show();
            return;
        }

        cameraFile = new File(MyApplication.DEFAULT_SAVE_IMAGE_PATH, System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    /**
     * select local image
     */
    protected void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    /**
     * send image
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;
            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(getActivity(), "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            if (state == false) {
                iconPath = picturePath;
                if (img.contains("000000")) {
                    img.remove("000000");
                }
                img.add(iconPath);
                cache.add(iconPath);
                img.add("000000");
                adapter.notifyDataSetChanged();
            } else {
                headPath = picturePath;
                Glide.with(getActivity())
                        .load(headPath)
                        .dontAnimate()
                        .placeholder(R.drawable.erro_store)
                        .into(banner);
            }

        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getActivity(), "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            if (state == false) {
                iconPath = file.getAbsolutePath();
                if (img.contains("000000")) {
                    img.remove("000000");
                }
                img.add(iconPath);
                cache.add(iconPath);
                img.add("000000");
                adapter.notifyDataSetChanged();
            } else {
                headPath = file.getAbsolutePath();
                Glide.with(getActivity())
                        .load(headPath)
                        .dontAnimate()
                        .placeholder(R.drawable.erro_store)
                        .into(banner);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                if (cameraFile != null && cameraFile.exists())
                    if (state == false) {
                        iconPath = cameraFile.getAbsolutePath();
                        if (img.contains("000000")) {
                            img.remove("000000");
                        }
                        img.add(iconPath);
                        cache.add(iconPath);
                        img.add("000000");
                        adapter.notifyDataSetChanged();
                    } else {
                        headPath = cameraFile.getAbsolutePath();
                        Glide.with(getActivity())
                                .load(headPath)
                                .dontAnimate()
                                .placeholder(R.drawable.erro_store)
                                .into(banner);
                    }
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            }
        }
    }

    void showPopupWindow() {
        View.OnClickListener itemsOnClick = v -> {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_take_photo:
                    selectPicFromCamera();
                    break;
                case R.id.btn_pick_photo:
                    selectPicFromLocal();
                    break;
                default:
                    break;
            }

        };
        //实例化SelectPicPopupWindow
        menuWindow = new SelectPicPopupWindow(getActivity(), itemsOnClick);
        //显示窗口
        menuWindow.showAtLocation(contentLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        //为弹出窗口实现监听类
    }

    private class getImageCacheAsyncTask extends AsyncTask<String, Void, File> {
        private final Context context;

        public getImageCacheAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected File doInBackground(String... params) {
            String imgUrl = params[0];
            try {
                return Glide.with(context)
                        .load(imgUrl)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();
            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(File result) {
            if (result == null) {
                return;
            }
            //此path就是对应文件的缓存路径
            String path = result.getPath();
            cache.add(path);
        }
    }
}
