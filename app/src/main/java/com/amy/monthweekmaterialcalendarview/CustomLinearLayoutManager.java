package com.amy.monthweekmaterialcalendarview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import com.amy.monthweek.materialcalendarview.ILayoutManager;

/**
 * Created by Administrator on 2018/2/14 0014.
 */

public class CustomLinearLayoutManager extends LinearLayoutManager implements ILayoutManager {
    private boolean isScrollEnabled = true;

    public CustomLinearLayoutManager(Context context) {
        super(context);
    }

    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}