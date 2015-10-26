package com.juttec.goldmetal.bean.chartentity;

import java.util.List;

/**
 * 分时图的每分钟数据 实体
 * 
 *
 */
public class TimesEntity {
	private List<ResultEntity> result;

	public TimesEntity() {
	}

	public TimesEntity(List<ResultEntity> result) {

		this.result = result;
	}

	public List<ResultEntity> getResult() {
		return result;
	}

	public void setResult(List<ResultEntity> result) {
		this.result = result;
	}



	/**
	 * Symbol : NECLI0
	 * Name : 美原油指数
	 * Date : 2015-08-20 06:01:00
	 * LastClose : 42.7
	 * TimeTrend : 42.705
	 * AverageLine : 42.705
	 * Open_Int : 1333580
	 * Volume : 121
	 * Amount : 0
	 */

//	struct _data_kline {
//		Symbol          char(16),//代码，16字节
//		Name            char(32),//名字，32字节
//		datetime        timestamp,//交易时间，4字节
//		LastClose       float,//昨日收盘价 4字节
//		TimeTrend       float,//高开收低价的均价 白线 4字节
//		AverageLine     float,//均价 总交易额除总交易量 黄线 4字节
//		Open_Int        float,//持仓量 4字节
//		volume          float,//总成交量 4字节
//		amount          float//总成交额 4字节
//	}data_kline;

	public class ResultEntity {
		private String Symbol;
		private String Name;
		private String Date;
		private String LastClose;
		private double TimeTrend;
		private double AverageLine;
		private String Open_Int;
		private String Volume;
		private String Amount;



		public void setSymbol(String Symbol) {
			this.Symbol = Symbol;
		}

		public void setName(String Name) {
			this.Name = Name;
		}

		public void setDate(String Date) {
			this.Date = Date;
		}

		public void setLastClose(String LastClose) {
			this.LastClose = LastClose;
		}

		public void setTimeTrend(double TimeTrend) {
			this.TimeTrend = TimeTrend;
		}

		public void setAverageLine(double AverageLine) {
			this.AverageLine = AverageLine;
		}

		public void setOpen_Int(String Open_Int) {
			this.Open_Int = Open_Int;
		}

		public void setVolume(String Volume) {
			this.Volume = Volume;
		}

		public void setAmount(String Amount) {
			this.Amount = Amount;
		}

		public String getSymbol() {
			return Symbol;
		}

		public String getName() {
			return Name;
		}

		public String getDate() {
			return Date;
		}

		public String getLastClose() {
			return LastClose;
		}

		public double getTimeTrend() {
			return TimeTrend;
		}

		public double getAverageLine() {
			return AverageLine;
		}

		public String getOpen_Int() {
			return Open_Int;
		}

		public String getVolume() {
			return Volume;
		}

		public String getAmount() {
			return Amount;
		}
	}

}
