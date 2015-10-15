package com.juttec.goldmetal.bean;

/**
 * Created by Administrator on 2015/8/4.
 */
public class MarketFormInfo {
    private String type,high,low,sale,buy,increase,scale,riseOrFall;

    public MarketFormInfo() {
    }

    public MarketFormInfo(String type, String high, String low, String sale, String buy, String increase, String scale, String riseOrFall) {
        this.type = type;
        this.high = high;
        this.low = low;
        this.sale = sale;
        this.buy = buy;
        this.increase = increase;
        this.scale = scale;
        this.riseOrFall = riseOrFall;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getSale() {
        return sale;
    }

    public void setSale(String sale) {
        this.sale = sale;
    }

    public String getBuy() {
        return buy;
    }

    public void setBuy(String buy) {
        this.buy = buy;
    }

    public String getIncrease() {
        return increase;
    }

    public void setIncrease(String increase) {
        this.increase = increase;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getRiseOrFall() {
        return riseOrFall;
    }

    public void setRiseOrFall(String riseOrFall) {
        this.riseOrFall = riseOrFall;
    }
}
