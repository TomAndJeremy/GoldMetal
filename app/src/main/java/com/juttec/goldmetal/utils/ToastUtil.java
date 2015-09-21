package com.juttec.goldmetal.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/9/21.
 */
public class ToastUtil {


    public static boolean isShow = true;


    public static void showShort(Context context,String msg){

        if(isShow)
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }


    public static void showLong(Context context,String msg){

        if(isShow)
            Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
}
