package com.example.criminalintent.classs;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import androidx.fragment.app.FragmentActivity;

public class PictureUtils {
    public static Bitmap getScaledBitmap(String path , int destWidth , int destHeight){
        //创建可操作的对象
        BitmapFactory.Options options =new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        //开始压缩
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if (srcHeight >  destHeight || srcWidth > destWidth){
            float heightScale = srcHeight / destHeight;
            float widthScale =srcWidth /destWidth;
            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
        }
        options =new BitmapFactory.Options();
        options.inSampleSize =inSampleSize;

        //返回位图的对象
        return BitmapFactory.decodeFile(path,options);
    }
    public static  Bitmap getScaleBitMap(String path , Activity activity){
        Point size =new Point();
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);
        return getScaledBitmap(path,size.x,size.y);
    }


}
