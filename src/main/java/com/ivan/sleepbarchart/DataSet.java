package com.ivan.sleepbarchart;

/**
 * Created by ivan on 17-4-19.
 */

public class DataSet {
    private String mLabel;

    private float mFirstValue;

    private float mThirdValue;

    private float mSecondValue;

    private boolean mLabelHighlight;

    private int mHighlightColor;

    public int getHighlightColor() {
        return mHighlightColor;
    }

    public void setHighlightColor(int highlightColor) {
        mHighlightColor = highlightColor;
    }

    public boolean isLabelHighlight() {
        return mLabelHighlight;
    }

    public void setLabelHighlight(boolean labelHighlight) {
        mLabelHighlight = labelHighlight;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        this.mLabel = label;
    }

    public float getFirstValue() {
        return mFirstValue;
    }

    public void setFirstValue(float firstValue) {
        this.mFirstValue = firstValue;
    }

    public float getThirdValue() {
        return mThirdValue;
    }

    public void setThirdValue(float thirdValue) {
        this.mThirdValue = thirdValue;
    }

    public float getSecondValue() {
        return mSecondValue;
    }

    public void setSecondValue(float secondValue) {
        this.mSecondValue = secondValue;
    }

    public float getTotalValue() {
        return mSecondValue + mThirdValue + mFirstValue;
    }
}
