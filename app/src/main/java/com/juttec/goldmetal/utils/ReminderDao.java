package com.juttec.goldmetal.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.juttec.goldmetal.bean.ReminderFloatBeen;
import com.juttec.goldmetal.bean.ReminderPointBeen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremy on 2015/11/30.
 */
public class  ReminderDao {
    Context context;
    // 列定义
    private final String[] ORDER_COLUMNS_FLOATE = new String[]{"Id", "StockSymbol", "BasePrice", "FloatPrice"};
    private final String[] ORDER_COLUMNS_POINT = new String[]{"Id", "StockSymbol", "Operator", "Value"};

    ReminderDBHelper reminderDBHelper;

    public ReminderDao(Context context) {
        this.context = context;
        reminderDBHelper = new ReminderDBHelper(context);
    }

    /**
     * 插入
     *
     * @param contentValues
     * @param flag 1插入浮动提醒表，2插入点位提醒表
     */
    public void insert(ContentValues contentValues, int flag) {
        SQLiteDatabase db = null;
        String tableName = getTableName(flag);

        if (tableName == null) {
            ToastUtil.showShort(context, "插入失败");
            return;
        }

        //如果该浮动提醒的股票号已经存在则更新
        if (flag == 1 && isExist(db, contentValues.get("StockSymbol").toString())) {
            updata(contentValues);
            return;
        }
        //如果不存在就插入
        try {
            db = reminderDBHelper.getWritableDatabase();
            db.beginTransaction();
            db.insertOrThrow(tableName, null, contentValues);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
            ToastUtil.showShort(context, "保存失败");
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    //删除点位提醒
    public boolean deletePoint(String symblo,String operator,String value) {
        SQLiteDatabase db = null;
        try {
            db = reminderDBHelper.getWritableDatabase();
            db.beginTransaction();
            db.delete(reminderDBHelper.TABLE_NAME_POINT, "StockSymbol = ? AND  Operator=? AND Value=?", new String[]{symblo, operator, value});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();

            ToastUtil.showShort(context, "删除失败");
            return false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    //跟新浮动提醒的数据
    public void updata(ContentValues contentValues) {
        SQLiteDatabase db = null;
        String name = contentValues.get("StockSymbol").toString();
        try {
            db = reminderDBHelper.getWritableDatabase();
            db.beginTransaction();


            db.update(reminderDBHelper.TABLE_NAME_FLOAT,
                    contentValues,
                    "StockSymbol = ?",
                    new String[]{name});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showShort(context, "更新失败");
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

 //删除浮动提醒提醒
    public void deleteFloat(String stocksymbol) {
        SQLiteDatabase db = null;


        try {
            db = reminderDBHelper.getWritableDatabase();
            db.beginTransaction();

            db.delete(reminderDBHelper.TABLE_NAME_FLOAT, "StockSymbol = ?  ", new String[]{stocksymbol});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showShort(context, "删除失败");
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }


    /**
     * 根据用户Id查出 此用户Id下所有的浮动提醒数据
     * @return
     */
    public List<ReminderFloatBeen> getAllFloatDate() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<ReminderFloatBeen> reminderList = null;
        try {
            db = reminderDBHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(reminderDBHelper.TABLE_NAME_FLOAT, ORDER_COLUMNS_FLOATE, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                reminderList = new ArrayList<ReminderFloatBeen>();
                while (cursor.moveToNext()) {
                    reminderList.add(new ReminderFloatBeen(cursor));
                }

            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
            return reminderList;
        }
    }

    public List<ReminderPointBeen> getAllPointDate() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = reminderDBHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(reminderDBHelper.TABLE_NAME_POINT, ORDER_COLUMNS_POINT, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                List<ReminderPointBeen> reminderList = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    reminderList.add(new ReminderPointBeen(cursor));
                }
                return reminderList;
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }


    //判断浮动提醒股票是否已存在
    private boolean isExist(SQLiteDatabase db, String name) {
        Cursor cursor = null;
        db = reminderDBHelper.getReadableDatabase();
        // select * from Orders where CustomName = 'Bor'
        cursor = db.query(reminderDBHelper.TABLE_NAME_FLOAT,
                ORDER_COLUMNS_FLOATE,
                "StockSymbol = ?",
                new String[]{name},
                null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }

    }


    /**
     * 根据flag获得表名
     * @param flag 1表示浮动记录表，2表示点位记录表
     * @return
     */
    private String getTableName(int flag) {

        if (flag == 1) {
            return reminderDBHelper.TABLE_NAME_FLOAT;
        } else if (flag == 2) {
            return reminderDBHelper.TABLE_NAME_POINT;
        } else {

            return null;
        }

    }
}
