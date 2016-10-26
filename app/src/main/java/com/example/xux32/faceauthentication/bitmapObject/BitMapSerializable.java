package com.example.xux32.faceauthentication.bitmapObject;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by xux32 on 2016/9/28.
 */
public class BitMapSerializable implements Serializable {
    private Bitmap bitmap;
    public BitMapSerializable(){

    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}
