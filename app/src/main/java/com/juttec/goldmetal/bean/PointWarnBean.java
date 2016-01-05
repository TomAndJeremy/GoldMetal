package com.juttec.goldmetal.bean;

/**
 * Created by Administrator on 2015/12/31.
 */
public class PointWarnBean {

    private String  logicOperator;//操作符
    private String  newestPrice;//设定值
    private String  pointWarnId;//提醒值id

    public String getPointWarnId() {
        return pointWarnId;
    }

    public void setPointWarnId(String pointWarnId) {
        this.pointWarnId = pointWarnId;
    }

    public String getNewestPrice() {
        return newestPrice;
    }

    public void setNewestPrice(String newestPrice) {
        this.newestPrice = newestPrice;
    }

    public String getLogicOperator() {
        return logicOperator;
    }

    public void setLogicOperator(String logicOperator) {
        this.logicOperator = logicOperator;
    }
}
