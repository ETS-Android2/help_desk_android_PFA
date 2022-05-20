package com.example.myapplication.ui;

import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.renderer.PieChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class CostumRenderer extends PieChartRenderer {
    public CostumRenderer(PieChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    public void drawExtras(Canvas c) {
        if (mDrawBitmap != null) {
            super.drawExtras(c);
        }
    }
}
