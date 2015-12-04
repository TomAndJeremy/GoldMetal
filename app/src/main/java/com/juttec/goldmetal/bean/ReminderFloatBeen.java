package com.juttec.goldmetal.bean;

import android.database.Cursor;

/**
 * Created by jeremy on 2015/11/30.
 */
public class ReminderFloatBeen {
    private int Id;
    private String stock;
    private String BasePrice;
    private String FloatPrice;

    public ReminderFloatBeen(Cursor cursor) {
        Id = cursor.getInt(cursor.getColumnIndex("Id"));
        stock = cursor.getString(cursor.getColumnIndex("StockSymbol"));
        BasePrice = cursor.getString(cursor.getColumnIndex("BasePrice"));
        FloatPrice = cursor.getString(cursor.getColumnIndex("FloatPrice"));
    }

    public void setId(int id) {
        Id = id;
    }

    public int getId() {

        return Id;
    }


    public String getStock() {
        return stock;
    }

    public String getBasePrice() {
        return BasePrice;
    }

    public String getFloatPrice() {
        return FloatPrice;
    }


    public void setStock(String stock) {

        this.stock = stock;
    }

    public void setBasePrice(String basePrice) {
        BasePrice = basePrice;
    }

    public void setFloatPrice(String floatPrice) {
        FloatPrice = floatPrice;
    }

}
