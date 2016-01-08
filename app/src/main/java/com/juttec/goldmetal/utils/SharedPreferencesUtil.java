package com.juttec.goldmetal.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2015/9/25.
 *
 * 用于存储某些信息的  工具类
 */
public class SharedPreferencesUtil {
    //isFirst 判断用户是否是第一次使用  boolean  默认为：false
    //isLogining 判断用户是否是登录状态  boolean 默认为：false   登录成功之后为：true 直到用户点击退出登录将值置为：false


    //username          手机号  String
    //pwd               密码   String
    //userId            用户id   String
    //goldMetalId       掌金ID   String
    //realName          真实姓名   String
    //userNickName       用户昵称   String
    //userQQ             用户QQ   String
    //userPhoto          用户头像(地址)   String
    //noteWarn          短信提醒 0：未开启，1开启   String


    //remember 是否记住密码  boolean

    //isScreenLight 是否设置屏幕常亮  boolean
    //isShowRefreshTiem  是否显示刷新时间 boolean
    //refreshTime  刷新时间  int












        /**
         * 保存在手机里面的文件名
         */
        private static final String FILE_NAME = "share_date";


        /**
         * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
         * @param context
         * @param key
         * @param object
         */
        public static void setParam(Context context , String key, Object object){

            String type = object.getClass().getSimpleName();
            SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();

            if("String".equals(type)){
                editor.putString(key, (String)object);
            }
            else if("Integer".equals(type)){
                editor.putInt(key, (Integer)object);
            }
            else if("Boolean".equals(type)){
                editor.putBoolean(key, (Boolean)object);
            }
            else if("Float".equals(type)){
                editor.putFloat(key, (Float)object);
            }
            else if("Long".equals(type)){
                editor.putLong(key, (Long)object);
            }

            editor.commit();
        }


        /**
         * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
         * @param context
         * @param key
         * @param defaultObject
         * @return
         */
        public static Object getParam(Context context , String key, Object defaultObject){
            String type = defaultObject.getClass().getSimpleName();
            SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

            if("String".equals(type)){
                return sp.getString(key, (String)defaultObject);
            }
            else if("Integer".equals(type)){
                return sp.getInt(key, (Integer)defaultObject);
            }
            else if("Boolean".equals(type)){
                return sp.getBoolean(key, (Boolean)defaultObject);
            }
            else if("Float".equals(type)){
                return sp.getFloat(key, (Float)defaultObject);
            }
            else if("Long".equals(type)){
                return sp.getLong(key, (Long)defaultObject);
            }

            return null;
        }




    /**
     * 根据key   清除存储的 某一个值
     * @param context
     * @param key
     */
    public static void clearParam(Context context , String key){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }


}
