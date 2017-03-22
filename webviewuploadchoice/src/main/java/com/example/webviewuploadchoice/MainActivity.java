package com.example.webviewuploadchoice;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends Activity {

    private ValueCallback<Uri> mUploadMessage;
    public static final int NONE = 0;
    public static final int PHOTO_CAMERA = 1;// 相机拍照
    public static final int PHOTO_COMPILE = 2; // 编辑图片
    public static final int PHOTO_RESOULT = 3;// 结果
    private String ImageName;
    Bitmap myBitmap;
    private byte[] mContent;

    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.b).setOnClickListener(new OnClickListener() {

            MyPopupWindow pop = new MyPopupWindow(MainActivity.this, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    // 判断
                    switch (v.getId()) {
                        case R.id.btn_pick_photo:

                            // 设置调用系统相册的意图(隐式意图)
//                            Intent intent = new Intent();
//                            // 设置值活动//android.intent.action.PICK
//                            intent.setAction(Intent.ACTION_PICK);
//                            // 设置类型和数据
//                            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");

                            // 开启系统的相册
                            startActivityForResult(intent, PHOTO_COMPILE);

                            break;

                        case R.id.btn_take_photo:

                            // 设置图片的名称
                            ImageName = "/" + getStringToday() + ".jpg";
                            // 设置调用系统摄像头的意图(隐式意图)
                            Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            // 设置照片的输出路径和文件名
                            File file = new File(Environment.getExternalStorageDirectory(), ImageName);
                            intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                            // 开启摄像头
                            startActivityForResult(intent1, PHOTO_CAMERA);
//                            takePhoto();

                            break;
                        case R.id.btn_cancel:

                            break;

                    }

                }
            });
            @Override
            public void onClick(View v) {
                // 显示窗口 main 为mainactivity的主控件
                pop.showAtLocation(MainActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
            }
        });

        // 找控件
        final WebView wv = (WebView) findViewById(R.id.wv);

        // 设置js有效
        WebSettings mWebSettings = wv.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(false);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowContentAccess(true);

        // 为wv加载网络页面
        wv.loadUrl("http://www.script-tutorials.com/demos/199/index.html");

        // 为wv设置js交互
        wv.setWebChromeClient(ImgPicker.ins(this).client);
//        wv.setWebChromeClient(new WebChromeClient() {
//
//            // 进行上传
//            @SuppressWarnings("unused")
//            public void openFileChooser(ValueCallback<Uri> uploadMsg,
//                                        String acceptType, String capture) {
//
//                mUploadMessage = uploadMsg;
//
//                // 在下方弹出poppupWindow
//                MyPopupWindow pop = new MyPopupWindow(MainActivity.this, new OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        // TODO Auto-generated method stub
//                        // 判断
//                        switch (v.getId()) {
//                            case R.id.btn_pick_photo:
//
//                                // 设置调用系统相册的意图(隐式意图)
//                                Intent intent = new Intent();
//
//                                // 设置值活动//android.intent.action.PICK
//                                intent.setAction(Intent.ACTION_PICK);
//
//                                // 设置类型和数据
//                                intent.setDataAndType(
//                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                                        "image/*");
//
//                                // 开启系统的相册
//                                startActivityForResult(intent,
//                                        PHOTO_COMPILE);
//
//                                break;
//
//                            case R.id.btn_take_photo:
//
//                                // 设置图片的名称
//                                ImageName = "/" + getStringToday() + ".jpg";
//
//                                // 设置调用系统摄像头的意图(隐式意图)
//                                Intent intent1 = new Intent(
//                                        MediaStore.ACTION_IMAGE_CAPTURE);
//
//                                // 设置照片的输出路径和文件名
//
//                                File file = new File(Environment
//                                        .getExternalStorageDirectory(),
//                                        ImageName);
//
//                                intent1.putExtra(MediaStore.EXTRA_OUTPUT,
//                                        Uri.fromFile(file));
//                                // 开启摄像头
//                                startActivityForResult(intent1,
//                                        PHOTO_CAMERA);
//
//                                break;
//                            case R.id.btn_cancel:
//
//                                break;
//
//                        }
//
//                    }
//                });
//                // 显示窗口 main 为mainactivity的主控件
//                pop.showAtLocation(MainActivity.this.findViewById(R.id.main),
//                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
//
//            }
//
//        });

    }

    // 调用startActivityResult，返回之后的回调函数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//        ContentResolver resolver = getContentResolver();
//        // 裁剪照片的处理结果
//        if (requestCode == PHOTO_COMPILE) {
//            //进行图片的压缩
//            String path = getAbsoluteImagePath(data.getData());
//            Bitmap bitmap = getimage(path);
//            Uri imageUri = Uri.parse(MediaStore.Images.Media.insertImage(
//                    getContentResolver(), bitmap, null, null));
//
//            Uri result = data == null || resultCode != RESULT_OK ? null
//                    : data.getData();
//            if (mUploadMessage!=null){
//                mUploadMessage.onReceiveValue(result);
//                mUploadMessage = null;
//            }
//        }
//
//        if (requestCode == PHOTO_CAMERA)// 拍照
//        {
//            // 设置文件保存路径这里放在跟目录下
//            File picture = new File(Environment.getExternalStorageDirectory() + ImageName);
//            //进行图片的压缩
//            Bitmap bitmap1 = getimage(picture.getPath());
//
//            Uri imageUri = Uri.parse(MediaStore.Images.Media.insertImage(
//                    getContentResolver(), bitmap1, null, null));
//            if (mUploadMessage!=null){
//                mUploadMessage.onReceiveValue(imageUri);
//                mUploadMessage = null;
//            }
//
//        }

        ImgPicker.ins(this).onActivityResult(requestCode, resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    // http://blog.csdn.net/cherry609195946/article/details/9264409
    // android图片压缩总结
    // // 图片按比例大小压缩方法（根据Bitmap图片压缩）
    // 压缩图片url
    private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    protected String getAbsoluteImagePath(Uri uri) {
        // can post image

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)

        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }


    private void takePhoto() {
        ImageName = "/" + getStringToday() + ".jpg";// 设置图片的名称
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 设置调用系统摄像头的意图(隐式意图)
        File file = new File(Environment.getExternalStorageDirectory(), ImageName);// 设置照片的输出路径和文件名
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, PHOTO_CAMERA);// 开启摄像头
    }

}
