package com.example.myapplication.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.PieChart;

public class CostumPieChart extends PieChart {
    public CostumPieChart(Context context) {
        super(context);
        setRenderer(new CostumRenderer(this,this.mAnimator,this.mViewPortHandler));
    }

    public CostumPieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        setRenderer(new CostumRenderer(this,this.mAnimator,this.mViewPortHandler));
    }
}
