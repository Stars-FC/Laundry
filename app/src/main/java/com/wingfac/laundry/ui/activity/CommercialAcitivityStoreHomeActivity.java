package com.wingfac.laundry.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.app.MyApplication;
import com.wingfac.laundry.bean.StoreClassBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.ui.adapter.StoreAddAdapter;
import com.wingfac.laundry.weight.LoadingDialog;
import com.wingfac.laundry.weight.SelectPicPopupWindow;
import com.yuyh.library.utils.toast.ToastUtils;

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


public class CommercialAcitivityStoreHomeActivity extends BaseActivity implements StoreAddAdapter.CallBack,View.OnClickListener{
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.head_layout_right)
    Button right;
    @Bind(R.id.banner)
//    ImageView banner;
//    @Bind(R.id.lv_storeAdd_list)
//    ListViewForScrollView mList;
//    @Bind(R.id.ll_storeAdd)
    ImageView mLinearLayout;
    @Bind(R.id.title_img)
    ImageView titleImg;
    @Bind(R.id.main_left_two_level_title_line)
    View mainLeftTwoLevelTitleLine;
    @Bind(R.id.main_left_two_level_title)
    Button mainLeftTwoLevelTitle;
    @Bind(R.id.main_right_two_level_title_line)
    View mainRightTwoLevelTitleLine;
    @Bind(R.id.main_right_two_level_title)
    Button mainRightTwoLevelTitle;
    @Bind(R.id.activity_appointment_wash_left_list)
    ListView activityAppointmentWashLeftList;
    @Bind(R.id.activity_appointment_wash_right_list)
    ListView activityAppointmentWashRightList;
    private StoreClassBean mData = new StoreClassBean();
    private StoreAddAdapter storeAddAdapter;
    private String headPath = "";
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected File cameraFile;
    private SelectPicPopupWindow menuWindow;
    @Bind(R.id.fragment_navigation_content)
    LinearLayout contentLayout;

    /**
     * check if sdcard exist
     *
     * @return
     */
    public static boolean isSdcardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dian_pu_shou_ye);
        ButterKnife.bind(this);
        initData();
        bindEvent();
    }

    int select;
    Boolean state = true;
    List<StoreClassBean.StoreClass> list = new ArrayList<StoreClassBean.StoreClass>();

    void initData() {
//        banner.setOnClickListener(view -> {
//            state = true;
//            showPopupWindow();
//        });
        right.setText("保存");
        right.setVisibility(View.VISIBLE);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                for (int i = 0; i < mData.obj1.size(); i++) {
                    if (!mData.obj1.get(i).cc_name.equals("") && !mData.obj1.get(i).cc_picture.equals("")) {
                        list.add(mData.obj1.get(i));
                    }
                }
                for (int j = 0; j < list.size(); j++) {
                    if (j == list.size() - 1) {
                        LoadingDialog.showRoundProcessDialog(getActivity());
                        alertClass(j);
                    } else {
                        alertClass(j);
                    }
                }
            }
        });
        title.setText("店铺首页");
        left.setOnClickListener(view -> finish());
        storeAddAdapter = new StoreAddAdapter(CommercialAcitivityStoreHomeActivity.this, mData, this);
//        mList.setAdapter(storeAddAdapter);
        mLinearLayout.setOnClickListener(view -> {
            if (mData.obj1.size() >= 20) {
                ToastUtils.showToast("最多添加二十个分类");
                return;
            }
            mData.obj1.add(mData.new StoreClass());
            storeAddAdapter.notifyDataSetChanged();
        });
        getHomeCommodity();

        mainLeftTwoLevelTitle.setText("" + getResources().getString(R.string.group_buy));
        mainRightTwoLevelTitle.setText("" + getResources().getString(R.string.takeaway));
        mainLeftTwoLevelTitle.setOnClickListener(this);
        mainRightTwoLevelTitle.setOnClickListener(this);
    }

    void alertClass(int i) {
        Map<String, RequestBody> bodyMap = new HashMap<>();
        if (!list.get(i).cc_picture.substring(0, 4).equals("pict")) {
            File file1 = new File(list.get(i).cc_picture);
            File compressedImageFile1 = Compressor.getDefault(this).compressToFile(file1);
            bodyMap.put("file" + "\";filename=\"" + compressedImageFile1.getName(), RequestBody.create(MediaType.parse("commodity/pen"), compressedImageFile1));
        }
        APPApi.getInstance().service
                .alertClass(RequestBody.create(null, String.valueOf(list.get(i).cc_id)), RequestBody.create(null, String.valueOf(list.get(i).cc_name)), bodyMap)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Base value) {
                        if (i == list.size() - 1) {
                            LoadingDialog.mDialog.dismiss();
                            finish();
                            ToastUtils.showToast(value.msg);
                        }
                        if (value.responseStatus.equals("0")) {
                            if (i == list.size() - 1) {
                                finish();
                                ToastUtils.showToast(value.msg);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                        if (i == list.size() - 1) {
                            LoadingDialog.mDialog.dismiss();
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void getHomeCommodity() {
        APPApi.getInstance().service
                .getStoreClass(UserBean.user.id)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<StoreClassBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(StoreClassBean value) {
                        mData.obj1.clear();
                        mData.obj1.addAll(value.obj1);
                        for (int i = 0; i < mData.obj1.size(); i++) {
                            if (mData.obj1.get(i).cc_picture.equals(" ")) {
                                mData.obj1.get(i).cc_picture = "";
                            }
                            if (mData.obj1.get(i).cc_name.equals(" ")) {
                                mData.obj1.get(i).cc_name = "";
                            }
                        }
//                        Glide.with(getActivity())
//                                .load(Constant.BASE_IMG + value.obj)
//                                .dontAnimate()
//                                .placeholder(R.drawable.add_big)
//                                .into(banner);
                        storeAddAdapter.notifyDataSetChanged();
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

    @Override
    public void click(View view) {
        select = (int) view.getTag();
        state = false;
        showPopupWindow();
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
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
            if (state) {
                headPath = picturePath;
                LoadingDialog.showRoundProcessDialog(getActivity());
                addStoreFile();
            } else {
                mData.obj1.get(select).cc_picture = picturePath;
                storeAddAdapter.notifyDataSetChanged();
            }

        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getActivity(), "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            if (state) {
                headPath = file.getAbsolutePath();
                LoadingDialog.showRoundProcessDialog(getActivity());
                addStoreFile();
            } else {
                mData.obj1.get(select).cc_picture = file.getAbsolutePath();
                storeAddAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                if (cameraFile != null && cameraFile.exists())
                    if (state) {
                        headPath = cameraFile.getAbsolutePath();
                        LoadingDialog.showRoundProcessDialog(getActivity());
                        addStoreFile();
                    } else {
                        mData.obj1.get(select).cc_picture = cameraFile.getAbsolutePath();
                    }
                storeAddAdapter.notifyDataSetChanged();
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

    void addStoreFile() {
        Map<String, RequestBody> bodyMap = new HashMap<>();
        File file = new File(headPath);
        File compressedImageFile = Compressor.getDefault(getActivity()).compressToFile(file);
        bodyMap.put("file" + "\";filename=\"" + compressedImageFile.getName(), RequestBody.create(MediaType.parse("cover/pen"), compressedImageFile));

        APPApi.getInstance().service
                .addStoreFile(RequestBody.create(null, String.valueOf(UserBean.userStore.s_id)), bodyMap)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Base value) {
                        LoadingDialog.mDialog.dismiss();
//                        Glide.with(getActivity())
//                                .load(headPath)
//                                .dontAnimate()
//                                .placeholder(R.drawable.add_big)
//                                .into(banner);
                        Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
                        LoadingDialog.mDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void bindEvent() {
        mainLeftTwoLevelTitle.setOnClickListener(this);
        mainRightTwoLevelTitleLine.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mainLeftTwoLevelTitle) {
            mainLeftTwoLevelTitleLine.setVisibility(View.VISIBLE);
            mainRightTwoLevelTitleLine.setVisibility(View.INVISIBLE);
        } else if (view == mainRightTwoLevelTitle) {
            mainRightTwoLevelTitleLine.setVisibility(View.VISIBLE);
            mainLeftTwoLevelTitleLine.setVisibility(View.INVISIBLE);
        }else {

        }
    }
}
