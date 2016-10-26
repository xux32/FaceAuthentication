package com.example.xux32.faceauthentication.popupwindow.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.xux32.faceauthentication.R;
import com.googlecode.javacv.cpp.opencv_features2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by xux32 on 2016/9/23.
 */
public class ImageChooseAdapter extends BaseAdapter {
    Vector<Bitmap> mVector;
    Context mContext;
//    HashMap<String, Boolean> mStates = new HashMap<String, Boolean>();//用于记录每个RadioButton的状态，并保证只可选一个


    public ImageChooseAdapter(Vector<Bitmap> mVector) {
        this.mVector = mVector;
    }

    @Override
    public int getCount() {
        return mVector.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_choose, null);
            viewHolder.mFace = (ImageView) convertView.findViewById(R.id.id_face);
            viewHolder.mDelete = (TextView) convertView.findViewById(R.id.id_delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Bitmap bmp = (Bitmap) mVector.get(position);
        viewHolder.mFace.setImageBitmap(bmp);
        viewHolder.mDelete.setTag(position);

        viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pressFlag = (int) v.getTag();
                mVector.remove(pressFlag);
                notifyDataSetChanged();
            }
        });


//        Bitmap bmp = (Bitmap) mVector.get(position);
//        Bitmap resizeBmp = bitmapZoom(bmp);
//        Drawable drawable = new BitmapDrawable(parent.getContext().getResources(),resizeBmp);
//        drawable.setBounds(0,0,resizeBmp.getWidth(),resizeBmp.getHeight());
//        viewHolder.mButton.setCompoundDrawables(drawable,null,null,null);
//        viewHolder.mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for(String key: mStates.keySet()){
//                    mStates.put(String.valueOf(position),false);
//                }
//                mStates.put(String.valueOf(position),viewHolder.mButton.isChecked());
//            }
//        });
//
//        if(mStates.get(String.valueOf(position)) == false || mStates.get(String.valueOf(position)) == null){
//            mStates.put(String.valueOf(position),false);
//            viewHolder.mButton.setChecked(false);
//        }else {
//            viewHolder.mButton.setChecked(true);
//        }
        return convertView;
    }

    static class ViewHolder {
        ImageView mFace;
        TextView mDelete;
    }

    public Vector getVectorBmp(){
        return mVector;
    }

//    private Bitmap bitmapZoom(Bitmap bitmap){
//        Matrix matrix = new Matrix();
//        float rateWidth;
//        float rateHeight;
//        if(bitmap.getWidth() > 200) {
//            rateWidth = 200 / bitmap.getWidth();
//        }else{
//            rateWidth = bitmap.getWidth();
//        }
//        if (bitmap.getHeight() > 200){
//            rateHeight = 200/bitmap.getHeight();
//        }else{
//            rateHeight = bitmap.getHeight();
//        }
//        matrix.postScale(rateWidth,rateHeight); //长和宽放大缩小的比例
//        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
//        return resizeBmp;
//    }

}
