package com.juttec.goldmetal.utils;

import android.content.Context;
import android.util.Log;

/**
 * Created by Administrator on 2015/9/21.
 */
public class LogUtil {

    public static boolean isDebug = true;


    public static String TAG = "tag";


    public static void d(String msg) {

        if (isDebug)
            Log.d(TAG, msg);
    }


    public static void e(String msg) {

        if (isDebug)
            Log.e(TAG, msg);
    }


    public static void d(String tag, String msg) {

        if (isDebug)
            Log.d(tag, msg);
    }

    public static void e(Context context, int lineNUm, String msg) {

        String s = context.getClass().toString();

        if (isDebug)
            Log.e(TAG, "in " + s.substring(s.lastIndexOf(".")+1)+ " " + lineNUm + ":" + msg);
    }

    public static void d(Context context, int lineNUm, String msg) {
        String s = context.getClass().toString();
        if (isDebug)
            Log.d(TAG, "in " + s.substring(s.lastIndexOf(".")+1)+ " " + lineNUm + ":" + msg);
    }

}
