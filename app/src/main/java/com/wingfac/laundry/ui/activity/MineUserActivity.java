package com.wingfac.laundry.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.app.MyApplication;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.base.Constant;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.weight.CircleImageView;
import com.wingfac.laundry.weight.LoadingDialog;
import com.wingfac.laundry.weight.SelectPicPopupWindow;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class MineUserActivity extends BaseActivity {
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected File cameraFile;
    @Bind(R.id.head_layout_left)
    RelativeLayout left;
    @Bind(R.id.head_layout_title)
    TextView title;
    @Bind(R.id.head_layout_right)
    Button right;
    @Bind(R.id.activity_mine_icon_layout)
    RelativeLayout iconLayout;
    @Bind(R.id.activity_mine_icon_img)
    CircleImageView icon;
    @Bind(R.id.content_layout)
    LinearLayout contentLayout;
    @Bind(R.id.activity_mine_name)
    EditText name;
    @Bind(R.id.activity_mine_mobile)
    EditText mobile;
    @Bind(R.id.activity_mine_password)
    EditText passWord;
    @Bind(R.id.activity_mine_pay_password)
    EditText payPassword;
    private SelectPicPopupWindow menuWindow;
    private String logoPath = "";

    /**
     * check if sdcard exist
     *
     * @return
     */
    public static boolean isSdcardExist() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        ButterKnife.bind(this);
        initData();
    }

    void initData() {
        name.setText(UserBean.user.nickname);
        mobile.setText(UserBean.user.tel);
        passWord.setText(UserBean.user.password);
        if (UserBean.user.payPassword != null) {
            payPassword.setText(UserBean.user.payPassword);
        }
        Glide.with(getActivity())
                .load(Constant.BASE_IMG + UserBean.user.headPortrait)
                .dontAnimate()
                .placeholder(R.drawable.default_icon)
                .into(icon);
        left.setOnClickListener(view -> finish());
        title.setText("我的信息");
        right.setVisibility(View.VISIBLE);
        right.setText("修改");
        iconLayout.setOnClickListener(view -> showPopupWindow());
        right.setOnClickListener(view -> {
            LoadingDialog.showRoundProcessDialog(getActivity());
            upData();
        });
    }

    void upData() {
        File file = null;
        Map<String, RequestBody> bodyMap = new HashMap<>();
        if (!logoPath.equals("")) {
            file = new File(logoPath);
            bodyMap.put("file" + "\";filename=\"" + file.getName(), RequestBody.create(MediaType.parse("icon/pen"), file));
        }
        APPApi.getInstance().service
                .upDataUser(RequestBody.create(null, UserBean.user.id), RequestBody.create(null, mobile.getText().toString()), RequestBody.create(null, passWord.getText().toString()), RequestBody.create(null, name.getText().toString()), RequestBody.create(null, payPassword.getText().toString()), bodyMap)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<UserBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(UserBean value) {
                        LoadingDialog.mDialog.dismiss();
                        Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
                        if (value.responseStatus.equals("0")) {
                            UserBean.user = value.obj;
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialog.mDialog.dismiss();
                        Toast.makeText(getActivity(), "请检查您的网络设置", Toast.LENGTH_SHORT).show();
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

            logoPath = picturePath;
            Glide.with(getActivity())
                    .load(logoPath)
                    .dontAnimate()
                    .placeholder(R.drawable.default_icon)
                    .into(icon);


        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getActivity(), "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            logoPath = file.getAbsolutePath();
            Glide.with(getActivity())
                    .load(logoPath)
                    .dontAnimate()
                    .placeholder(R.drawable.default_icon)
                    .into(icon);

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                if (cameraFile != null && cameraFile.exists())
                    logoPath = cameraFile.getAbsolutePath();
                Glide.with(getActivity())
                        .load(logoPath)
                        .dontAnimate()
                        .placeholder(R.drawable.default_icon)
                        .into(icon);

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
