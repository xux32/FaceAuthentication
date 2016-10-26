package com.example.xux32.faceauthentication;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.xux32.faceauthentication.bitmapObject.BitMapSerializable;
import com.example.xux32.faceauthentication.popupwindow.ImageChooseInterface;
import com.example.xux32.faceauthentication.popupwindow.ImageChoosePopupWindow;
import com.example.xux32.faceauthentication.popupwindow.ParameterSetInterface;
import com.example.xux32.faceauthentication.popupwindow.ParameterSetPopupWindow;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_imgproc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_COMP_CORREL;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_HIST_ARRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCalcHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCompareHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvNormalizeHist;

public class MainActivity extends AppCompatActivity implements ImageChooseInterface,ParameterSetInterface{
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }

    Button mBTakePic;
    Button mDetection;
    //    Button mChoosepic;
    Button mParameterButton;
    Button mAuthentication;
//    ImageView mFaceImage;
    LinearLayout mView;

    private File mPicFile;
    private Bitmap mSrcImage;
    private Bitmap mSrcFace;
    private Bitmap mDetectFace;
    private FaceDetector mFaceDetector;
    FaceDetector.Face[] faceList;
    private Vector<Bitmap> mVector;
    private ProgressDialog progressDialog = null;
    private ImageChoosePopupWindow popupWindow;
    private ParameterSetPopupWindow parameterSetPopupWindow;

    private final int N_MAX = 2;
    private final int TAKE_PICKTURE = 1;
    private final int TAKE_PICTURE_AUTHEN = 2;
    private ArrayList<String> mPathList = null;
    private String mPath = null;
    public final static int PICTURE_FLAG = 1;
    public final static int PICTURE_FLAG_AUTHEN = 2;

    private int mThresholdParam = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mView = (LinearLayout) findViewById(R.id.id_mianview);
        mBTakePic = (Button) findViewById(R.id.id_takepic);
        mDetection = (Button) findViewById(R.id.id_detection);
//        mFaceImage = (ImageView) findViewById(R.id.id_face_image);
        mAuthentication = (Button) findViewById(R.id.id_authentication);
        mParameterButton = (Button)findViewById(R.id.id_param);

        mBTakePic.setOnClickListener(myClickListener);
        mDetection.setOnClickListener(myClickListener);
        mAuthentication.setOnClickListener(myClickListener);
        mParameterButton.setOnClickListener(myClickListener);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在进行人脸检测，请稍等");

        mPathList = new ArrayList<>();
        mVector = new Vector();

    }

    View.OnClickListener myClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.id_takepic:
//                    mPicFile = new File(Environment.getExternalStorageDirectory(), "face.jpg");
//                    Intent mIntent = new Intent();
//                    mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//                    mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPicFile));
//                    mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
//                    startActivityForResult(mIntent, TAKE_PICKTURE);
                    Intent intent = new Intent(MainActivity.this, TakePictureActivity.class);
                    intent.putExtra("flag",PICTURE_FLAG);
                    startActivityForResult(intent, TAKE_PICKTURE);
                    break;
//                case R.id.id_choosepic:
//                    Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                    openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                    startActivityForResult(openAlbumIntent,CHOOSE_PICTURE);
//                    break;
                case R.id.id_detection:
//                    showProgressDialog();
//                    Toast.makeText(MainActivity.this,"正在进行人脸检测，请稍等！",Toast.LENGTH_SHORT).show();
                    progressDialog.show();
                    initFace();
                    for (int i = 0; i < mPathList.size(); i++) {
                        detectFace(i);
                    }
                    progressDialog.hide();
                    getPopupWindow();

                    break;
                case R.id.id_authentication:
                    Intent intent1 = new Intent(MainActivity.this, TakePictureActivity.class);
                    intent1.putExtra("flag",PICTURE_FLAG_AUTHEN);
                    startActivityForResult(intent1, TAKE_PICTURE_AUTHEN);
                    break;
                case R.id.id_param:
                    getPopupWindowParam();
                    break;
            }
        }
    };

    private void showProgressDialog(){
        WindowManager.LayoutParams params = progressDialog.getWindow().getAttributes();
        progressDialog.getWindow().setGravity(Gravity.BOTTOM);
        params.y = 100;
        progressDialog.getWindow().setAttributes(params);
        progressDialog.show();
    }
    private void getPopupWindow(){
        popupWindow = new ImageChoosePopupWindow(this,mView,this,mVector);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackageAlpha(1f);
            }
        });
        setBackageAlpha(0.5f);

    }
    private void getPopupWindowParam(){
        parameterSetPopupWindow = new ParameterSetPopupWindow(this,mView,this,mThresholdParam);
        parameterSetPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackageAlpha(1f);
            }
        });
        setBackageAlpha(0.5f);
    }
    private void setBackageAlpha(float alpha){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == TAKE_PICKTURE) {
//            if(mPicFile == null){
//                Toast.makeText(this, "拍照失败！", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            mSrcImage = BitmapFactory.decodeFile(mPicFile.getAbsolutePath());
            mPathList = (ArrayList) data.getStringArrayListExtra("picture");
            Log.i("face count", "count = " + mPathList.size());

//            bitmap = BitmapFactory.decodeFile(mPath.get(0));
//            mFaceImage.setImageBitmap(bitmap);
        }else if(requestCode == TAKE_PICTURE_AUTHEN){
            mPath = data.getStringExtra("picture");
            Log.i("authentication_face", "path = " + mPath);
            CmpPic();
        }
//        else if(requestCode == CHOOSE_PICTURE){
//            if (data == null){
//                return;
//            }
//            ContentResolver resolver = getContentResolver();
//            //照片的原始资源地址
//            Uri originalUri = data.getData();
//            //使用ContentProvider通过URI获取原始图片
//            try {
//                mSrcImage = MediaStore.Images.Media.getBitmap(resolver, originalUri);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
    }

    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    private void initFace() {

        mSrcImage = BitmapFactory.decodeFile(mPathList.get(0));
        mSrcImage = rotateBitmapByDegree(mSrcImage,270);
//        mFaceImage.setImageBitmap(mSrcImage);
        mSrcFace = mSrcImage.copy(Bitmap.Config.RGB_565, true);

        int w = mSrcFace.getWidth();
        int h = mSrcFace.getHeight();
        Log.i("人脸检测", "待检测图像: w = " + w + "h = " + h);
        mFaceDetector = new FaceDetector(w, h, N_MAX);
        faceList = new FaceDetector.Face[N_MAX];
    }



    public void detectFace(int count) {
        //      Drawable d = getResources().getDrawable(R.drawable.face_2);
        //      Log.i(tag, "Drawable尺寸 w = " + d.getIntrinsicWidth() + "h = " + d.getIntrinsicHeight());
        //      BitmapDrawable bd = (BitmapDrawable)d;
        //      Bitmap srcFace = bd.getBitmap();

        mSrcImage = BitmapFactory.decodeFile(mPathList.get(count));
        mSrcFace = mSrcImage.copy(Bitmap.Config.RGB_565, true);
        mSrcFace = rotateBitmapByDegree(mSrcFace,270);
        int nFace = mFaceDetector.findFaces(mSrcFace, faceList);
        Log.i("facedetection：", "检测到人脸：num = " + nFace);

        for (int i = 0; i < nFace; i++) {
            FaceDetector.Face f = faceList[i];
            PointF midPoint = new PointF();
            float dis = f.eyesDistance();
            f.getMidPoint(midPoint);
            int dd = (int) (dis);
            Point eyeLeft = new Point((int) (midPoint.x - dis / 2), (int) midPoint.y);
            Point eyeRight = new Point((int) (midPoint.x + dis / 2), (int) midPoint.y);
            Rect faceRect = new Rect((int) (midPoint.x - dd), (int) (midPoint.y - dd), (int) (midPoint.x + 2 * dd), (int) (midPoint.y + 3 * dd));
            mDetectFace = Bitmap.createBitmap(mSrcFace, (int) (midPoint.x - dd), (int) (midPoint.y - dd), 2 * dd, (int) (2.5 * dd), null, false);

            Log.i("coordinate", "左眼坐标 x = " + eyeLeft.x + "y = " + eyeLeft.y);
            mVector.addElement(mDetectFace);
        }
        //选择图片
//        new ImageChoosePopupWindow(this,getCurrentFocus(),this,mVector);
        saveJpeg(mDetectFace);
//        mFaceImage.setImageBitmap(mDetectFace);
//        Log.i("imagesave", "保存完毕");

    }

    public boolean checkFace(Rect rect) {
        int w = rect.width();
        int h = rect.height();
        int s = w * h;
        Log.i("checkface", "人脸 宽w = " + w + "高h = " + h + "人脸面积 s = " + s);
        if (s < 1000000) {
            Log.i("checkface", "无效人脸，舍弃.");
            return false;
        } else {
            Log.i("checkface", "有效人脸，保存.");
            return true;
        }
    }

//    public void imageChoose(int position) {
//        mPosition = position;
//        Bitmap bmp = (Bitmap) mVector.get(position);
//        saveJpeg(bmp);
//        mFaceImage.setImageBitmap(bmp);
//        Log.i("imagesave", "保存完毕！");
//    }
    public String formatTimer(int d) {
        return d >= 10 ? "" + d : "0" + d;
    }

    public void saveJpeg(Bitmap bmp) {

         String strCaptureFilePath = Environment
                .getExternalStorageDirectory() + "/faceimage/";
        Calendar c = Calendar.getInstance();
        String time = formatTimer(c.get(Calendar.YEAR)) + "-"
                + formatTimer(c.get(Calendar.MONTH)) + "-"
                + formatTimer(c.get(Calendar.DAY_OF_MONTH)) + " "
                + formatTimer(c.get(Calendar.HOUR_OF_DAY)) + "."
                + formatTimer(c.get(Calendar.MINUTE)) + "."
                + formatTimer(c.get(Calendar.SECOND));
        String jpegName = strCaptureFilePath + "" + time
                + ".jpg";
//        String jpegName = Environment.getExternalStorageDirectory() + "/detectface.jpg";
        Log.i("saveimage", "路径 " + jpegName);

        //File jpegFile = new File(jpegName);
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            //Bitmap newBM = bm.createScaledBitmap(bm, 600, 800, false);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public double CmpPic() {
        int l_bins = 20;
        int hist_size[] = {l_bins};

        float v_ranges[] = {0, 100};
        float ranges[][] = {v_ranges};
        double result = 0;
        Vector mResultList = new Vector<>();

//        File file = new File(Environment
//                .getExternalStorageDirectory() + "/faceimage/");
//        File[] files = file.listFiles();

        opencv_core.IplImage Image1 = cvLoadImage(mPath, CV_LOAD_IMAGE_GRAYSCALE);
        opencv_core.IplImage imageArr1[] = {Image1};
        opencv_imgproc.CvHistogram Histogram1 = opencv_imgproc.CvHistogram.create(1, hist_size,
                CV_HIST_ARRAY, ranges, 1);
        cvCalcHist(imageArr1, Histogram1, 0, null);
        cvNormalizeHist(Histogram1, 100.0);

        opencv_core.IplImage Image2 = null;
        Log.i("人脸count",String.valueOf(mPathList.size()));

        for(int i = 0;i < mPathList.size(); i++){
            Image2 = cvLoadImage(mPathList.get(i), CV_LOAD_IMAGE_GRAYSCALE);
            opencv_core.IplImage imageArr2[] = {Image2};
            opencv_imgproc.CvHistogram Histogram2 = opencv_imgproc.CvHistogram.create(1, hist_size,
                    CV_HIST_ARRAY, ranges, 1);
            cvCalcHist(imageArr2, Histogram2, 0, null);
            cvNormalizeHist(Histogram2, 100.0);

            mResultList.add(cvCompareHist(Histogram1, Histogram2, CV_COMP_CORREL));
        }

//        opencv_core.IplImage Image1 = cvLoadImage(Environment.getExternalStorageDirectory() + "/detectface.jpg", CV_LOAD_IMAGE_GRAYSCALE);
//        opencv_core.IplImage Image2 = cvLoadImage(Environment.getExternalStorageDirectory() + "/detectface1.jpg", CV_LOAD_IMAGE_GRAYSCALE);

//        opencv_core.IplImage imageArr1[] = {Image1};
//        opencv_core.IplImage imageArr2[] = {Image2};

//        opencv_imgproc.CvHistogram Histogram1 = opencv_imgproc.CvHistogram.create(1, hist_size,
//                CV_HIST_ARRAY, ranges, 1);
//        opencv_imgproc.CvHistogram Histogram2 = opencv_imgproc.CvHistogram.create(1, hist_size,
//                CV_HIST_ARRAY, ranges, 1);

//        cvCalcHist(imageArr1, Histogram1, 0, null);
//        cvCalcHist(imageArr2, Histogram2, 0, null);

//        cvNormalizeHist(Histogram1, 100.0);
//        cvNormalizeHist(Histogram2, 100.0);
//        double result = cvCompareHist(Histogram1, Histogram2, CV_COMP_CORREL);
//        Log.i("比较结果", String.valueOf(result));

        for(int i = 0; i < mResultList.size(); i++ ){
            result = result + (double)mResultList.get(i);
            Log.i("比较结果", String.valueOf((double)mResultList.get(i)));
        }
        result = result/mResultList.size();
        Log.i("比较结果", String.valueOf(result));
        if(result > mThresholdParam){
            Toast.makeText(MainActivity.this, "相似度:" + String.valueOf(result) + "认证通过",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(MainActivity.this, "相似度:" + String.valueOf(result)+ "认证未通过", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    @Override
    public void imageChoose(Vector<Bitmap> vector) {
        mVector.clear();
        mVector = vector;

       for (int a = 0;a< mVector.size(); a++){
           Bitmap mFace = mVector.get(a);

       }
    }

    public void ParameterSet(int mThreshold){
        mThresholdParam = mThreshold;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
