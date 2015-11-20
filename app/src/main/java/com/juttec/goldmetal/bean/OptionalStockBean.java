package com.juttec.goldmetal.bean;

import io.realm.RealmObject;

/**
 * Created by Administrator on 2015/11/19.
 *
 * 自选股  实体类
 */
public class OptionalStockBean extends RealmObject{

    private String symbol;//自选股对应的 股票代码

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
