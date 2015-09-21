package com.juttec.goldmetal.utils;

import android.util.Log;

/**
 * Created by Administrator on 2015/9/21.
 */
public class LogUtil {

    public static boolean isDebug = true;


    public static String TAG = "tag";



    public static void d(String msg){

        if(isDebug)
            Log.d(TAG,msg);
    }



    public static void e(String msg){

        if(isDebug)
            Log.e(TAG, msg);
    }




    public static void d(String tag,String msg){

        if(isDebug)
            Log.d(tag, msg);
    }


}
