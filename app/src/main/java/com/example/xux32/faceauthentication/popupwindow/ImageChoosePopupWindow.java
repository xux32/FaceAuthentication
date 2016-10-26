package com.example.xux32.faceauthentication.popupwindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.print.PageRange;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xux32.faceauthentication.R;
import com.example.xux32.faceauthentication.popupwindow.adapter.ImageChooseAdapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

/**
 * Created by xux32 on 2016/9/23.
 */
public class ImageChoosePopupWindow extends PopupWindow {

//    private int chooseItem;
//    private boolean mFalg = false;
    ImageChooseAdapter mAdapter = null;
    BmpChooseInterface bmpChooseInterface;

    public ImageChoosePopupWindow(Context mContext, View parent, ImageChooseInterface imageChooseInterface, Vector mVector){
        initPopupWindow(mContext,parent,imageChooseInterface,mVector);
    }
    private void initPopupWindow(final Context mContext, View parent, final ImageChooseInterface imageChooseInterface, Vector mVector){

        View mView = LayoutInflater.from(mContext).inflate(R.layout.popwindow_image_choose,null);
        ListView mList = (ListView) mView.findViewById(R.id.id_list);
        TextView mButton = (TextView) mView.findViewById(R.id.id_ok);

        mAdapter = new ImageChooseAdapter(mVector);
        mList.setAdapter(mAdapter);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vector<Bitmap> vectorBmp = new Vector();
                vectorBmp = mAdapter.getVectorBmp();
                imageChooseInterface.imageChoose(vectorBmp);
                dismiss();
//                for(int i = 0; i < mList.getCount(); i++){
//                    View mChild = mList.getChildAt(i);
//                    RadioButton mRadioButton = (RadioButton) mChild.findViewById(R.id.id_choose);
//                    if(mRadioButton.isChecked()){
//                        chooseItem = i;
//                        mFalg = true;
//                    }
//                }
//                if (mFalg == true){
//                    imageChooseInterface.imageChoose(chooseItem);
//                    dismiss();
//                }else {
//                    Toast.makeText(mContext,"您还未选择图片！",Toast.LENGTH_SHORT).show();
//                }
            }
        });
        setWidth(1040);
        setHeight(1200);
        setFocusable(true);
        setContentView(mView);
        //实例化一个ColorDrawable颜色为半透明，已达到变暗的效果
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//         如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
//       setBackgroundDrawable(dw);
// ;
        showAtLocation(parent, Gravity.CENTER,0,0);
    }

    public interface BmpChooseInterface{
        public void resultofChoose(Vector vector);
    }
}
