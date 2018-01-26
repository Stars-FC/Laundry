package com.wingfac.laundry.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.app.MyApplication;
import com.wingfac.laundry.bean.LocationMod;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.bean.base.Constant;
import com.wingfac.laundry.ui.activity.CommercialAcitivityStoreHomeActivity;
import com.wingfac.laundry.ui.activity.CommercialActivityMapActivity;
import com.wingfac.laundry.weight.CustomDatePickerNo;
import com.wingfac.laundry.weight.LoadingDialog;
import com.wingfac.laundry.weight.SelectPicPopupWindow;
import com.yuyh.library.utils.toast.ToastUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
 * Created by Administrator on 2017/5/19 0019.
 */

public class CommercialActivityStoreFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "CommercialActivityStoreFragment";
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected File cameraFile;
    @Bind(R.id.head_layout_left)
    RelativeLayout returnLayout;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.head_layout_right)
    Button right;
    @Bind(R.id.commercial_fragment_store_head)
    ImageView head;
    @Bind(R.id.commercial_fragment_store_logo)
    ImageView logo;
    @Bind(R.id.commercial_fragment_store_name)
    EditText name;
    @Bind(R.id.commercial_fragment_store_time)
    TextView timeTV;
    @Bind(R.id.commercial_fragment_store_mobile)
    EditText mobile;
    @Bind(R.id.commercial_fragment_store_address)
    EditText address;
    @Bind(R.id.commercial_fragment_store_describe)
    EditText describe;
    @Bind(R.id.fragment_navigation_content)
    LinearLayout contentLayout;
    @Bind(R.id.commercial_fragment_store_save)
    Button save;
    private CustomDatePickerNo customDatePicker;
    private SelectPicPopupWindow menuWindow;
    private String headPath = "";
    private String logoPath = "";
    private Boolean state = false;

    /**
     * check if sdcard exist
     *
     * @return
     */
    public static boolean isSdcardExist() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.commercial_fragment_store, null, false);
        ButterKnife.bind(this, view);
        initData();
        initDatePicker();
        return view;
    }

    void initData() {
        head.setImageResource(R.drawable.add_big);
        logo.setImageResource(R.drawable.add_smore);
        returnLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), CommercialActivityMapActivity.class);
            startActivity(intent);
        });
        title.setText("店铺信息");
        right.setText("店铺首页");
        right.setVisibility(View.VISIBLE);
        right.setOnClickListener(view -> {
            if(UserBean.userStore==null){
                ToastUtils.showToast("您还未创建店铺");
                return;
            }
            Intent intent = new Intent(getActivity(), CommercialAcitivityStoreHomeActivity.class);
            startActivity(intent);
        });
        head.setOnClickListener(this);
        logo.setOnClickListener(this);
        address.setText(LocationMod.address);
        timeTV.setOnClickListener(view -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            customDatePicker.show(sdf.format(new Date()));
        });
        save.setOnClickListener(this);
        if (UserBean.userStore != null) {
            Glide.with(getActivity()).load(Constant.BASE_IMG + UserBean.userStore.first_picture).into(head);
            headPath = Constant.BASE_IMG + UserBean.userStore.picture;
            Glide.with(getActivity()).load(Constant.BASE_IMG + UserBean.userStore.s_logo).into(logo);
            logoPath = Constant.BASE_IMG + UserBean.userStore.s_logo;
            name.setText(UserBean.userStore.s_name);
            timeTV.setText(UserBean.userStore.open_time);
            mobile.setText(UserBean.userStore.s_mobile);
            address.setText(UserBean.userStore.s_address);
            describe.setText(UserBean.userStore.describe);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UserBean.userStore != null) {
            name.setText(UserBean.userStore.s_name);
            timeTV.setText(UserBean.userStore.open_time);
            mobile.setText(UserBean.userStore.s_mobile);
            address.setText(UserBean.userStore.s_address);
            describe.setText(UserBean.userStore.describe);
        }
    }

    private void initDatePicker() {
        customDatePicker = new CustomDatePickerNo(getActivity(), new CustomDatePickerNo.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                timeTV.setText(time);
            }
        }, "2010-01-01", "2500-01-01"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker.setIsLoop(true); // 允许循环滚动
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commercial_fragment_store_head:
                state = false;
                showPopupWindow();
                break;
            case R.id.commercial_fragment_store_logo:
                state = true;
                showPopupWindow();
                break;
            case R.id.commercial_fragment_store_save:
                if (headPath.equals("")) {
                    Toast.makeText(getActivity(), "请选择展示图片", Toast.LENGTH_SHORT).show();
                    return;
                } else if (logoPath.equals("")) {
                    Toast.makeText(getActivity(), "请选择logo", Toast.LENGTH_SHORT).show();
                    return;
                } else if (name.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "请填写店铺名称", Toast.LENGTH_SHORT).show();
                    return;
                } else if (timeTV.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "请填写开店时间", Toast.LENGTH_SHORT).show();
                    return;
                } else if (mobile.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "请填写联系方式", Toast.LENGTH_SHORT).show();
                    return;
                } else if (address.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "请填写公司地址", Toast.LENGTH_SHORT).show();
                    return;
                } else if (describe.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "请填写店铺描述", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (UserBean.userStore != null) {
                    LoadingDialog.showRoundProcessDialog(getActivity());
                    upDataStore();
                } else {
                    LoadingDialog.showRoundProcessDialog(getActivity());
                    addStore();
                }
                break;
        }
    }

    void login(String phone, String password) {
        APPApi.getInstance().service
                .login(phone, password)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<UserBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UserBean value) {
                        if (value.responseStatus.equals("0")) {
                            UserBean.user = value.obj;
                            UserBean.userStore = value.obj1;
                        } else {
                            ToastUtils.showToast(value.msg);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showToast("请检查您的网络设置");
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void addStore() {
        Map<String, RequestBody> bodyMap = new HashMap<>();
        File file = new File(logoPath);
        File compressedImageFile = Compressor.getDefault(getActivity()).compressToFile(file);
        bodyMap.put("file2" + "\";filename=\"" + compressedImageFile.getName(), RequestBody.create(MediaType.parse("logo/pen"), compressedImageFile));
        File file1 = new File(headPath);
        File compressedImageFile1 = Compressor.getDefault(getActivity()).compressToFile(file1);
        bodyMap.put("file1" + "\";filename=\"" + compressedImageFile1.getName(), RequestBody.create(MediaType.parse("head/pen"), compressedImageFile1));
        APPApi.getInstance().service
                .addStore(RequestBody.create(null, UserBean.user.id),
                        RequestBody.create(null, name.getText().toString()),
                        RequestBody.create(null, timeTV.getText().toString()),
                        RequestBody.create(null, mobile.getText().toString()),
                        RequestBody.create(null, address.getText().toString()),
                        RequestBody.create(null, describe.getText().toString()),
                        RequestBody.create(null, String.valueOf(LocationMod.s_longitude)),
                        RequestBody.create(null, String.valueOf(LocationMod.s_latitude)),
                        bodyMap)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Base value) {
                        LoadingDialog.mDialog.dismiss();
                        Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                        if (value.responseStatus.equals("0")) {
                            login(UserBean.user.tel, UserBean.user.password);
                        } else {
                        }
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

    void upDataStore() {
        Map<String, RequestBody> bodyMap = new HashMap<>();
        if (!logoPath.substring(0, 4).equals("http")) {
            File file = new File(logoPath);
            bodyMap.put("file2" + "\";filename=\"" + file.getName(), RequestBody.create(MediaType.parse("logo/pen"), file));
        }
        if (!headPath.substring(0, 4).equals("http")) {
            File file1 = new File(headPath);
            bodyMap.put("file1" + "\";filename=\"" + file1.getName(), RequestBody.create(MediaType.parse("head/pen"), file1));
        }
        APPApi.getInstance().service
                .upDataStore(RequestBody.create(null, String.valueOf(UserBean.userStore.s_id)),
                        RequestBody.create(null, name.getText().toString()),
                        RequestBody.create(null, timeTV.getText().toString()),
                        RequestBody.create(null, mobile.getText().toString()),
                        RequestBody.create(null, address.getText().toString()),
                        RequestBody.create(null, describe.getText().toString()),
                        RequestBody.create(null, String.valueOf(UserBean.userStore.s_longitude)),
                        RequestBody.create(null, String.valueOf(UserBean.userStore.s_latitude)),
                        bodyMap)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Base value) {
                        LoadingDialog.mDialog.dismiss();
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
                headPath = picturePath;
                Glide.with(getActivity())
                        .load(headPath)
                        .dontAnimate()
                        .placeholder(R.drawable.add_big)
                        .into(head);
            } else {
                logoPath = picturePath;
                Glide.with(getActivity())
                        .load(logoPath)
                        .dontAnimate()
                        .placeholder(R.drawable.add_smore)
                        .into(logo);
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
                headPath = file.getAbsolutePath();
                Glide.with(getActivity())
                        .load(headPath)
                        .dontAnimate()
                        .placeholder(R.drawable.add_big)
                        .into(head);
            } else {
                logoPath = file.getAbsolutePath();
                Glide.with(getActivity())
                        .load(logoPath)
                        .dontAnimate()
                        .placeholder(R.drawable.add_smore)
                        .into(logo);
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
                        headPath = cameraFile.getAbsolutePath();
                        Glide.with(getActivity())
                                .load(headPath)
                                .dontAnimate()
                                .placeholder(R.drawable.add_big)
                                .into(head);
                    } else {
                        logoPath = cameraFile.getAbsolutePath();
                        Glide.with(getActivity())
                                .load(logoPath)
                                .dontAnimate()
                                .placeholder(R.drawable.add_smore)
                                .into(logo);
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
}
