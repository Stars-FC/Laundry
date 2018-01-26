package com.wingfac.laundry.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wingfac.laundry.R;
import com.wingfac.laundry.api.APPApi;
import com.wingfac.laundry.app.MyApplication;
import com.wingfac.laundry.bean.base.Base;
import com.wingfac.laundry.ui.activity.base.BaseActivity;
import com.wingfac.laundry.weight.CircleImageView;
import com.wingfac.laundry.weight.LoadingDialog;
import com.wingfac.laundry.weight.SelectPicPopupWindow;
import com.yuyh.library.utils.toast.ToastUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/4/5 0005.
 */

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    private static final String TAG = "RegisterActivity";
    protected File cameraFile;
    @Bind(R.id.et_phone)
    EditText phone;
    @Bind(R.id.et_code)
    EditText code;
    @Bind(R.id.btn_code)
    Button sendCode;
    @Bind(R.id.et_input)
    EditText passWord;
    @Bind(R.id.et_confirm)
    EditText passwordAgain;
    @Bind(R.id.btn_login)
    Button login;
    @Bind(R.id.btn_circle)
    CircleImageView icon;
    @Bind(R.id.content_layout)
    RelativeLayout contentLayout;
    @Bind(R.id.et_pay_confirm)
    TextView payPassword;
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
    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regisger);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        sendCode.setOnClickListener(this);
        login.setOnClickListener(this);
        icon.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_code:
                if (phone.getText().toString().trim().equals("")) {
                    toastS("请输入手机号");
                    return;
                }
                if (!isChinaPhoneLegal(phone.getText().toString())) {
                    ToastUtils.showToast("您输入的手机号有误");
                    return;
                }
                LoadingDialog.showRoundProcessDialog(getActivity());
                sendCode();
                break;
            case R.id.btn_login:
                if (phone.getText().toString().equals("")) {
                    toastS("请填写手机号");
                    return;
                } else if (!isChinaPhoneLegal(phone.getText().toString())) {
                    ToastUtils.showToast("您输入的手机号有误");
                    return;
                } else if (code.getText().toString().equals("")) {
                    toastS("请填写验证码");
                    return;
                } else if (passWord.getText().toString().equals("")) {
                    toastS("请填写密码");
                    return;
                } else if (!passwordAgain.getText().toString().equals(passWord.getText().toString())) {
                    ToastUtils.showToast("两次密码不一致");
                    return;
                } else if (!payPassword.getText().toString().equals("")) {
                    ToastUtils.showToast("请输入支付密码");
                    return;
                }
                LoadingDialog.showRoundProcessDialog(getActivity());
                register();
                break;
            case R.id.btn_circle:
                showPopupWindow();
                break;
        }
    }

    void register() {
        File file = null;
        Map<String, RequestBody> bodyMap = new HashMap<>();
        if (!logoPath.equals("")) {
            file = new File(logoPath);
            bodyMap.put("file" + "\";filename=\"" + file.getName(), RequestBody.create(MediaType.parse("icon/pen"), file));
        }
        APPApi.getInstance().service
                .register(RequestBody.create(null, phone.getText().toString()), RequestBody.create(null, code.getText().toString()), RequestBody.create(null, passWord.getText().toString()), RequestBody.create(null, payPassword.getText().toString()), bodyMap)
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
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.putExtra("name", phone.getText().toString());
                            intent.putExtra("password", passWord.getText().toString());
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
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

    void sendCode() {
        APPApi.getInstance().service
                .sendCode(phone.getText().toString(), "1")
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
                            getCode();
                        } else {
                            Toast.makeText(getActivity(), value.msg, Toast.LENGTH_SHORT).show();
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

    private void getCode() {
        /**
         * 设置点击后验证按钮的背景样式
         */
        sendCode.setClickable(false);
        sendCode.setBackgroundResource(R.drawable.register_selector_last);
        /**
         * 计时器
         */
        CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long l) {
                sendCode.setText(l / 1000 + "S");
            }

            @Override
            public void onFinish() {
                sendCode.setClickable(true);
                sendCode.setBackgroundResource(R.drawable.shape_btn_code);
                sendCode.setText("获取验证码");
            }
        };
        /**
         * 启动计时器
         */
        timer.start();
        toastS("验证码已发送，请注意查收哦");
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
