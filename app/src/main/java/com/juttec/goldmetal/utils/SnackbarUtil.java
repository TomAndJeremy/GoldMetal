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


    public static Snackbar snackbar;

    public static void showShort(Context context, String msg) {
        Snackbar.make(((Activity) context).getCurrentFocus(), msg, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }


    public static void showLong(Context context, String msg) {
        Snackbar.make(((Activity) context).getCurrentFocus(), msg, Snackbar.LENGTH_LONG).show();
    }
}
