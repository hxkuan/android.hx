package com.example.webviewuploadchoice;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/1/9.
 */
public class ImgPicker {

    private Activity activity;
    private static ImgPicker mIns = null;

    public ImgPicker(Activity activity) {
        this.activity = activity;
    }

    public static ImgPicker ins(Activity activity) {
        if (mIns == null) {
            mIns = new ImgPicker(activity);
        }
        return mIns;
    }

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;

    public static final int NONE = 0;
    public static final int PHOTO_CAMERA = 1;// 相机拍照
    public static final int PHOTO_COMPILE = 2; // 编辑图片
    public static final int PHOTO_RESOULT = 3;// 结果
    private String ImageName;

    public WebChromeClient client = new WebChromeClient() {

        private void pickPhoto() {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            activity.startActivityForResult(Intent.createChooser(i, "File Browser"), PHOTO_COMPILE);
        }

        private void takePhoto() {
            ImageName = "/" + getStringToday() + ".jpg";// 设置图片的名称
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 设置调用系统摄像头的意图(隐式意图)
            File file = new File(Environment.getExternalStorageDirectory(), ImageName);// 设置照片的输出路径和文件名
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            activity.startActivityForResult(intent, PHOTO_CAMERA);// 开启摄像头
        }
        public String getStringToday() {
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateString = formatter.format(currentTime);
            return dateString;
        }
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_pick_photo:
                        pickPhoto();
                        break;
                    case R.id.btn_take_photo:
                        takePhoto();
                        break;
                    case R.id.btn_cancel:
                        break;
                }

            }
        };


        // For 3.0+ Devices (Start)
        // onActivityResult attached before constructor
        protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            MyPopupWindow pop = new MyPopupWindow(activity,listener);
            pop.showAtLocation(activity.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }


        // For Lollipop 5.0+ Devices
        public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, final FileChooserParams fileChooserParams) {
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            }
            uploadMessage = filePathCallback;

            Log.e("hxk","---------------");

            MyPopupWindow pop = new MyPopupWindow(activity, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.btn_pick_photo:
                            Intent intent = fileChooserParams.createIntent();
                            activity.startActivityForResult(intent, REQUEST_SELECT_FILE);
                            break;
                        case R.id.btn_take_photo:
                            takePhoto();
                            break;
                        case R.id.btn_cancel:
                            break;
                    }

                }
            });
            try {
            pop.showAtLocation(activity.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            } catch (ActivityNotFoundException e) {
                uploadMessage = null;
                Toast.makeText(activity, "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                Log.e("hxk","---------------false");
                return false;
            }


//            Intent intent = fileChooserParams.createIntent();
//            try {
//                activity.startActivityForResult(intent, REQUEST_SELECT_FILE);
//            } catch (ActivityNotFoundException e) {
//                uploadMessage = null;
//                Toast.makeText(activity, "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
//                Log.e("hxk","---------------false");
//                return false;
//            }
            Log.e("hxk","---------------true");
            return false;
        }

        //For Android 4.1 only
        protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadMessage = uploadMsg;
            MyPopupWindow pop = new MyPopupWindow(activity,listener);
            pop.showAtLocation(activity.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }

        protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mUploadMessage = uploadMsg;
            MyPopupWindow pop = new MyPopupWindow(activity,listener);
            pop.showAtLocation(activity.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    };


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null) return;

                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == PHOTO_COMPILE) {
            if (null == mUploadMessage) return;

            Uri result = intent == null || resultCode != activity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else if (requestCode == PHOTO_CAMERA)// 拍照
        {
            if (null == mUploadMessage) return;
            // 设置文件保存路径这里放在跟目录下
            File picture = new File(Environment.getExternalStorageDirectory() + ImageName);
            Bitmap bitmap = BitmapFactory.decodeFile(picture.getAbsolutePath());
            Uri imageUri = Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, null, null));
            mUploadMessage.onReceiveValue(imageUri);
            mUploadMessage = null;

        }

    }


}
