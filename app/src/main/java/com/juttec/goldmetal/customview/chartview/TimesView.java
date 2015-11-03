package com.juttec.goldmetal.customview.chartview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;


import com.juttec.goldmetal.bean.chartentity.TimesEntity;
import com.juttec.goldmetal.utils.DateUtil;

import java.text.DecimalFormat;
import java.util.List;

public class TimesView extends GridChart {
	private  int DATA_MAX_COUNT = 12 * 60;
	private List<TimesEntity.ResultEntity> timesList;

	private float uperBottom;
	private float uperHeight;
	private float lowerBottom;
	private float lowerHeight;
	private float dataSpacing;

	private double initialWeightedIndex;
	private float uperHalfHigh;
	//	private float lowerHigh;
	private float uperRate;
	private float lowerRate;

	private boolean showDetails;
	private float touchX;

	public TimesView(Context context) {
		super(context);
		init();
	}

	public TimesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TimesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		super.setShowLowerChartTabs(false);
		super.setShowTopTitles(false);

		timesList = null;
		uperBottom = 0;
		uperHeight = 0;
		lowerBottom = 0;
		lowerHeight = 0;
		dataSpacing = 0;

		initialWeightedIndex = 0;
		uperHalfHigh = 0;
//		lowerHigh = 0;
		uperRate = 0;
		lowerRate = 0;
		showDetails = false;
		touchX = 0;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		DEFAULT_LOWER_LATITUDE_NUM = 0;
		if (timesList == null || timesList.size() <= 0) {
			return;
		}
		uperBottom = UPER_CHART_BOTTOM - 2;
		uperHeight = getUperChartHeight() - 4;
		lowerBottom = getHeight() - 3;
		lowerHeight = getLowerChartHeight() - 2;
		dataSpacing = (getWidth() - 4) * 10.0f / 10.0f / DATA_MAX_COUNT;

		if (uperHalfHigh > 0) {
			uperRate = uperHeight / uperHalfHigh / 2.0f;
		}
//		if (lowerHigh > 0) {
//			lowerRate = lowerHeight / lowerHigh;
//		}

		// 绘制上部曲线及下部线条
		drawLines(canvas);

		// 绘制坐标标题
		drawTitles(canvas);

		// 绘制点击时的详细信息
		drawDetails(canvas);
	}

	private void drawDetails(Canvas canvas) {
		if (showDetails) {
			float width = getWidth();
			float left = 5.0f;
			float top = 4.0f;
			float right = 3.0f + 13f * DEFAULT_AXIS_TITLE_SIZE;
			float bottom = 7.0f + 4 * DEFAULT_AXIS_TITLE_SIZE;
			if (touchX < width / 2.0f) {
				right = width - 4.0f;
				left = width - 4.0f - 13.0f * DEFAULT_AXIS_TITLE_SIZE;
			}

			// 绘制点击线条
			Paint paint = new Paint();
			paint.setColor(Color.LTGRAY);
			paint.setStrokeWidth(3);
			paint.setAlpha(150);
			canvas.drawLine(touchX, 2.0f, touchX, UPER_CHART_BOTTOM, paint);
			canvas.drawLine(touchX, lowerBottom - lowerHeight, touchX, lowerBottom, paint);
			canvas.drawRect(left, top, right, bottom, paint);
			//绘制详情区域
			Paint borderPaint = new Paint();
			borderPaint.setColor(Color.WHITE);
			borderPaint.setStrokeWidth(2);
			canvas.drawLine(left, top, left, bottom, borderPaint);
			canvas.drawLine(left, top, right, top, borderPaint);
			canvas.drawLine(right, bottom, right, top, borderPaint);
			canvas.drawLine(right, bottom, left, bottom, borderPaint);

			//绘制详情文字
			Paint textPaint = new Paint();
			textPaint.setTextSize(DEFAULT_AXIS_TITLE_SIZE);
			textPaint.setColor(Color.WHITE);
			textPaint.setFakeBoldText(true);
			try {
				TimesEntity.ResultEntity fenshiData = timesList.get((int) ((touchX - 2) / dataSpacing));
				canvas.drawText("时间: " + fenshiData.getDate(), left + 1, top
						+ DEFAULT_AXIS_TITLE_SIZE, textPaint);

				canvas.drawText("价格:", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 2.0f, textPaint);
				double price = fenshiData.getAverageLine();
				if (price >= initialWeightedIndex) {
					textPaint.setColor(Color.RED);
				} else {
					textPaint.setColor(Color.GREEN);
				}
				canvas.drawText(new DecimalFormat("#.##").format(price), left + 1
								+ DEFAULT_AXIS_TITLE_SIZE * 2.5f, top + DEFAULT_AXIS_TITLE_SIZE * 2.0f,
						textPaint);

				textPaint.setColor(Color.WHITE);
				canvas.drawText("涨跌:", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 3.0f, textPaint);
				double change = (fenshiData.getAverageLine() - initialWeightedIndex)
						/ initialWeightedIndex;
				if (change >= 0) {
					textPaint.setColor(Color.RED);
				} else {
					textPaint.setColor(Color.GREEN);
				}
				canvas.drawText(new DecimalFormat("#.##%").format(change), left + 1
								+ DEFAULT_AXIS_TITLE_SIZE * 2.5f, top + DEFAULT_AXIS_TITLE_SIZE * 3.0f,
						textPaint);

				textPaint.setColor(Color.WHITE);
				canvas.drawText("成交:", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 4.0f, textPaint);
				textPaint.setColor(Color.YELLOW);
				canvas.drawText(String.valueOf(fenshiData.getVolume()), left + 1
								+ DEFAULT_AXIS_TITLE_SIZE * 2.5f, top + DEFAULT_AXIS_TITLE_SIZE * 4.0f,
						textPaint);

			} catch (Exception e) {
				canvas.drawText("时间: --", left + 1, top + DEFAULT_AXIS_TITLE_SIZE, textPaint);
				canvas.drawText("价格: --", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 2.0f, textPaint);
				canvas.drawText("涨跌: --", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 3.0f, textPaint);
				canvas.drawText("成交: --", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 4.0f, textPaint);
			}
		}

	}

	private void drawTitles(Canvas canvas) {
		// 绘制Y轴titles
		float viewWidth = getWidth();
		Paint paint = new Paint();
		paint.setTextSize(DEFAULT_AXIS_TITLE_SIZE);

		paint.setColor(Color.GREEN);
		canvas.drawText(new DecimalFormat("#.##").format(initialWeightedIndex - uperHalfHigh), 2,
				uperBottom, paint);
		String text = new DecimalFormat("#.##%").format(-uperHalfHigh / initialWeightedIndex);
		canvas.drawText(text, viewWidth - 5 - text.length() * DEFAULT_AXIS_TITLE_SIZE / 2.0f,
				uperBottom, paint);

		canvas.drawText(
				new DecimalFormat("#.##").format(initialWeightedIndex - uperHalfHigh * 1.0f / 3), 2,
				uperBottom - getLatitudeSpacing(), paint);
		text = new DecimalFormat("#.##%").format(-uperHalfHigh * 2.0f/3 / initialWeightedIndex);
		canvas.drawText(text, viewWidth - 5 - text.length() * DEFAULT_AXIS_TITLE_SIZE / 2.0f,
				uperBottom - getLatitudeSpacing(), paint);


		canvas.drawText(
				new DecimalFormat("#.##").format(initialWeightedIndex - uperHalfHigh * 2.0f/3), 2,
				uperBottom - getLatitudeSpacing()*2, paint);
		text = new DecimalFormat("#.##%").format(-uperHalfHigh * 1.0f/3 / initialWeightedIndex);
		canvas.drawText(text, viewWidth - 5 - text.length() * DEFAULT_AXIS_TITLE_SIZE / 2.0f,
				uperBottom - getLatitudeSpacing()*2, paint);

		paint.setColor(Color.WHITE);
		canvas.drawText(new DecimalFormat("#.##").format(initialWeightedIndex), 2, uperBottom
				- getLatitudeSpacing() * 3, paint);
		text = "0.00%";
		canvas.drawText(text, viewWidth - 6 - text.length() * DEFAULT_AXIS_TITLE_SIZE / 2.0f,
				uperBottom - getLatitudeSpacing() * 3, paint);

		paint.setColor(Color.RED);
		canvas.drawText(
				new DecimalFormat("#.##").format(uperHalfHigh * 1.0f/3 + initialWeightedIndex), 2,
				uperBottom - getLatitudeSpacing() * 4, paint);
		text = new DecimalFormat("#.##%").format(uperHalfHigh * 1.0f/3 / initialWeightedIndex);
		canvas.drawText(text, viewWidth - 6 - text.length() * DEFAULT_AXIS_TITLE_SIZE / 2.0f,
				uperBottom - getLatitudeSpacing() * 4, paint);

		canvas.drawText(
				new DecimalFormat("#.##").format(uperHalfHigh * 2.0f/3 + initialWeightedIndex), 2,
				uperBottom - getLatitudeSpacing() * 5, paint);
		text = new DecimalFormat("#.##%").format(uperHalfHigh * 2.0f/3 / initialWeightedIndex);
		canvas.drawText(text, viewWidth - 6 - text.length() * DEFAULT_AXIS_TITLE_SIZE / 2.0f,
				uperBottom - getLatitudeSpacing() * 5, paint);

		canvas.drawText(new DecimalFormat("#.##").format(uperHalfHigh + initialWeightedIndex), 2,
				DEFAULT_AXIS_TITLE_SIZE, paint);
		text = new DecimalFormat("#.##%").format(uperHalfHigh / initialWeightedIndex);
		canvas.drawText(text, viewWidth - 6 - text.length() * DEFAULT_AXIS_TITLE_SIZE / 2.0f,
				DEFAULT_AXIS_TITLE_SIZE, paint);

		// 绘制X轴Titles


		paint.setColor(Color.WHITE);
		try {
			int t1 = (int)(super.getLongitudeSpacing()/ dataSpacing);
			if(t1<=timesList.size()){
                canvas.drawText(DateUtil.formatDate(timesList.get(t1).getDate()), 2+super.getLongitudeSpacing()-3.0f * DEFAULT_AXIS_TITLE_SIZE,
                        uperBottom + DEFAULT_AXIS_TITLE_SIZE, paint);
            }


			int t2 = (int)(super.getLongitudeSpacing()*2 / dataSpacing);
			if(t2<=timesList.size()){
                canvas.drawText(DateUtil.formatDate(timesList.get(t2).getDate()), 2+super.getLongitudeSpacing()*2-3.0f * DEFAULT_AXIS_TITLE_SIZE,
                        uperBottom + DEFAULT_AXIS_TITLE_SIZE, paint);
            }


			int t3 = (int)(super.getLongitudeSpacing()*3 / dataSpacing);
			if(t3<=timesList.size()){
                canvas.drawText(DateUtil.formatDate(timesList.get(t3).getDate()), 2+super.getLongitudeSpacing()*3-3.0f * DEFAULT_AXIS_TITLE_SIZE,
                        uperBottom + DEFAULT_AXIS_TITLE_SIZE, paint);
            }

			int t4 = (int)(super.getLongitudeSpacing()*4 / dataSpacing);
			if(t4<=timesList.size()){
                canvas.drawText(DateUtil.formatDate(timesList.get(t4).getDate()), 2+super.getLongitudeSpacing()*4-3.0f * DEFAULT_AXIS_TITLE_SIZE,
                        uperBottom + DEFAULT_AXIS_TITLE_SIZE, paint);
            }

			int t5 = (int)(super.getLongitudeSpacing()*5 / dataSpacing);
			if(t5<=timesList.size()){
                canvas.drawText(DateUtil.formatDate(timesList.get(t5).getDate()), 2+super.getLongitudeSpacing()*5-3.0f * DEFAULT_AXIS_TITLE_SIZE,
                        uperBottom + DEFAULT_AXIS_TITLE_SIZE, paint);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
//		canvas.drawText("11:30/13:00", viewWidth / 2.0f - DEFAULT_AXIS_TITLE_SIZE * 2.5f,
//				uperBottom + DEFAULT_AXIS_TITLE_SIZE, paint);
//		canvas.drawText("15:00", viewWidth - 2 - DEFAULT_AXIS_TITLE_SIZE * 2.5f, uperBottom
//				+ DEFAULT_AXIS_TITLE_SIZE, paint);
	}

	private void drawLines(Canvas canvas) {
		float x = 0;
		float uperWhiteY = 0;
		float uperYellowY = 0;
		Paint paint = new Paint();
		for (int i = 0; i < timesList.size() && i < DATA_MAX_COUNT; i++) {
			TimesEntity.ResultEntity fenshiData = timesList.get(i);

			// 绘制上部表中曲线
			float endWhiteY = (float) (uperBottom - (fenshiData.getTimeTrend()
					+ uperHalfHigh - initialWeightedIndex)
					* uperRate);
			float endYelloY = (float) (uperBottom - (fenshiData.getAverageLine() + uperHalfHigh - initialWeightedIndex)
					* uperRate);

			if (i != 0) {
				paint.setColor(Color.WHITE);
				canvas.drawLine(x, uperWhiteY, 3 + dataSpacing * i, endWhiteY, paint);
				paint.setColor(Color.YELLOW);
				canvas.drawLine(x, uperYellowY, 3 + dataSpacing * i, endYelloY, paint);
			}

			x = 3 + dataSpacing * i;
			uperWhiteY = endWhiteY;
			uperYellowY = endYelloY;

			// 绘制下部表内数据线
//			int buy = fenshiData.getResult().get(0).getBuy();
//			if (i <= 0) {
//				paint.setColor(Color.RED);
//			} else if (fenshiData.getResult().get(0).getTimeTrend() >= timesList.get(i - 1)
//					.getResult().get(0).getTimeTrend()) {
//				paint.setColor(Color.RED);
//			} else {
//				paint.setColor(Color.GREEN);
//			}
//			canvas.drawLine(x, lowerBottom, x, lowerBottom - buy * lowerRate, paint);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				touchX = event.getRawX();
				if (touchX < 2 || touchX > getWidth() - 2) {
					return false;
				}
				showDetails = true;
				postInvalidate();
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_OUTSIDE:
				showDetails = false;
				break;

			default:
				break;
		}

		return true;
	}

	public void setTimesList(List<TimesEntity.ResultEntity> timesList) {
		if (timesList == null || timesList.size() <= 0) {
			return;
		}
		this.timesList = timesList;
		DATA_MAX_COUNT = timesList.size()+60;

		TimesEntity.ResultEntity fenshiData = timesList.get(0);
		double weightedIndex = fenshiData.getAverageLine();
		double nonWeightedIndex = fenshiData.getTimeTrend();
//		int buy = fenshiData.getBuy();
		initialWeightedIndex = weightedIndex;
//		lowerHigh = buy;
		for (int i = 1; i < timesList.size() && i < DATA_MAX_COUNT; i++) {
			fenshiData = timesList.get(i);
			weightedIndex = fenshiData.getAverageLine();
			nonWeightedIndex = fenshiData.getTimeTrend();
//			buy = fenshiData.getBuy();

			uperHalfHigh = (float) (uperHalfHigh > Math
					.abs(nonWeightedIndex - initialWeightedIndex) ? uperHalfHigh : Math
					.abs(nonWeightedIndex - initialWeightedIndex));

//			lowerHigh = lowerHigh > buy ? lowerHigh : buy;
		}
		postInvalidate();

	}
}
