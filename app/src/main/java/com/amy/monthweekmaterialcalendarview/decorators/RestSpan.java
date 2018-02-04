package com.amy.monthweekmaterialcalendarview.decorators;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.style.LineBackgroundSpan;

import com.amy.monthweekmaterialcalendarview.R;

/**
 * Created by Administrator on 2018/2/4 0004.
 */

public class RestSpan implements LineBackgroundSpan {
    private Context context;
    public RestSpan(Context context) {
        this.context=context;
    }
    @Override
    public void drawBackground(
            Canvas canvas, Paint paint,
            int left, int right, int top, int baseline, int bottom,
            CharSequence charSequence,
            int start, int end, int lineNum) {
        Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.rest);

        Rect mSrcRect = new Rect(left, top, bitmap.getWidth(), bitmap.getHeight());
        int width=(right-left)*3/4-(right-left)/2;
        Rect mDestRect = new Rect((right-left)*5/8, -width, (right-left)*7/8,0);
        canvas.drawBitmap(bitmap, mSrcRect, mDestRect, paint);
    }
}
