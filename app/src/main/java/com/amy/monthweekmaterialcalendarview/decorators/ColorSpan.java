package com.amy.monthweekmaterialcalendarview.decorators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;


/**
 * Created by Administrator on 2018/5/18 0018.
 */

public class ColorSpan implements LineBackgroundSpan {
    private Context context;
    private int color;
    public ColorSpan(Context contex,int color) {
        this.context=context;
        this.color=color;
    }
    @Override
    public void drawBackground(
            Canvas canvas, Paint paint,
            int left, int right, int top, int baseline, int bottom,
            CharSequence charSequence,
            int start, int end, int lineNum) {

    }
}
