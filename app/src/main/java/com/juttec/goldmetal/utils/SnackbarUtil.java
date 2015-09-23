package com.juttec.goldmetal.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.juttec.goldmetal.activity.PublishTopicActivity;

/**
 * Created by Jeremy on 2015/9/21.
 */
public class SnackbarUtil {



    public static Snackbar Short(Context context, String msg,String action,View.OnClickListener onClickListener) {
        return Snackbar.make(((Activity) context).getCurrentFocus(), msg, Snackbar.LENGTH_SHORT);


    }


    public static Snackbar Long(Context context, String msg) {
        return   Snackbar.make(((Activity) context).getCurrentFocus(), msg, Snackbar.LENGTH_LONG);


    }

    public static void showShort(Context context, String msg) {
        Snackbar.make(((Activity) context).getCurrentFocus(), msg, Snackbar.LENGTH_SHORT).show();


    }


    public static void showLong(Context context, String msg) {
        Snackbar.make(((Activity) context).getCurrentFocus(), msg, Snackbar.LENGTH_LONG).show();


    }
}
