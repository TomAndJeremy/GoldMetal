package com.juttec.goldmetal.bean;

import android.database.Cursor;

/**
 * Created by jeremy on 2015/11/30.
 */
public class ReminderPointBeen {
    private int Id;
    private String stock;
    private String Operator;
    private String Value;

    public ReminderPointBeen(Cursor cursor) {
        Id = cursor.getInt(cursor.getColumnIndex("Id"));
        stock = cursor.getString(cursor.getColumnIndex("StockSymbol"));
        Operator = cursor.getString(cursor.getColumnIndex("Operator"));
        Value = cursor.getString(cursor.getColumnIndex("Value"));
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


    public String getOperator() {
        return Operator;
    }

    public String getValue() {
        return Value;
    }

    public void setStock(String stock) {

        this.stock = stock;
    }

    public void setOperator(String operator) {
        Operator = operator;
    }

    public void setValue(String value) {
        Value = value;
    }
}
