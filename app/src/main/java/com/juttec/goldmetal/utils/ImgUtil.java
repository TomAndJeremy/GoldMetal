package com.juttec.goldmetal.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Administrator on 2015/9/24.
 *
 * 关于图片的工具类
 */
public class ImgUtil {

    // 根据路径获取图片 并进行缩放
    public static Bitmap getBitmap(String path, int reqWidth, int reqHeight) {
        // 先解析图片边框的大小
        BitmapFactory.Options ops = new BitmapFactory.Options();
        ops.inJustDecodeBounds = true;
        Bitmap bm = BitmapFactory.decodeFile(path, ops);
        ops.inSampleSize = 1;
        int height = ops.outHeight;
        int width = ops.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        ops.inSampleSize = inSampleSize;
        ops.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, ops);
        return bm;
    }



}