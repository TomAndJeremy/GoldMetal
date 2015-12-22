package com.juttec.goldmetal.utils;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jeremy on 2015/11/30.
 */
public class ReminderDBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String  DB_NAME = "remider.db";
    public static final String TABLE_NAME_FLOAT = "remind_float";
    public static final String TABLE_NAME_POINT = "remind_point";


    public ReminderDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public ReminderDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public ReminderDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建表，列为id，股票号，基准价，浮动值
        String sql_float = "create table if not exists " + TABLE_NAME_FLOAT + " (Id integer primary key autoincrement, UserId text, StockSymbol text, BasePrice double, FloatPrice double)";
        //创建表，列为id，股票号，操作符（大于/小于），点位值
        String sql_point = "create table if not exists " + TABLE_NAME_POINT + " (Id integer primary key autoincrement, UserId text, StockSymbol text, Operator text,Value double)";
        sqLiteDatabase.execSQL(sql_float);
        sqLiteDatabase.execSQL(sql_point);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql_float = "DROP TABLE IF EXISTS " + TABLE_NAME_FLOAT;
        String sql_point = "DROP TABLE IF EXISTS " + TABLE_NAME_POINT;
        sqLiteDatabase.execSQL(sql_point);
        sqLiteDatabase.execSQL(sql_float);
        onCreate(sqLiteDatabase);
    }

}
