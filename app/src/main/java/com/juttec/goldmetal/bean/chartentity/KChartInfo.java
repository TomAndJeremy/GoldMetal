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
