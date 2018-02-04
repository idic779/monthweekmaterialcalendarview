package com.prolificinteractive.materialcalendarview.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

import com.amy.monthweek.materialcalendarview.Preference;
import com.amy.monthweek.materialcalendarview.CalendarUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;

/**
 * Created by Administrator on 2018/2/3 0003.
 */

public class LunarSpan implements LineBackgroundSpan {

    private CalendarDay calendarDay;

    /**
     *
     * @param calendarDay
     */
    public LunarSpan(CalendarDay calendarDay) {
        this.calendarDay = calendarDay;
    }


    @Override
    public void drawBackground(
            Canvas canvas, Paint paint,
            int left, int right, int top, int baseline, int bottom,
            CharSequence charSequence,
            int start, int end, int lineNum) {
        int oldColor = paint.getColor();
        Paint textpain=new Paint();
        textpain.setColor(oldColor);
        textpain.setAntiAlias(true);
        textpain.setTextAlign(Paint.Align.CENTER);
        textpain.setTextSize(Preference.dayTextSize);
        CalendarUtils.Lunar lunar = CalendarUtils.solarToLunar(new CalendarUtils.Solar(calendarDay.getYear(), calendarDay.getMonth()+1, calendarDay.getDay()));
        String lunarDayString = CalendarUtils.getLunarDayString(calendarDay.getYear(), calendarDay.getMonth()+1, calendarDay.getDay(), lunar.lunarYear, lunar.lunarMonth, lunar.lunarDay, lunar.isLeap);
        canvas.drawText(lunarDayString,(left + right) / 2,bottom+ Preference.dayTextSize,textpain);
        paint.setColor(oldColor);
    }
}
