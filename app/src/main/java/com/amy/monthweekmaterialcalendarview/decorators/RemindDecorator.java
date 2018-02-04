package com.amy.monthweekmaterialcalendarview.decorators;

import android.content.Context;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

/**
 * Created by Administrator on 2018/2/4 0004.
 */

public class RemindDecorator implements DayViewDecorator {
    private final Calendar calendar = Calendar.getInstance();
    private Context context;
    public RemindDecorator(Context context) {
        this.context=context;
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(calendar);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDay == Calendar.SATURDAY || weekDay == Calendar.SUNDAY;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new RestSpan(context));
    }
}
