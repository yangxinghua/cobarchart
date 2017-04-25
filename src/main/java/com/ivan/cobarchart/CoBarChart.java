package com.ivan.cobarchart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 17-4-19.
 */

public class CoBarChart extends View {
    private static final String TAG = "SleepChart --->";

    public static final int DIVIDER_NORMAL = 0;
    public static final int DIVIDER_DASH = 1;


    private List<com.ivan.cobarchart.DataSet> mDataSetList = new ArrayList<>();

    private int mThirdColor;
    private int mFirstColor;
    private int mSecondColor;

    private int mLabelColor;
    private float mLabelSize;

    private float mBarWidth;

    private Paint mLabelPaint;
    private Paint mBarPaint;
    private Paint mDividerPaint;
    private Paint mValueRectPaint;
    private Paint mValueTextPaint;

    private float mLabelAndDividerGap;

    private float mMaxValue;

    private boolean mAutoAnimate = true;

    private float mCurrentAnimationValue;

    private ValueAnimator mValueAnimator;

    private float mTarget;

    private boolean mTargetLineAvailable;

    private Paint mTargetLinePaint;

    private float mTargetLabelWidth;
    private float mTargetLabelHeight;

    private Paint mTargetLabelPaint;
    private Paint mTargetLabelRegionPaint;

    private float mChartPadding;

    private Path mArcPath;
    private float mTargetLabelArc;

    private int mDividerAppearance;

    private DashPathEffect mDividerEffect;

    private boolean mTouch;
    private int mTouchIndex;
    private float mTouchX;
    private float mTouchY;

    private float mValueLabelWidth;
    private float mValueLabelHeight;

    private Path mValueLabelArrowPath;

    private float mAvailableChartHeight;
    private float mAvailableChartWidth;

    private RectF mValueLabelRect;


    public CoBarChart(Context context) {
        this(context, null);
    }

    public CoBarChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoBarChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.CoBarChart, 0, 0);
        mThirdColor = t.getColor(R.styleable.CoBarChart_thirdColor,
                context.getResources().getColor(R.color.color_ffa000));
        mFirstColor = t.getColor(R.styleable.CoBarChart_firstColor,
                context.getResources().getColor(R.color.color_02286C));
        mSecondColor = t.getColor(R.styleable.CoBarChart_secondColor,
                context.getResources().getColor(R.color.color_5078c8));
        mLabelColor = t.getColor(R.styleable.CoBarChart_labelColor,
                context.getResources().getColor(R.color.color_e0e0e0));
        mBarWidth = t.getDimension(R.styleable.CoBarChart_barWidth,
                context.getResources().getDimension(R.dimen.default_bar_width));

        mLabelSize = t.getDimension(R.styleable.CoBarChart_labelSize,
                context.getResources().getDimension(R.dimen.default_label_size));
        t.recycle();

        init(context);
    }

    private void init(Context context) {
        mLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLabelPaint.setColor(mLabelColor);
        mLabelPaint.setTextAlign(Paint.Align.CENTER);
        mLabelPaint.setTextSize(mLabelSize);

        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setColor(mFirstColor);

        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setColor(mLabelColor);
        mDividerPaint.setStrokeWidth(1.0f);

        mTargetLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTargetLinePaint.setColor(Color.parseColor("#FF4081"));
        mTargetLinePaint.setStyle(Paint.Style.STROKE);
        mTargetLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));

        mLabelAndDividerGap = context.getResources().getDimension(R.dimen.label_divider_gap);

        mTargetLabelHeight = context.getResources().getDimension(R.dimen.target_label_height);
        mTargetLabelWidth = context.getResources().getDimension(R.dimen.target_label_width);

        mTargetLabelRegionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTargetLabelRegionPaint.setStyle(Paint.Style.FILL);
        mTargetLabelRegionPaint.setColor(context.getResources().getColor(R.color.color_647584));

        mTargetLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTargetLabelPaint.setTextSize(context.getResources().getDimension(R.dimen.target_label_size));
        mTargetLabelPaint.setColor(Color.WHITE);

        mValueRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValueRectPaint.setStyle(Paint.Style.FILL);
        mValueRectPaint.setColor(Color.GREEN);

        mValueTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValueTextPaint.setTextSize(context.getResources().getDimension(R.dimen.value_label_text_size));
        mValueTextPaint.setColor(Color.WHITE);

        mValueLabelWidth = context.getResources().getDimension(R.dimen.value_label_width);
        mValueLabelHeight = context.getResources().getDimension(R.dimen.value_label_height);

        mArcPath = new Path();
        mValueLabelArrowPath = new Path();

        mChartPadding = context.getResources().getDimension(R.dimen.chart_padding);
        mTargetLabelArc = context.getResources().getDimension(R.dimen.target_label_arc);

        mDividerEffect = new DashPathEffect(new float[]{10f, 10f}, 0);

        mValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mValueAnimator.setDuration(1000);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentAnimationValue = (float) animation.getAnimatedValue();
                invalidate();

            }
        });

        if (mAutoAnimate) {
            mValueAnimator.start();
        }
    }

    public void autoAnimate(boolean autoAnimate) {
        mAutoAnimate = autoAnimate;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw --->");

        super.onDraw(canvas);
        if (mDataSetList.isEmpty()) {
            return;
        }

        for (DataSet dataSet : mDataSetList) {
            float totalValue = dataSet.getTotalValue();
            mMaxValue = mMaxValue > totalValue ? mMaxValue : totalValue;
        }


        float width = getWidth();
        float height = getHeight();

        int length = mDataSetList.size();

        int labelHeight = getBoundsHeight(mLabelPaint);


        mAvailableChartHeight = height - labelHeight - mLabelAndDividerGap - mDividerPaint.getStrokeWidth();
        mAvailableChartWidth = width - mChartPadding * 2;

        float perWidth = mAvailableChartWidth / mDataSetList.size();
        // the bar width can't be large than per width;
        mBarWidth = perWidth > mBarWidth ? mBarWidth : perWidth;


        if (mDividerAppearance == DIVIDER_DASH) {
            mDividerPaint.setPathEffect(mDividerEffect);
        }
        // draw divider
        canvas.drawLine(mChartPadding, height - labelHeight - mLabelAndDividerGap, width - mChartPadding,
                height - labelHeight - mLabelAndDividerGap, mDividerPaint);

        float targetLineY = 0.0f;
        if (mTargetLineAvailable && mTarget > 0.0f) {
            // draw target line
            if (mTarget >= mMaxValue) {
                targetLineY = mTargetLabelHeight * 0.5f;
                mMaxValue = mTarget;
            } else {
                targetLineY = mTarget / mMaxValue * mAvailableChartHeight;
            }

            canvas.drawLine(0, targetLineY, width, targetLineY, mTargetLinePaint);
        }


        for (int i = 0; i < length; i++) {
            DataSet dataSet = mDataSetList.get(i);
            if (dataSet.isLabelHighlight()) {
                mLabelPaint.setColor(dataSet.getHighlightColor());
            } else {
                mLabelPaint.setColor(mLabelColor);
            }

            canvas.drawText(dataSet.getLabel(), perWidth * i + perWidth * 0.5f + mChartPadding, height, mLabelPaint);

            // if the max value is 0, it means no data, we no need to draw bar.
            if (mMaxValue == 0) {
                continue;
            }

            float totalValue = dataSet.getTotalValue();

            if (totalValue <= 0) {
                continue;
            }

            float availableHeight = mAvailableChartHeight;
            float barHeight;
            if (mMaxValue == mTarget) {
                barHeight = dataSet.getTotalValue() / mMaxValue * (availableHeight - mTargetLabelHeight * 0.5f);
            } else {
                barHeight = dataSet.getTotalValue() / mMaxValue * availableHeight;
            }
            float firstHeight = dataSet.getFirstValue() / totalValue * barHeight;
            float secondHeight = dataSet.getSecondValue() / totalValue * barHeight;
            float thirdHeight = dataSet.getThirdValue() / totalValue * barHeight;

            if (mAutoAnimate && !mTouch) {
                float drawHeight = barHeight * mCurrentAnimationValue;
                if (drawHeight <= firstHeight) {
                    mBarPaint.setColor(mFirstColor);
                    canvas.drawRect(mChartPadding + perWidth * i + perWidth * 0.5f - mBarWidth * 0.5f,
                            availableHeight - drawHeight,
                            mChartPadding + perWidth * i + perWidth * 0.5f + mBarWidth * 0.5f,
                            availableHeight, mBarPaint);
                } else if (drawHeight <= firstHeight + secondHeight) {
                    // draw first value rect;
                    mBarPaint.setColor(mFirstColor);
                    canvas.drawRect(mChartPadding + perWidth * i + perWidth * 0.5f - mBarWidth * 0.5f,
                            availableHeight - firstHeight,
                            mChartPadding + perWidth * i + perWidth * 0.5f + mBarWidth * 0.5f,
                            availableHeight, mBarPaint);
                    availableHeight -= firstHeight;
                    drawHeight -= firstHeight;

                    // draw second value rect;
                    mBarPaint.setColor(mSecondColor);
                    canvas.drawRect(mChartPadding + perWidth * i + perWidth * 0.5f - mBarWidth * 0.5f,
                            availableHeight - drawHeight,
                            mChartPadding + perWidth * i + perWidth * 0.5f + mBarWidth * 0.5f,
                            availableHeight, mBarPaint);
                } else {
                    // draw third value rect;
                    mBarPaint.setColor(mFirstColor);
                    canvas.drawRect(mChartPadding + perWidth * i + perWidth * 0.5f - mBarWidth * 0.5f,
                            availableHeight - firstHeight,
                            mChartPadding + perWidth * i + perWidth * 0.5f + mBarWidth * 0.5f,
                            availableHeight, mBarPaint);
                    availableHeight -= firstHeight;
                    drawHeight -= firstHeight;

                    // draw second value rect;
                    mBarPaint.setColor(mSecondColor);
                    canvas.drawRect(mChartPadding + perWidth * i + perWidth * 0.5f - mBarWidth * 0.5f,
                            availableHeight - drawHeight,
                            mChartPadding + perWidth * i + perWidth * 0.5f + mBarWidth * 0.5f,
                            availableHeight, mBarPaint);
                    availableHeight -= secondHeight;
                    drawHeight -= secondHeight;

                    // draw third value rect;
                    mBarPaint.setColor(mThirdColor);
                    canvas.drawRect(mChartPadding + perWidth * i + perWidth * 0.5f - mBarWidth * 0.5f,
                            availableHeight - drawHeight,
                            mChartPadding + perWidth * i + perWidth * 0.5f + mBarWidth * 0.5f,
                            availableHeight, mBarPaint);
                }
            } else {
                // draw first value rect;
                mBarPaint.setColor(mFirstColor);
                canvas.drawRect(mChartPadding + perWidth * i + perWidth * 0.5f - mBarWidth * 0.5f,
                        availableHeight - firstHeight,
                        mChartPadding + perWidth * i + perWidth * 0.5f + mBarWidth * 0.5f,
                        availableHeight, mBarPaint);
                availableHeight -= firstHeight;

                // draw second value rect;
                mBarPaint.setColor(mSecondColor);
                canvas.drawRect(mChartPadding + perWidth * i + perWidth * 0.5f - mBarWidth * 0.5f,
                        availableHeight - secondHeight,
                        mChartPadding + perWidth * i + perWidth * 0.5f + mBarWidth * 0.5f,
                        availableHeight, mBarPaint);
                availableHeight -= secondHeight;

                // draw third value rect;
                mBarPaint.setColor(mThirdColor);
                canvas.drawRect(mChartPadding + perWidth * i + perWidth * 0.5f - mBarWidth * 0.5f,
                        availableHeight - thirdHeight,
                        mChartPadding + perWidth * i + perWidth * 0.5f + mBarWidth * 0.5f,
                        availableHeight, mBarPaint);

                // draw bar value label;
                if (mTouch) {
                    int touchBar = (int) ((mTouchX - mChartPadding) / perWidth);
                    if (touchBar == i) {
                        Log.d(TAG, "touchbar ---> " + touchBar);
                        Log.d(TAG, "draw value label arrow --->");
                        mValueLabelArrowPath.reset();
                        mValueLabelArrowPath.moveTo(mChartPadding + perWidth * (i + 1) - perWidth * 0.5f - 10,
                                mAvailableChartHeight - barHeight - 20);
                        mValueLabelArrowPath.lineTo(mChartPadding + perWidth * (i + 1) - perWidth * 0.5f,
                                mAvailableChartHeight - barHeight - 10);
                        mValueLabelArrowPath.lineTo(mChartPadding + perWidth * (i + 1) - perWidth * 0.5f + 10,
                                mAvailableChartHeight - barHeight - 20);
                        mValueLabelArrowPath.lineTo(mChartPadding + perWidth * (i + 1) - 10,
                                mAvailableChartHeight - barHeight - 20);
                        mValueLabelArrowPath.quadTo(mChartPadding + perWidth * (i + 1),
                                mAvailableChartHeight - barHeight - 20,
                                mChartPadding + perWidth * (i + 1),
                                mAvailableChartHeight - barHeight - 10 + mValueLabelHeight);
                        mValueLabelArrowPath.close();
                        canvas.drawPath(mValueLabelArrowPath, mValueRectPaint);
                    }

                }
            }

        }


        if (mTargetLineAvailable && mTarget > 0.0f) {
            // draw target label
            mArcPath.moveTo(width - mTargetLabelWidth, targetLineY + mTargetLabelHeight * 0.5f);
            mArcPath.quadTo(width - mTargetLabelWidth - mTargetLabelArc, targetLineY, width - mTargetLabelWidth,
                    targetLineY - mTargetLabelHeight * 0.5f);
            mArcPath.addRect(width - mTargetLabelWidth, targetLineY - mTargetLabelHeight * 0.5f, width,
                    targetLineY + mTargetLabelHeight * 0.5f, Path.Direction.CCW);
            canvas.drawPath(mArcPath, mTargetLabelRegionPaint);
            canvas.drawText(String.valueOf((int) mTarget), width - mTargetLabelWidth,
                    targetLineY + getBoundsHeight(mTargetLabelPaint) * 0.5f, mTargetLabelPaint);
        }

    }

    public void setDividerAppearance(@IntRange(from = DIVIDER_NORMAL, to = DIVIDER_DASH) int appearance) {
        mDividerAppearance = appearance;
    }

    public void setDataSet(List<DataSet> dataSetList) {
        this.mDataSetList = dataSetList;
        requestLayout();
    }

    private int getBoundsHeight(Paint paint) {
        Rect rect = new Rect(0, 0, 0, 0);
        paint.getTextBounds("0", 0, 1, rect);
        return rect.height();
    }

    public void setTarget(float target) {
        mTarget = target;
    }

    public void setTargetLineAvailable(boolean available) {
        this.mTargetLineAvailable = available;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mTouchX = event.getX();
            mTouchY = event.getY();
            if (mTouchX >= mChartPadding && mTouchX <= mAvailableChartWidth &&
                    mTouchY <= mAvailableChartHeight) {
                mValueLabelRect = new RectF();
                mTouch = true;
                invalidate();
            }
        }

        return true;
    }
}
