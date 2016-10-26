package com.example.xux32.faceauthentication.popupwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.xux32.faceauthentication.R;

/**
 * Created by xux32 on 2016/10/26.
 */

public class ParameterSetPopupWindow extends PopupWindow{

    private SeekBar mSeekBar;
    private TextView mThreshold;
    private Button mOk;
    private Button mCancel;
    private int mProgress;
    public ParameterSetPopupWindow(Context mContext, View parent,ParameterSetInterface parameterSetInterface,int threshold){
         initPopupWindow(mContext,parent,parameterSetInterface,threshold);
    }
    private void initPopupWindow(Context mContext, View parent, final ParameterSetInterface parameterSetInterface, int threshold){

        View view = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_parameter,null);
        mSeekBar = (SeekBar) view.findViewById(R.id.id_seekbar);
        mThreshold = (TextView) view.findViewById(R.id.id_threshold);
        mOk = (Button) view.findViewById(R.id.id_ok);
        mCancel = (Button) view.findViewById(R.id.id_cancel);

        mSeekBar.setProgress(threshold);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mThreshold.setText("当前阈值：" + progress);
                mProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parameterSetInterface.ParameterSet(mProgress);
                dismiss();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setWidth(1040);
        setHeight(400);
        setFocusable(true);
        setContentView(view);
        //实例化一个ColorDrawable颜色为半透明，已达到变暗的效果
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//         如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
//       setBackgroundDrawable(dw);
// ;
        showAtLocation(parent, Gravity.BOTTOM,0,0);
    }
}
