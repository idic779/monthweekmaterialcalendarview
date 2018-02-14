package com.amy.monthweek.materialcalendarview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.Calendar;

/**
 * Created by amy .
 * 整体页面布局是2个calendarView
 * 一个是日历视图mCalendarViewMonth ，日历视图切换为周模式的mCalendarViewWeek，
 */
public class MonthWeekMaterialCalendarView extends FrameLayout implements SlideMonthInterface {
    //月视图calendar的高度
    private int calendarMonthHight;
    //滚动到最后周模式的高度 顶部周日高度+单个item的高度
    private int finalWeekModeHeight;
    //顶部的周日高度
    private int weekViewHight;
    //最后剩下的Item的位置单行item高度
    private int singleItemHight;
    // 滑动停下的高度，默认当前选中的日期的行数*乘以单行高度
    private int defaultStopHeight;
    //移动的距离
    private float transY;
    //最大移动的距离
    private int maxOffset;
    //日模式CalendarView
    private MaterialCalendarView mCalendarViewMonth;
    //周模式的calendarView，默认是隐藏的
    private MaterialCalendarView mCalendarViewWeek;
    //周的视图
    private View mTopWeekView;
    private RecyclerView mRecyclerView;
    public ViewDragHelper mDragHelper;
    private DragHelperCallback mCallBack;
    private VelocityTracker mVTracker;
    //recyclerView最开始的高度
    private int finalMonthModeHeight;
    private static final int DEFAULT_INTERVAL = 250;
    private SlideModeChangeListener mSlideModeChangelistener;
    private SlideDateSelectedlistener mDateSelectedlistener;
    private SlideScrolledlistener mSlideScrolledlistener;
    private SlideOnMonthChangedListener mSlideOnMonthChangedListener;
    private Context mContext;
    //模式是否改变
    private boolean isModeChange;
    //是否允许拖动,默认上下拖动
    private boolean canDrag = true;
    //默认是月模式
    public Mode currentMode = Mode.MONTH;
    @Override
    public void setMaxOffset(int maxOffset) {
        this.maxOffset=maxOffset;
    }

    @Override
    public void setSingleItemHeight(int height) {
        this.singleItemHight =height;
    }


    public enum Mode {
        //2种模式
        MONTH, WEEK
    }

    public MonthWeekMaterialCalendarView(Context context) {
        this(context, null);
    }

    public MonthWeekMaterialCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mCallBack = new DragHelperCallback();
        mDragHelper = ViewDragHelper.create(this, mCallBack);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setSingleItemHeight(mCalendarViewMonth.getItemHeight());
        if (defaultStopHeight == 0) {
            // 设置停止滑动的位置
            setStopItemPosition(getCurrentItemPosition(CalendarDay.today()));
        }
        calendarMonthHight = mCalendarViewMonth.getMeasuredHeight();
        weekViewHight = mTopWeekView.getMeasuredHeight();
        finalMonthModeHeight = weekViewHight + calendarMonthHight;
        finalWeekModeHeight = singleItemHight + weekViewHight;
        setMaxOffset(calendarMonthHight - singleItemHight);
    }

    private int recyclerViewTop;
    private int monthCalendarTop;
    private int weekTop;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
        if (currentMode == Mode.WEEK) {
//            monthCalendarTop=singleItemHight-defaultStopHeight;
            recyclerViewTop = finalWeekModeHeight;
//            weekTop=weekViewHight;
        } else if (currentMode == Mode.MONTH) {
//            monthCalendarTop =weekViewHight;
            if (mRecyclerView.getTop()==0) {
                //如果没初始化的话
                recyclerViewTop = finalMonthModeHeight;
            }else {
                recyclerViewTop = mRecyclerView.getTop();
            }
//            weekTop=weekViewHight;
        }

        mCalendarViewMonth.layout(0,
                weekViewHight,
                mCalendarViewMonth.getMeasuredWidth(),
                weekViewHight + mCalendarViewMonth.getMeasuredHeight());
        mCalendarViewWeek.layout(0,
                mTopWeekView.getMeasuredHeight(),
                mCalendarViewWeek.getMeasuredWidth(),
                mTopWeekView.getMeasuredHeight() + mCalendarViewWeek.getMeasuredHeight());
        mTopWeekView.layout(0,
                0,
                mTopWeekView.getMeasuredWidth(),
                mTopWeekView.getMeasuredHeight());
        mRecyclerView.layout(0,
                recyclerViewTop,
                getMeasuredWidth(),
                recyclerViewTop + getMeasuredHeight()-finalWeekModeHeight);
    }
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        try{
            super.onRestoreInstanceState(state);
        } catch (Exception e){
            state = null;
        }
    }

    /**
     * 设置滑动最后停止的位置，默认是选中的日期的位置
     *
     * @param position
     */

    private void setStopItemPosition(int position) {

        defaultStopHeight = (position) * singleItemHight;
    }

    class DragHelperCallback extends ViewDragHelper.Callback {

        /**
         * 设置recyclerView可拖拽
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return !mDragHelper.continueSettling(true) &&
                    child == mRecyclerView && !animatStart
                    && isAtTop(mRecyclerView) && !ViewCompat.canScrollVertically(mRecyclerView, -1);
        }

        /**
         *
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            //决定竖直方向上能移动的距离为 finalWeekModeHeight-finalMonthModeHeight
            int topBound = finalWeekModeHeight;
            int bottomBound = finalMonthModeHeight;
            int newTop = Math.min(Math.max(top, topBound), bottomBound);
            return newTop;
        }

        /**
         * 水平可拖拽的距离范围,大于0才能水平拖动
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return 0;
        }

        /**
         * 垂直可拖拽的距离范围
         */
        @Override
        public int getViewVerticalDragRange(View child) {
            return maxOffset;
        }

        /**
         * 监听到View位置的变化，同时移动其他的View
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            HandlerOffset(changedView, left, top, dx, dy);
        }

        /**
         * @param changedView : the same as onViewPositionChanged
         * @param left        ：the same as onViewPositionChanged
         * @param top         ：the same as onViewPositionChanged
         * @param dx          : the same as onViewPositionChanged
         * @param dy          : the same as onViewPositionChanged
         */

        //滑动处理
        private void HandlerOffset(View changedView, int left, int top, int dx, int dy) {
            //获取日历相对手指移动的相对距离 dy向上移动小于0
            transY = transY + dy;
            if (transY > 0) {
                transY = 0;
            }
            if (transY < -calendarMonthHight - singleItemHight) {
                transY = -calendarMonthHight - singleItemHight;
            }

            float abstransY = Math.abs(transY);
            if (mSlideScrolledlistener != null) {
                if (currentMode == Mode.MONTH) {
                    if (dy < 0) {
                        if (abstransY > 0 && abstransY <= singleItemHight) {
                            float percent = abstransY / singleItemHight;
//                            mSlideScrolledlistener.onScrolled(0, percent);
                        }
                    }
                    if (dy < 0) {
                        if (abstransY > 0 && abstransY <= singleItemHight) {
                            float percent = 1 - abstransY / singleItemHight;
//                            mSlideScrolledlistener.onScrolled(1, percent);
                        }
                    }
                }


            }
            if (dy < 0) {
                //如果上滑动，默认滑动的绝对值距离在超过calendarMonthHight-defaultStopHeight
                // 并且小于可以滑动的距离calendarMonthHight-calendarItemHight之间的话
                if (abstransY >= (calendarMonthHight - defaultStopHeight) && abstransY < maxOffset) {
                    if (!animatStart) {
                        mCalendarViewMonth.setTranslationY(getOffset((int) mCalendarViewMonth.getTranslationY() + dy, singleItemHight - defaultStopHeight));
                    }
                }
            }
            if (dy > 0) {
                if (abstransY < maxOffset
                        && currentMode.equals(Mode.WEEK)) {
                    mCalendarViewWeek.setVisibility(INVISIBLE);
                }
                if (abstransY < maxOffset) {
                    mCalendarViewMonth.setTranslationY(getOffset((int) mCalendarViewMonth.getTranslationY() + dy, 0));
                }

            }

        }

        /**
         * 释放拖拽后执行，根据mViewContent的拖拽距离决定是否上滑或返回原位
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int moveY = finalMonthModeHeight - mRecyclerView.getTop();
            //默认滑动距离为一行的高度，超过就滑动到周模式位置
            int minDistance = singleItemHight;
            //最大滑动距离
            int maxDistance = calendarMonthHight;
            if (currentMode == Mode.MONTH) {
                //如果滑动距离超过默认的一行距离和最大滑动距离之间的距离
                if (moveY > minDistance && moveY < maxDistance) {
                    //变为周模式
                    setMode(Mode.WEEK);
                } else if (moveY <= minDistance) {
                    //变为月模式
                    setMode(Mode.MONTH);
                }
            } else {
                //周模式下滑动距离小于最大滑动距离单行距离的话就让它变为月模式
                if (moveY > maxOffset - singleItemHight) {
                    //变为周模式
                    setMode(Mode.WEEK);
                } else if (moveY <= maxOffset - minDistance) {
                    //变为月模式
                    setMode(Mode.MONTH);
                }
            }
        }

    }

    /**
     * 判断线性布局的recyclerView是否到达顶部
     *
     * @param view
     */
    private boolean isAtTop(View view) {
        boolean isTop = false;
        if (view instanceof RecyclerView) {
            if (((RecyclerView) view).getLayoutManager() instanceof ILayoutManager) {
                if (((RecyclerView) view).getLayoutManager() instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) ((RecyclerView) view).getLayoutManager();
                    if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        isTop = true;
                    }
                }
                if (((RecyclerView) view).getLayoutManager() instanceof GridLayoutManager) {
                    GridLayoutManager gridLayoutManager = (GridLayoutManager) ((RecyclerView) view).getLayoutManager();
                    if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        isTop = true;
                    }
                }
                if (((RecyclerView) view).getLayoutManager() instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) ((RecyclerView) view).getLayoutManager();
                    int[] positions = new int[staggeredGridLayoutManager.getSpanCount()];
                    staggeredGridLayoutManager.findFirstVisibleItemPositions(positions);
                    if (findMin(positions) == 0) {
                        isTop = true;
                    }
                }
            } else {
                throw new IllegalArgumentException("RecyclerView layoutManager must implement ILayoutManager");
            }

        }
        return isTop;
    }

    private int findMin(int [] position) {
        int min=position[0];
        for (int value : position) {
            if (value<min) {
                min=value;
            }
        }
        return  min;
    }
    /**
     * 防止滑动过快越界
     *
     * @param offset
     * @param maxOffset
     * @return
     */
    private int getOffset(int offset, int maxOffset) {
        if (offset < maxOffset && maxOffset < 0) {
            return maxOffset;
        }
        if (offset > maxOffset && maxOffset >= 0) {
            return maxOffset;
        }

        return offset;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mVTracker == null) {
            mVTracker = VelocityTracker.obtain();
        }
        mVTracker.addMovement(event);
        final VelocityTracker vt = mVTracker;

        if (animatStart) {
            return true;
        }
        boolean shouldIntercept = mDragHelper.shouldInterceptTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            vt.computeCurrentVelocity(1000);
            if (currentMode == Mode.MONTH && canDrag) {
                setRecyclerViewCanScroll(false);
            }
            if (!canDrag) {
                return super.onInterceptTouchEvent(event);
            }
            //向上
            if (vt.getYVelocity() < 0) {
                //周模式向上滑动时候交给recyclerView去处理，并且在顶部
                if (isAtTop(mRecyclerView) && currentMode == Mode.WEEK) {
                    return super.onInterceptTouchEvent(event);
                }


            } else {
                //如果recyclerView不在顶部，向下滑动时候交给recyclerView去处理，
                if (!isAtTop(mRecyclerView) ) {
                    setRecyclerViewCanScroll(true);
                    return super.onInterceptTouchEvent(event);
                }
            }

        }

        return shouldIntercept && isAtTop(mRecyclerView) && canDrag;
    }

    public void setRecyclerViewCanScroll(boolean enable) {
        ((ILayoutManager) mRecyclerView.getLayoutManager()).setScrollEnabled(enable);
    }

    /**
     * 选中日期
     * @param date
     */
    public void setSelectedDate(@Nullable CalendarDay date) {
        mCalendarViewWeek.setSelectedDate(date);
        mCalendarViewMonth.setSelectedDate(date);
        selectDate = date;
        int position = getCurrentItemPosition(date);
        setStopItemPosition(position);
        if (currentMode == Mode.WEEK) {
            setTransYObjectAnimator(mCalendarViewMonth, mCalendarViewMonth.getTranslationY(), singleItemHight - defaultStopHeight, 250, null);
        }
    }

    private void setWeek2MonthMode() {
        mCalendarViewWeek.setVisibility(INVISIBLE);
        setCalendarView2MonthMode();
        setUpdateBottomAndTopAnimator(mRecyclerView.getTop(), finalMonthModeHeight, DEFAULT_INTERVAL, new SlideOffsetAnimatorlistener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int animatedValue = (int) valueAnimator.getAnimatedValue();
                int top = mRecyclerView.getTop();
                int offset = animatedValue - top;
                mRecyclerView.offsetTopAndBottom(offset);
            }
        });
//        mDragHelper.smoothSlideViewTo(mRecyclerView, 0, finalMonthModeHeight);
//        transY = 0;
//        currentMode = Mode.MONTH;
//        postInvalidate();
    }

    private void setMonth2WeekMode() {
        setUpdateBottomAndTopAnimator(mRecyclerView.getTop(), finalWeekModeHeight, DEFAULT_INTERVAL, new SlideOffsetAnimatorlistener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                int top = mRecyclerView.getTop();
                int offset = animatedValue - top;
                mRecyclerView.offsetTopAndBottom(offset);
            }
        });
        setCalendarView2WeekMode();
//        mDragHelper.smoothSlideViewTo(mRecyclerView, 0, finalWeekModeHeight);
//        postInvalidate();
    }


    public void setMode(Mode mode) {
        if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_SETTLING) {
            return;
        }
        //如果点击了月模式并且当前模式不是月模式的话
        if (mode.equals(Mode.MONTH)) {
            // 月模式切月模式
            if (currentMode.equals(Mode.MONTH)) {
                isModeChange = false;
            }
            //周模式切月模式
            if (currentMode.equals(Mode.WEEK)) {
                isModeChange = true;
            }
            setWeek2MonthMode();
            return;
        }
        if (mode.equals(Mode.WEEK)) {

            //周模式切周模式
            if (currentMode.equals(Mode.WEEK)) {
                isModeChange = false;
            }
            //月模式切周模式
            if (currentMode.equals(Mode.MONTH)) {
                isModeChange = true;
            }
            setMonth2WeekMode();

        }

    }


    private boolean animatStart;

    public void setAnimatStart(boolean animatStart) {
        this.animatStart = animatStart;
    }

    public boolean isAnimatStart() {
        return  animatStart;
    }

    //把calendarView移动到周模式的高度
    private void setCalendarView2WeekMode() {
        animatStart = true;
        setTransYObjectAnimator(mCalendarViewMonth, mCalendarViewMonth.getTranslationY(), singleItemHight - defaultStopHeight, DEFAULT_INTERVAL, new SlideAnimatorlistener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mCalendarViewWeek.setVisibility(VISIBLE);
                mCalendarViewMonth.clearAnimation();
                //动画播放完毕说明已经滚动到了顶部
                animatStart = false;
                transY = singleItemHight - calendarMonthHight;
                setRecyclerViewCanScroll(true);
                mCalendarViewMonth.setCurrentDate(selectDate);
                currentMode = Mode.WEEK;
                if (mSlideModeChangelistener != null && isModeChange) {
                    mSlideModeChangelistener.modeChange(currentMode);
                }
            }
        });

    }

    //把calendarView移动到月模式的高度
    private void setCalendarView2MonthMode() {
        animatStart = true;
        setTransYObjectAnimator(mCalendarViewMonth, mCalendarViewMonth.getTranslationY(), 0, DEFAULT_INTERVAL, new SlideAnimatorlistener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
//                mCalendarViewWeek.setVisibility(INVISIBLE);
                mCalendarViewMonth.clearAnimation();
                //动画播放完毕说明已经滚动到了顶部
                animatStart = false;
                transY = 0;
                currentMode = Mode.MONTH;
                mCalendarViewWeek.setCurrentDate(selectDate);
                if (mSlideModeChangelistener != null && isModeChange) {
                    mSlideModeChangelistener.modeChange(currentMode);
                }
            }
        });

    }

    private void setUpdateBottomAndTopAnimator(int fromY, int toY, long duration, final SlideOffsetAnimatorlistener listener) {
        ValueAnimator animator = new ValueAnimator();
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
//        animator.setEvaluator(new TypeEvaluator<Integer>() {
//            @Override
//            public Integer evaluate(float fraction, Integer start, Integer end) {
//                return (int) (start + (end - start) * fraction);
//            }
//        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (listener != null) {
                    listener.onAnimationUpdate(valueAnimator);
                }
            }
        });
        animator.setIntValues(fromY, toY);
        animator.setDuration(duration);
        animator.start();
    }

    private void setTransYObjectAnimator(final View view, float fromY, float toY, long duration, final SlideAnimatorlistener listener) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", fromY, toY);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {


            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (listener != null) {
                    listener.onAnimationStart(animator);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (listener != null) {
                    listener.onAnimationEnd(animator);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {   //边界拖动时候
        mDragHelper.processTouchEvent(event);
        return true;
    }

    /**
     * 是否点击在对应的view上
     * @param view
     * @param x
     * @param y
     * @return
     */
    private boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.getWidth() &&
                screenY >= viewLocation[1] && screenY < viewLocation[1] + view.getHeight();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCalendarViewMonth = (MaterialCalendarView) getChildAt(0);
        mCalendarViewWeek = (MaterialCalendarView) getChildAt(1);
        mRecyclerView = (RecyclerView) getChildAt(2);
        mTopWeekView = getChildAt(3);
        mCalendarViewMonth.setTopbarVisible(false);
        newState().commit();
        setListener();
    }

    private void setListener() {
        mCalendarViewWeek.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectDate = date;
                int position = getCurrentItemPosition(date);
                setStopItemPosition(position);
                setTransYObjectAnimator(mCalendarViewMonth, mCalendarViewMonth.getTranslationY(), singleItemHight - defaultStopHeight, 250, null);
                //月视图也要选中日期
                mCalendarViewMonth.setCurrentDate(date);
                mCalendarViewMonth.setSelectedDate(date);
                if (currentMode == Mode.WEEK) {
                    if (mDateSelectedlistener != null) {
                        mDateSelectedlistener.onDateSelected(widget, date, selected);
                    }
                }
            }
        });
        mCalendarViewMonth.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectDate = date;
                int position = getCurrentItemPosition(date);
                setStopItemPosition(position);
                //周视图也要选中日期
                mCalendarViewWeek.setCurrentDate(date);
                mCalendarViewWeek.setSelectedDate(date);
                if (currentMode == Mode.MONTH) {
                    if (mDateSelectedlistener != null) {
                        mDateSelectedlistener.onDateSelected(widget, date, selected);
                    }
                }
            }
        });
        mCalendarViewWeek.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                if (mSlideOnMonthChangedListener != null && currentMode == Mode.WEEK) {
                    mSlideOnMonthChangedListener.onMonthChanged(widget, date);
                }
            }
        });
        mCalendarViewMonth.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                if (mSlideOnMonthChangedListener != null && currentMode == Mode.MONTH) {
                    mSlideOnMonthChangedListener.onMonthChanged(widget, date);
                }
            }
        });
    }

    //计算拖动速度
    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    //是否处于STATE_SETTLING状态
    public boolean isSetting() {
        boolean isSetting = mDragHelper.getViewDragState() == ViewDragHelper.STATE_SETTLING;
        return isSetting;
    }


    //判断当前选中天数在第几行
    public int getCurrentItemPosition(CalendarDay calendarDay) {
        Calendar currentCanlendar = calendarDay.getCalendar();
        //先计算每个月第一天是周几
        currentCanlendar.set(Calendar.DAY_OF_MONTH,
                currentCanlendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        int i = currentCanlendar.get(Calendar.DAY_OF_WEEK);
//        Calendar中每周是从我们中国人的周日(星期七)开始计算的,
//       所以要减一才是真正的周几,（当前选中日期数+每个月第一天是周几）/7
//       整除的结果就是选中的日期行数
        int result = calendarDay.getDay() + i - 1;
        int position = 0;
        if (result % 7 == 0) {
            position = result / 7;
            return position;
        }
        if (result % 7 != 0) {
            position = result / 7 + 1;
            return position;
        }
        return position;
    }

    public void addDecorator(DayViewDecorator decorator) {
        mCalendarViewMonth.addDecorator(decorator);
        mCalendarViewWeek.addDecorator(decorator);
    }

    public void addDecorators(DayViewDecorator... decorators) {
        mCalendarViewMonth.addDecorators(decorators);
        mCalendarViewWeek.addDecorators(decorators);
    }

    public void removeDecorator(DayViewDecorator decorator) {
        mCalendarViewMonth.removeDecorator(decorator);
        mCalendarViewWeek.removeDecorator(decorator);
    }

    public  void setShowLunar(boolean showLunar){
        mCalendarViewMonth.setShowLunar(showLunar);
        mCalendarViewWeek.setShowLunar(showLunar);
    }

    public void removeDecorators() {
        mCalendarViewMonth.removeDecorators();
        mCalendarViewWeek.removeDecorators();
    }

    public void goToNext() {
        mCalendarViewMonth.goToNext();
        mCalendarViewWeek.goToNext();
    }

    public void goToPrevious() {
        mCalendarViewMonth.goToPrevious();
        mCalendarViewWeek.goToPrevious();
    }

    /**
     * 设置当前所在的日期，设置后滚动到对应日期
     * @param date
     */
    public void setCurrentDate(@Nullable CalendarDay date) {
        mCalendarViewMonth.setCurrentDate(date);
        mCalendarViewWeek.setCurrentDate(date);
    }

    public int getShowOtherDates() {
        return mCalendarViewMonth.getShowOtherDates();
    }

    public boolean isCanDrag() {
        return canDrag;
    }

    public void setCanDrag(boolean canDrag) {
        this.canDrag = canDrag;
    }

    public void setPagingEnabled(boolean pagingEnabled) {
        mCalendarViewMonth.setPagingEnabled(pagingEnabled);
        mCalendarViewWeek.setPagingEnabled(pagingEnabled);
    }

    public boolean allowClickDaysOutsideCurrentMonth() {
        return mCalendarViewMonth.allowClickDaysOutsideCurrentMonth();
    }

    public void setAllowClickDaysOutsideCurrentMonth(final boolean enabled) {
        mCalendarViewMonth.setAllowClickDaysOutsideCurrentMonth(enabled);
        mCalendarViewWeek.setAllowClickDaysOutsideCurrentMonth(enabled);
    }

    public void setShowOtherDates(int showOtherDates) {
        mCalendarViewMonth.setShowOtherDates(showOtherDates);
        mCalendarViewWeek.setShowOtherDates(showOtherDates);
    }

    public void setSelectionColor(int color) {
        mCalendarViewMonth.setSelectionColor(color);
        mCalendarViewMonth.setShowOtherDates(color);
    }

    //模式改变的回调
    public interface SlideModeChangeListener {

        void modeChange(Mode mode);
    }
    //属性动画回调
    public interface SlideOffsetAnimatorlistener {

        void onAnimationUpdate(ValueAnimator valueAnimator);
    }

    //日期选中回调
    public interface SlideDateSelectedlistener {

        void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected);
    }

    //滚动距离回调
    public interface SlideScrolledlistener {

        void onScrolled(int position, float percent);
    }

    public interface SlideOnMonthChangedListener {
        void onMonthChanged(MaterialCalendarView widget, CalendarDay date);
    }

    public void setSlideScrolledlistener(SlideScrolledlistener mSlideScrolledlistener) {
        this.mSlideScrolledlistener = mSlideScrolledlistener;
    }

    private SlideState slideState;

    public SlideState state() {
        return slideState;
    }

    public SlideStateBuilder newState() {
        return new SlideStateBuilder();
    }

    private CalendarDay selectDate;

    private void commit(SlideState slideState) {
        this.slideState = slideState;
        mCalendarViewMonth.state().edit()
                .setMinimumDate(slideState.minDate)
                .setMaximumDate(slideState.maxDate)
                .commit();
        mCalendarViewWeek.state().edit()
                .setMinimumDate(slideState.minDate)
                .setMaximumDate(slideState.maxDate)
                .commit();
        this.mDateSelectedlistener = slideState.slideDateSelectedlistener;
        this.mSlideModeChangelistener = slideState.slideModeChangeListener;
        this.mSlideOnMonthChangedListener = slideState.slideOnMonthChangedListener;
        this.canDrag = slideState.canScrollVertical;
    }

    public class SlideState {
        private final CalendarDay minDate;
        private final CalendarDay maxDate;
        private final boolean canScrollVertical;
        private SlideDateSelectedlistener slideDateSelectedlistener;
        private SlideModeChangeListener slideModeChangeListener;
        private SlideOnMonthChangedListener slideOnMonthChangedListener;

        private SlideState(SlideStateBuilder builder) {
            minDate = builder.minDate;
            maxDate = builder.maxDate;
            canScrollVertical = builder.canScrollVertical;
            slideDateSelectedlistener = builder.slideDateSelectedlistener;
            slideModeChangeListener = builder.slideModeChangeListener;
            slideOnMonthChangedListener = builder.slideOnMonthChangedListener;
        }

        public SlideStateBuilder edit() {
            return new SlideStateBuilder(this);
        }
    }

    public class SlideStateBuilder {
        private CalendarDay minDate = null;
        private CalendarDay maxDate = null;
        private boolean canScrollVertical = true;
        private SlideDateSelectedlistener slideDateSelectedlistener;
        private SlideModeChangeListener slideModeChangeListener;
        private SlideOnMonthChangedListener slideOnMonthChangedListener;

        public SlideStateBuilder() {
        }

        private SlideStateBuilder(SlideState state) {
            minDate = state.minDate;
            maxDate = state.maxDate;
            canScrollVertical = state.canScrollVertical;
        }

        public SlideStateBuilder setSlideDateSelectedlistener(SlideDateSelectedlistener listener) {
            slideDateSelectedlistener = listener;
            return this;
        }

        public SlideStateBuilder setSlideModeChangeListener(SlideModeChangeListener listener) {
            slideModeChangeListener = listener;
            return this;
        }

        public SlideStateBuilder setSlideOnMonthChangedListener(SlideOnMonthChangedListener listener) {
            slideOnMonthChangedListener = listener;
            return this;
        }

        public SlideStateBuilder setMinimumDate(@Nullable Calendar calendar) {
            setMinimumDate(CalendarDay.from(calendar));
            return this;
        }

        public SlideStateBuilder setMinimumDate(@Nullable CalendarDay calendar) {
            minDate = calendar;
            return this;
        }

        public SlideStateBuilder setMaximumDate(@Nullable Calendar calendar) {
            setMaximumDate(CalendarDay.from(calendar));
            return this;
        }

        public SlideStateBuilder setMaximumDate(@Nullable CalendarDay calendar) {
            maxDate = calendar;
            return this;
        }
        public SlideStateBuilder canScrollVertical(boolean canScrollVertical) {
            this.canScrollVertical = canScrollVertical;
            return this;
        }

        public void commit() {
            MonthWeekMaterialCalendarView.this.commit(new SlideState(this));
        }
    }

}


