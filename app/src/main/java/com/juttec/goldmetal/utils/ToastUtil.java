package com.juttec.goldmetal.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/9/21.
 */
public class ToastUtil {


    public static boolean isShow = true;


    private static Toast toast = null;

    public static void showShort(Context context,String msg){

        if(isShow){

            if (toast == null) {
                toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            } else {
                toast.setText(msg);
            }
            toast.show();

        }

    }


    public static void showLong(Context context,String msg){

        if(isShow)
        {
            if (toast == null) {
                toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            } else {
                toast.setText(msg);
            }
            toast.show();
        }
    }
}
