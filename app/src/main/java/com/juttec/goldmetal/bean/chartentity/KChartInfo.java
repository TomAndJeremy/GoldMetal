package com.juttec.goldmetal.bean.chartentity;

import java.util.List;

/**
 *
 */
public class KChartInfo {
    private List<ResultEntity> result;

    public KChartInfo() {
    }

    public KChartInfo(List<ResultEntity> result) {
        this.result = result;
    }

    public List<ResultEntity> getResult() {
        return result;
    }

    public void setResult(List<ResultEntity> result) {
        this.result = result;
    }

    /**
     * Date : 2015-08-31 09:00:00
     * NewPrice : 1130.74
     * Open : 1132.92
     * High : 1134.19
     * Low : 1130.07
     */



//    struct _data_kline {
//        symbol     char(16),//代码，16字节
//        name       char(32),//名字，32字节
//        datetime   timestamp,//交易时间，4字节
//        open       float,//开盘价 4字节
//        low        float,//最低价 4字节
//        high       float,//最高价 4字节
//        close      float,//收盘价 4字节
//        volume     float,//总成交量 4字节
//        amount     float//总成交额 4字节
//    }data_kline;

    public class ResultEntity {
        private String Date;
        private String Close;
        private String Open;
        private String High;
        private String Low;

        public void setDate(String Date) {
            this.Date = Date;
        }

        public void setClose(String Close) {
            this.Close = Close;
        }

        public void setOpen(String Open) {
            this.Open = Open;
        }

        public void setHigh(String High) {
            this.High = High;
        }

        public void setLow(String Low) {
            this.Low = Low;
        }

        public String getDate() {
            return Date;
        }

        public String getClose() {
            return Close;
        }

        public String getOpen() {
            return Open;
        }

        public String getHigh() {
            return High;
        }

        public String getLow() {
            return Low;
        }
    }

}
