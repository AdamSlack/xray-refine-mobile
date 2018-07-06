package org.sociam.koalahero.charts;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

// https://stackoverflow.com/a/40674856/6658062

import java.text.DecimalFormat;

public class DecimalRemover extends PercentFormatter {

    protected DecimalFormat mFormat;

    public DecimalRemover(DecimalFormat format) {
        this.mFormat = format;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if(value < 1) return "";
        return mFormat.format(value);
    }
}