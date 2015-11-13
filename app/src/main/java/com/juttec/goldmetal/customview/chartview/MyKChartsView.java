package com.juttec.goldmetal.customview.chartview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;

import com.juttec.goldmetal.bean.chartentity.KChartInfo;
import com.juttec.goldmetal.bean.chartentity.KDJEntity;
import com.juttec.goldmetal.bean.chartentity.MACDEntity;
import com.juttec.goldmetal.bean.chartentity.MALineEntity;
import com.juttec.goldmetal.bean.chartentity.RSIEntity;
import com.juttec.goldmetal.utils.DateUtil;
import com.juttec.goldmetal.utils.LogUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyKChartsView extends GridChart /*implements GridChart.OnTabClickListener*/ {


    /**
     * 触摸模式
     */
    private static int TOUCH_MODE;
    private final static int NONE = 0;
    private final static int DOWN = 1;
    private final static int MOVE = 2;
    private final static int ZOOM = 3;

    /**
     * 默认Y轴字体颜色
     **/
    private static final int DEFAULT_AXIS_Y_TITLE_COLOR = Color.YELLOW;

    /**
     * 默认X轴字体颜色
     **/
    private static final int DEFAULT_AXIS_X_TITLE_COLOR = Color.WHITE;

    /**
     * 显示的最小Candle数
     */
    private final static int MIN_CANDLE_NUM = 10;

    /**
     * 默认显示的Candle数
     */
    private final static int DEFAULT_CANDLE_NUM = 50;

    /**
     * 最小可识别的移动距离
     */
    private final static int MIN_MOVE_DISTANCE = 15;

    /**
     * Candle宽度
     */
    private double mCandleWidth;

    /**
     * 触摸点
     */
    private float mStartX;
    private float mStartY;

    /**
     * OHLC数据
     */
    private List<KChartInfo.ResultEntity> mOHLCData;

    /**
     * 显示的OHLC数据起始位置
     */
    private int mDataStartIndext;

    /**
     * 显示的OHLC数据个数
     */
    private int mShowDataNum;

    /**
     * 是否显示蜡烛详情
     */
    private boolean showDetails;

    /**
     * 当前数据的最大最小值
     */

    private double mMaxPrice;
    private double mMinPrice;
    /**
     * MA数据
     */
    private List<MALineEntity> MALineData;

    /**
     * SMA数据
     */
    private List<MALineEntity> SMALineData;

    private String mTabTitle;
    private String mUpTitle;


    /**
     * 下部表的数据
     **/
    MACDEntity mMACDData;
    KDJEntity mKDJData;
    RSIEntity mRSIData;

    public MyKChartsView(Context context) {
        super(context);
        init();
    }

    public MyKChartsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyKChartsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setTabTitle(String mTabTitle) {
        this.mTabTitle = mTabTitle;
    }

    public void setUpTitle(String mUpTitle) {
        this.mUpTitle = mUpTitle;
    }

    private void init() {

        mShowDataNum = DEFAULT_CANDLE_NUM;
        mDataStartIndext = 0;
        showDetails = false;
        mMaxPrice = -1;
        mMinPrice = -1;


        mOHLCData = new ArrayList<KChartInfo.ResultEntity>();
        mMACDData = new MACDEntity(null);
        mKDJData = new KDJEntity(null);
        mRSIData = new RSIEntity(null);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DEFAULT_LOWER_LATITUDE_NUM = 1;
        if (mOHLCData == null || mOHLCData.size() <= 0) {
            return;
        }


        // drawUpperRegion(canvas);
        drawLowerRegion(canvas);
        // drawTitles(canvas);
        // drawCandleDetails(canvas);
    }

    private void drawLowerRegion(Canvas canvas) {
        float lowertop = LOWER_CHART_TOP + 1;
        float lowerHight = getHeight() - lowertop - 4;
        float viewWidth = getWidth();


        Paint whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        Paint yellowPaint = new Paint();
        yellowPaint.setColor(Color.YELLOW);
        Paint magentaPaint = new Paint();
        magentaPaint.setColor(Color.MAGENTA);

        Paint textPaint = new Paint();
        textPaint.setColor(DEFAULT_AXIS_Y_TITLE_COLOR);
        textPaint.setTextSize(DEFAULT_AXIS_TITLE_SIZE);
        int width = getWidth();
        mCandleWidth = (width - 4) / 10.0 * 10.0 / mShowDataNum;
        if (mTabTitle.trim().equalsIgnoreCase("MACD")) {

            List<Double> MACD = mMACDData.getMACD();
            List<Double> DEA = mMACDData.getDEA();
            List<Double> DIF = mMACDData.getDIF();

            double low = DEA.get(mDataStartIndext);
            double high = low;
            double rate = 0.0;
            for (int i = mDataStartIndext; i < mDataStartIndext + mShowDataNum && i < MACD.size(); i++) {
                low = low < MACD.get(i) ? low : MACD.get(i);
                low = low < DEA.get(i) ? low : DEA.get(i);
                low = low < DIF.get(i) ? low : DIF.get(i);

                high = high > MACD.get(i) ? high : MACD.get(i);
                high = high > DEA.get(i) ? high : DEA.get(i);
                high = high > DIF.get(i) ? high : DIF.get(i);
            }
            rate = lowerHight / (high - low);

            Paint paint = new Paint();
            float zero = (float) (high * rate) + lowertop;
            if (zero < lowertop) {
                zero = lowertop;
            }
            // 绘制双线
            float dea = 0.0f;
            float dif = 0.0f;
            for (int i = mDataStartIndext; i < mDataStartIndext + mShowDataNum && i < MACD.size(); i++) {
                // 绘制矩形
                if (MACD.get(i) >= 0.0) {
                    paint.setColor(Color.RED);
                    float top = (float) ((high - MACD.get(i)) * rate) + lowertop;
                    if (zero - top < 0.55f) {
                        canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
                                * (i + 1 - mDataStartIndext), zero, viewWidth - 2
                                - (float) mCandleWidth * (i - mDataStartIndext), zero, paint);
                    } else {
                        canvas.drawRect(viewWidth - 1 - (float) mCandleWidth
                                * (i + 1 - mDataStartIndext), top, viewWidth - 2
                                - (float) mCandleWidth * (i - mDataStartIndext), zero, paint);
                    }

                } else {
                    paint.setColor(Color.GREEN);
                    float bottom = (float) ((high - MACD.get(i)) * rate) + lowertop;

                    if (bottom - zero < 0.55f) {
                        canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
                                * (i + 1 - mDataStartIndext), zero, viewWidth - 2
                                - (float) mCandleWidth * (i - mDataStartIndext), zero, paint);
                    } else {
                        canvas.drawRect(viewWidth - 1 - (float) mCandleWidth
                                * (i + 1 - mDataStartIndext), zero, viewWidth - 2
                                - (float) mCandleWidth * (i - mDataStartIndext), bottom, paint);
                    }
                }

                if (i != mDataStartIndext) {
                    canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
                                    * (i + 1 - mDataStartIndext) + (float) mCandleWidth / 2,
                            (float) ((high - DEA.get(i)) * rate) + lowertop, viewWidth - 2
                                    - (float) mCandleWidth * (i - mDataStartIndext)
                                    + (float) mCandleWidth / 2, dea, whitePaint);

                    canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
                                    * (i + 1 - mDataStartIndext) + (float) mCandleWidth / 2,
                            (float) ((high - DIF.get(i)) * rate) + lowertop, viewWidth - 2
                                    - (float) mCandleWidth * (i - mDataStartIndext)
                                    + (float) mCandleWidth / 2, dif, yellowPaint);
                }
                dea = (float) ((high - DEA.get(i)) * rate) + lowertop;
                dif = (float) ((high - DIF.get(i)) * rate) + lowertop;
            }

            canvas.drawText(new DecimalFormat("#.##").format(high), 2, lowertop
                    + DEFAULT_AXIS_TITLE_SIZE - 2, textPaint);
            canvas.drawText(new DecimalFormat("#.##").format((high + low) / 2), 2, lowertop
                    + lowerHight / 2 + DEFAULT_AXIS_TITLE_SIZE - 2, textPaint);

            canvas.drawText(new DecimalFormat("#.##").format(low), 2, lowertop + lowerHight,
                    textPaint);

        } else if (mTabTitle.trim().equalsIgnoreCase("KDJ")) {
            List<Double> Ks = mKDJData.getK();
            List<Double> Ds = mKDJData.getD();
            List<Double> Js = mKDJData.getJ();

            double low = Ks.get(mDataStartIndext);
            double high = low;
            double rate = 0.0;
            for (int i = mDataStartIndext; i < mDataStartIndext + mShowDataNum && i < Ks.size(); i++) {
                low = low < Ks.get(i) ? low : Ks.get(i);
                low = low < Ds.get(i) ? low : Ds.get(i);
                low = low < Js.get(i) ? low : Js.get(i);

                high = high > Ks.get(i) ? high : Ks.get(i);
                high = high > Ds.get(i) ? high : Ds.get(i);
                high = high > Js.get(i) ? high : Js.get(i);
            }
            rate = lowerHight / (high - low);

            // 绘制白、黄、紫线
            float k = 0.0f;
            float d = 0.0f;
            float j = 0.0f;
            for (int i = mDataStartIndext; i < mDataStartIndext + mShowDataNum && i < Ks.size(); i++) {

                if (i != mDataStartIndext) {
                    canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
                                    * (i + 1 - mDataStartIndext) + (float) mCandleWidth / 2,
                            (float) ((high - Ks.get(i)) * rate) + lowertop, viewWidth - 2
                                    - (float) mCandleWidth * (i - mDataStartIndext)
                                    + (float) mCandleWidth / 2, k, whitePaint);

                    canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
                                    * (i + 1 - mDataStartIndext) + (float) mCandleWidth / 2,
                            (float) ((high - Ds.get(i)) * rate) + lowertop, viewWidth - 2
                                    - (float) mCandleWidth * (i - mDataStartIndext)
                                    + (float) mCandleWidth / 2, d, yellowPaint);

                    canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
                                    * (i + 1 - mDataStartIndext) + (float) mCandleWidth / 2,
                            (float) ((high - Js.get(i)) * rate) + lowertop, viewWidth - 2
                                    - (float) mCandleWidth * (i - mDataStartIndext)
                                    + (float) mCandleWidth / 2, j, magentaPaint);
                }
                k = (float) ((high - Ks.get(i)) * rate) + lowertop;
                d = (float) ((high - Ds.get(i)) * rate) + lowertop;
                j = (float) ((high - Js.get(i)) * rate) + lowertop;
            }

            canvas.drawText(new DecimalFormat("#.##").format(high), 2, lowertop
                    + DEFAULT_AXIS_TITLE_SIZE - 2, textPaint);
            canvas.drawText(new DecimalFormat("#.##").format((high + low) / 2), 2, lowertop
                    + lowerHight / 2 + DEFAULT_AXIS_TITLE_SIZE, textPaint);
            canvas.drawText(new DecimalFormat("#.##").format(low), 2, lowertop + lowerHight,
                    textPaint);
        } else if (mTabTitle.trim().equalsIgnoreCase("RSI")) {
            List<Double> Rsi1 = mRSIData.getValue();


            double low = Rsi1.get(mDataStartIndext);
            double high = low;
            double rate = 0.0;
            for (int i = mDataStartIndext; i < mDataStartIndext + mShowDataNum && i < Rsi1.size(); i++) {
                low = low < Rsi1.get(i) ? low : Rsi1.get(i);
//                low = low < Ds.get(i) ? low : Ds.get(i);
//                low = low < Js.get(i) ? low : Js.get(i);

                high = high > Rsi1.get(i) ? high : Rsi1.get(i);
//                high = high > Ds.get(i) ? high : Ds.get(i);
//                high = high > Js.get(i) ? high : Js.get(i);
            }
            rate = lowerHight / (high - low);

            // 绘制白、黄、紫线
            float k = 0.0f;
            float d = 0.0f;
            float j = 0.0f;
            for (int i = mDataStartIndext; i < mDataStartIndext + mShowDataNum && i < Rsi1.size(); i++) {

                if (i != mDataStartIndext) {
                    canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
                                    * (i + 1 - mDataStartIndext) + (float) mCandleWidth / 2,
                            (float) ((high - Rsi1.get(i)) * rate) + lowertop, viewWidth - 2
                                    - (float) mCandleWidth * (i - mDataStartIndext)
                                    + (float) mCandleWidth / 2, k, whitePaint);

//                    canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
//                                    * (i + 1 - mDataStartIndext) + (float) mCandleWidth / 2,
//                            (float) ((high - Ds.get(i)) * rate) + lowertop, viewWidth - 2
//                                    - (float) mCandleWidth * (i - mDataStartIndext)
//                                    + (float) mCandleWidth / 2, d, yellowPaint);
//
//                    canvas.drawLine(viewWidth - 1 - (float) mCandleWidth
//                                    * (i + 1 - mDataStartIndext) + (float) mCandleWidth / 2,
//                            (float) ((high - Js.get(i)) * rate) + lowertop, viewWidth - 2
//                                    - (float) mCandleWidth * (i - mDataStartIndext)
//                                    + (float) mCandleWidth / 2, j, magentaPaint);
                }
                k = (float) ((high - Rsi1.get(i)) * rate) + lowertop;
//                d = (float) ((high - Ds.get(i)) * rate) + lowertop;
//                j = (float) ((high - Js.get(i)) * rate) + lowertop;
            }

            canvas.drawText(new DecimalFormat("#.##").format(high), 2, lowertop
                    + DEFAULT_AXIS_TITLE_SIZE - 2, textPaint);
            canvas.drawText(new DecimalFormat("#.##").format((high + low) / 2), 2, lowertop
                    + lowerHight / 2 + DEFAULT_AXIS_TITLE_SIZE - 2, textPaint);

            canvas.drawText(new DecimalFormat("#.##").format(low), 2, lowertop + lowerHight,
                    textPaint);


        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 设置触摸模式
            case MotionEvent.ACTION_DOWN:
                TOUCH_MODE = DOWN;
                mStartX = event.getRawX();
                mStartY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (TOUCH_MODE == DOWN) {
                    TOUCH_MODE = NONE;
                    if (!super.onTouchEvent(event)) {
                        if (mStartX > 2.0f && mStartX < getWidth() - 2.0f) {
                            showDetails = true;
                        }
                    }
                    postInvalidate();
                } else {
                    TOUCH_MODE = NONE;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                TOUCH_MODE = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mOHLCData == null || mOHLCData.size() <= 0) {
                    return true;
                }
                showDetails = false;
                if (TOUCH_MODE == MOVE) {
                    float horizontalSpacing = event.getRawX() - mStartX;
                    if (Math.abs(horizontalSpacing) < MIN_MOVE_DISTANCE) {
                        return true;
                    }
                    mStartX = event.getRawX();
                    mStartY = event.getRawY();
                    if (horizontalSpacing < 0) {
                        mDataStartIndext--;
                        if (mDataStartIndext < 0) {
                            mDataStartIndext = 0;
                        }
                    } else if (horizontalSpacing > 0) {
                        mDataStartIndext++;
                    }
                    setCurrentData();
                    postInvalidate();
                } else if (TOUCH_MODE == ZOOM) {
                    float verticalSpacing = event.getRawY() - mStartY;
                    if (Math.abs(verticalSpacing) < MIN_MOVE_DISTANCE) {
                        return true;
                    }
                    mStartX = event.getRawX();
                    mStartY = event.getRawY();
                    if (verticalSpacing < 0) {
                        zoomOut();
                    } else {
                        zoomIn();
                    }
                    setCurrentData();
                    postInvalidate();

                } else if (TOUCH_MODE == DOWN) {
                    setTouchMode(event);
                }

                break;
        }
        return true;
    }

    private void setCurrentData() {
        if (mShowDataNum > mOHLCData.size()) {
            mShowDataNum = mOHLCData.size();
        }
        if (MIN_CANDLE_NUM > mOHLCData.size()) {
            mShowDataNum = MIN_CANDLE_NUM;
        }

        if (mShowDataNum > mOHLCData.size()) {
            mDataStartIndext = 0;
        } else if (mShowDataNum + mDataStartIndext > mOHLCData.size()) {
            mDataStartIndext = mOHLCData.size() - mShowDataNum;
        }
        mMinPrice = Double.parseDouble(mOHLCData.get(mDataStartIndext).getLow());
        mMaxPrice = Double.parseDouble(mOHLCData.get(mDataStartIndext).getHigh());
        for (int i = mDataStartIndext + 1; i < mOHLCData.size()
                && i < mShowDataNum + mDataStartIndext; i++) {
            KChartInfo.ResultEntity entity = mOHLCData.get(i);
            mMinPrice = mMinPrice < Double.parseDouble(entity.getLow()) ? mMinPrice : Double.parseDouble(entity.getLow());
            mMaxPrice = mMaxPrice > Double.parseDouble(entity.getHigh()) ? mMaxPrice : Double.parseDouble(entity.getHigh());
        }

        for (MALineEntity lineEntity : MALineData) {
            for (int i = mDataStartIndext; i < lineEntity.getLineData().size()
                    && i < mShowDataNum + mDataStartIndext; i++) {
                mMinPrice = mMinPrice < lineEntity.getLineData().get(i) ? mMinPrice : lineEntity
                        .getLineData().get(i);
                mMaxPrice = mMaxPrice > lineEntity.getLineData().get(i) ? mMaxPrice : lineEntity
                        .getLineData().get(i);
            }
        }

    }

    private void zoomIn() {
        mShowDataNum++;
        if (mShowDataNum > mOHLCData.size()) {
            mShowDataNum = MIN_CANDLE_NUM > mOHLCData.size() ? MIN_CANDLE_NUM : mOHLCData.size();
        }

    }

    private void zoomOut() {
        mShowDataNum--;
        if (mShowDataNum < MIN_CANDLE_NUM) {
            mShowDataNum = MIN_CANDLE_NUM;
        }

    }

    private void setTouchMode(MotionEvent event) {
        float daltX = Math.abs(event.getRawX() - mStartX);
        float daltY = Math.abs(event.getRawY() - mStartY);
        if (FloatMath.sqrt(daltX * daltX + daltY * daltY) > MIN_MOVE_DISTANCE) {
            if (daltX < daltY) {
                TOUCH_MODE = ZOOM;
            } else {
                TOUCH_MODE = MOVE;
            }
            mStartX = event.getRawX();
            mStartY = event.getRawY();
        }
    }

    private float SMA(List<KChartInfo.ResultEntity> entityList, int position, int day, int weight) {
        float close = Float.parseFloat(entityList.get(position).getClose());


        if (day == 1) {
            return close;
        } else {
            if (position + 1 > entityList.size()) {
                return close;

            }
            return (weight * close + (day - 1) * SMA(entityList, position + 1, day - 1, weight)) / (day + 1);
        }

    }

    private List<Float> initSMA(List<KChartInfo.ResultEntity> entityList, int days, int weight) {
        if (days < 2 || entityList == null || entityList.size() <= 0) {
            return null;
        }
        List<Float> SMAValues = new ArrayList<Float>();
        float close = Float.parseFloat(entityList.get(entityList.size() - 1).getClose());

        float result = 0;
        for (int i = 0; i < entityList.size(); i++) {

            if (i > entityList.size() - days) {
                result = SMA(entityList, i, entityList.size() - i, weight);
            } else {
                result = SMA(entityList, i, days, weight);
            }
            SMAValues.add(result);
        }
        return SMAValues;
    }

    /**
     * 初始化MA值，从数组的最后一个数据开始初始化
     *
     * @param entityList
     * @param days
     * @return
     */
    private List<Float> initMA(List<KChartInfo.ResultEntity> entityList, int days) {
        if (days < 2 || entityList == null || entityList.size() <= 0) {
            return null;
        }
        List<Float> MAValues = new ArrayList<Float>();

        float sum = 0;
        float avg = 0;
        for (int i = entityList.size() - 1; i >= 0; i--) {
            float close = Float.parseFloat(entityList.get(i).getClose());
            if (i > entityList.size() - days) {
                sum = sum + close;
                avg = sum / (entityList.size() - i);
            } else {
                sum = close + avg * (days - 1);
                avg = sum / days;
            }
            MAValues.add(avg);
        }

        List<Float> result = new ArrayList<Float>();
        for (int j = MAValues.size() - 1; j >= 0; j--) {
            result.add(MAValues.get(j));
        }
        return result;
    }

    public List<KChartInfo.ResultEntity> getOHLCData() {
        return mOHLCData;
    }

    public void setOHLCData(List<KChartInfo.ResultEntity> OHLCData) {
        if (OHLCData == null || OHLCData.size() <= 0) {
            return;
        }
        this.mOHLCData = OHLCData;
        initMALineData();
        initSMALineData();
        mMACDData = new MACDEntity(mOHLCData);
        mKDJData = new KDJEntity(mOHLCData);
        mRSIData = new RSIEntity(mOHLCData);

        setCurrentData();
        postInvalidate();
    }

    private void initMALineData() {
        MALineEntity MA5 = new MALineEntity();
        MA5.setTitle("MA5");
        MA5.setLineColor(Color.WHITE);
        MA5.setLineData(initMA(mOHLCData, 5));

        MALineEntity MA10 = new MALineEntity();
        MA10.setTitle("MA10");
        MA10.setLineColor(Color.CYAN);
        MA10.setLineData(initMA(mOHLCData, 10));

        MALineEntity MA20 = new MALineEntity();
        MA20.setTitle("MA20");
        MA20.setLineColor(Color.BLUE);
        MA20.setLineData(initMA(mOHLCData, 20));

        MALineData = new ArrayList<MALineEntity>();
        MALineData.add(MA5);
        MALineData.add(MA10);
        MALineData.add(MA20);
    }

    private void initSMALineData() {
        MALineEntity MA5 = new MALineEntity();
        MA5.setTitle("SMA5");
        MA5.setLineColor(Color.WHITE);
        MA5.setLineData(initSMA(mOHLCData, 5, 2));

        MALineEntity MA10 = new MALineEntity();
        MA10.setTitle("SMA10");
        MA10.setLineColor(Color.CYAN);
        MA10.setLineData(initSMA(mOHLCData, 10, 2));

        MALineEntity MA20 = new MALineEntity();
        MA20.setTitle("SMA20");
        MA20.setLineColor(Color.BLUE);
        MA20.setLineData(initSMA(mOHLCData, 20, 2));

        SMALineData = new ArrayList<MALineEntity>();
        SMALineData.add(MA5);
        SMALineData.add(MA10);
        SMALineData.add(MA20);

    }

    //从assets 文件夹中获取文件并读取数据
    public List<Double> getFromAssets(String fileName) {
        ArrayList<Double> list = new ArrayList<>();

        try {
            InputStream in = getResources().getAssets().open(fileName + ".txt");


            BufferedReader BufferedReader = new BufferedReader(new InputStreamReader(in));
            String s1 = null;
            while ((s1 = BufferedReader.readLine()) != null) {

                list.add(Double.parseDouble(s1));
            }
            BufferedReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.reverse(list);
        return list;
    }


}
