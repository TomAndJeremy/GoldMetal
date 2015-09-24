package com.juttec.goldmetal.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.juttec.goldmetal.activity.PublishTopicActivity;

/**
 * Created by Jeremy on 2015/9/21.
 */
public class SnackbarUtil {


    public static Snackbar Short(Context context, String msg, String action, View.OnClickListener onClickListener) {
        hideInput(context, ((Activity) context).getCurrentFocus());
        return Snackbar.make(((Activity) context).getCurrentFocus(), msg, Snackbar.LENGTH_SHORT);


    }


    public static Snackbar Long(Context context, String msg) {
        hideInput(context, ((Activity) context).getCurrentFocus());
        return Snackbar.make(((Activity) context).getCurrentFocus(), msg, Snackbar.LENGTH_LONG);


    }

    public static void showShort(Context context, String msg) {
        hideInput(context, ((Activity) context).getCurrentFocus());
        Snackbar.make(((Activity) context).getCurrentFocus(), msg, Snackbar.LENGTH_SHORT).show();


    }


    public static void showLong(Context context, String msg) {
        hideInput(context, ((Activity) context).getCurrentFocus());
        Snackbar.make(((Activity) context).getCurrentFocus(), msg, Snackbar.LENGTH_LONG).show();


    }

    private static void hideInput(Context context, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
