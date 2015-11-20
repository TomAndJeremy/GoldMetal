package com.juttec.goldmetal.bean;

/**
 * Created by Administrator on 2015/11/18.
 *
 * 搜索到的股票实体类    包含拼音，字母，代码，名称
 */
public class StockSearchBean {

   // {"pinyin":"meiyuanyoulianxu","letter":"myylx","symbol":"NECLA0","name":"美原油连续"}

    String pinyin;
    String letter;
    String symbol;
    String name;

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
