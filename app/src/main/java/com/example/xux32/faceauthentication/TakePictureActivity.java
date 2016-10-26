package com.example.xux32.faceauthentication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class TakePictureActivity extends AppCompatActivity implements SurfaceHolder.Callback,Camera.PreviewCallback {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    private Camera mCamera;
    private TextView mButton;
    private SurfaceView mSurfaceView;
    private SurfaceHolder holder;
    private AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback();
    private ImageView sendImageIv;
    private int mCount = 0;
    private ArrayList<String> mPath = null;
    private String strCaptureFilePath = Environment
            .getExternalStorageDirectory() + "/DCIM/Camera/";
    private String strAuthenFilePath = Environment
            .getExternalStorageDirectory() + "/DCIM/Camera/face";
    private ProgressDialog progressDialog = null;

    private Toast toast;

    private int mFlag;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		/* 隐藏状态栏 */
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /* 隐藏标题栏 */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /* 设定屏幕显示为横向 */
//        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_take_picture);
        /* SurfaceHolder设置 */
        mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
        holder = mSurfaceView.getHolder();
        holder.addCallback(TakePictureActivity.this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // TODO Auto-generated method stub
//				mCamera.autoFocus(mAutoFocusCallback);
		/* 设置拍照Button的OnClick事件处理 */
        mButton = (TextView) findViewById(R.id.myButton);

        toast = Toast.makeText(TakePictureActivity.this, "正在拍照，请稍等！", Toast.LENGTH_SHORT);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在拍照，请稍等！！");
//        mCamera.setDisplayOrientation(90);
        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
				/* 告动对焦后拍照 */
                mCamera.autoFocus(mAutoFocusCallback);
                System.out.println("完成照相功能！");
//                progressDialog.show();
//                showProgressDialog();
                toast.show();
            }
        });


        mPath = new ArrayList<>();
        mFlag = getIntent().getIntExtra("flag", 0);
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

    }

    private void showProgressDialog() {
        WindowManager.LayoutParams params = progressDialog.getWindow().getAttributes();
        progressDialog.getWindow().setGravity(Gravity.BOTTOM);
        params.y = 100;
        progressDialog.getWindow().setAttributes(params);
        progressDialog.show();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            mCamera.autoFocus(mAutoFocusCallback);
            super.handleMessage(msg);
        }
    };

    public void surfaceCreated(SurfaceHolder surfaceholder) {
        try {
			/* 打开相机， */
            System.out.println("打开照相功能！");
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();
            for (int i = 0; i < cameraCount; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mCamera = Camera.open(i);
                    mCamera.setPreviewDisplay(holder);
                }
            }

        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int format, int w,
                               int h) {
		/* 相机初始化 */
        initCamera();
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        stopCamera();
        mCamera.release();
        mCamera = null;
    }

    /* 拍照的method */
    private void takePicture() {

//        //调用AudioManager获取系统音量
//        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);

        if (mCamera != null) {
            mCamera.takePicture(null, rawCallback, jpegCallback);

            System.out.println("this is takePicture()");

        }
//        // 消除拍照声音
//        final Handler soundHandler = new Handler();
//        Timer t = new Timer();
//        t.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                soundHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
//                    }
//                });
//            }
//        }, 1000);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {

        System.out.println("intent=" + intent);
        System.out.println("requestCode=" + requestCode);

        super.startActivityForResult(intent, requestCode);

    }

//    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
//        public void onShutter() {
//			/* 按下快门瞬间会调用这里的程序 */
//            System.out.println("this is onShtter");
//        }
//    };

    private Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {
			/* 要处理raw data?写?否 */
            System.out.println("this is onPictureTaken");
        }
    };

    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {

            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) // 判断SD卡是否存在，并且可以可以读写
            {

            } else {
                Toast.makeText(TakePictureActivity.this, "SD卡不存在或写保护",
                        Toast.LENGTH_LONG).show();
            }


            Calendar c = Calendar.getInstance();
            String time = formatTimer(c.get(Calendar.YEAR)) + "-"
                    + formatTimer(c.get(Calendar.MONTH)) + "-"
                    + formatTimer(c.get(Calendar.DAY_OF_MONTH)) + " "
                    + formatTimer(c.get(Calendar.HOUR_OF_DAY)) + "."
                    + formatTimer(c.get(Calendar.MINUTE)) + "."
                    + formatTimer(c.get(Calendar.SECOND));
            System.out.println("现在时间：" + time + "  将此时间当作图片名存储");

				/* 取得相片 */
            Bitmap bm = BitmapFactory.decodeByteArray(_data, 0,
                    _data.length);
//                BitMapSerializable bitMapSerializable = new BitMapSerializable();
//                bitMapSerializable.setBitmap(bm);

            System.out.println("-------xiangpian----" + bm);
            handler.sendEmptyMessage(0);
				/* 创建文件 */
            String path = null;
            try {

                path = strCaptureFilePath + "" + time
                            + ".jpg";
                System.out.print(path);
                File myCaptureFile = new File(path);
                mPath.add(path);
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(myCaptureFile));
				/* 采用压缩转档方法 */
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                System.out.println("this is pass");

				/* 调用flush()方法，更新BufferStream */
                bos.flush();

				/* 结束OutputStream */
                bos.close();

				/* 让相片显示3秒后圳重设相机 */
                // Thread.sleep(2000);
				/* 重新设定Camera */
                stopCamera();
                if (mFlag == MainActivity.PICTURE_FLAG) {
                    if (mCount < 3) {
                        initCamera();
                        mCount++;
                    } else {
                        Intent intent = new Intent();
                        intent.putStringArrayListExtra("picture", mPath);
                        setResult(RESULT_OK, intent);
                        toast.cancel();
                        finish();
                    }
                } else if (mFlag == MainActivity.PICTURE_FLAG_AUTHEN) {
                        Intent intent = new Intent();
                        intent.putExtra("picture", path);
                        setResult(RESULT_OK, intent);
                        toast.cancel();
                        finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    };
    AlertDialog.Builder b;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * 转换时间
     *
     * @param d
     * @return
     */
    public String formatTimer(int d) {
        return d >= 10 ? "" + d : "0" + d;
    }

    /* 告定义class AutoFocusCallback */
    public final class AutoFocusCallback implements
            android.hardware.Camera.AutoFocusCallback {
        public void onAutoFocus(boolean focused, Camera camera) {
			/* 对到焦点拍照 */
            if (focused) {
                System.out.print("获得焦点");
                takePicture();
            }
        }
    }

    ;

    /* 相机初始化的method */
    private void initCamera() {
        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
				/*
				 * 设定相片大小为1024*768， 格式为JPG
				 */
                //关闭闪光灯
//                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                parameters.setPictureFormat(PixelFormat.JPEG);
//                parameters.setPictureSize(1024, 768);
                mCamera.setDisplayOrientation(90);
                mCamera.setParameters(parameters);
				/* 打开预览画面 */

                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* 停止相机的method */
    private void stopCamera() {
        if (mCamera != null) {
            try {
				/* 停止预览 */
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final static int ID_USER = 0;

    Runnable r = new Runnable() {

        public void run() {
            // TODO Auto-generated method stub
            Message msg = new Message();
            msg.what = ID_USER;
            mHandler.sendMessage(msg);
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case ID_USER:
                    System.out.println("tanchu s");
                    Toast.makeText(TakePictureActivity.this, "���һС��Ŷ...", Toast.LENGTH_SHORT)
                            .show();

                    break;
            }
        }

        ;
    };


}
