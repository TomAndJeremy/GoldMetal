package com.juttec.goldmetal.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/10/26.
 *
 * 时间工具类
 */
public class DateUtil {

    public static String formatDate(String strDate){

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date  = dateFormat.parse(strDate);

            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");

            strDate = format.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }
}
