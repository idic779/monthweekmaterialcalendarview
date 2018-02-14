package com.amy.monthweekmaterialcalendarview;

import android.animation.Animator;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amy.monthweek.materialcalendarview.MonthWeekMaterialCalendarView;
import com.amy.monthweekmaterialcalendarview.decorators.EventDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by Administrator on 2017/4/19 0019.
 */
public class CalendarSmoothActivity extends AppCompatActivity {
    @BindView(R.id.slidelayout)
    MonthWeekMaterialCalendarView monthWeekMaterialCalendarView;
    @BindView(R.id.weekview_top)
    LinearLayout calendarView_onlyhas_weekName;
    private CalendarDay selectedDate;
    @BindView(R.id.titlebar_month)
    TextView _titlebar_month;
    @BindView(R.id.titlebar_week)
    TextView _titlebar_week;
    @BindView(R.id.tv_selectdate)
    TextView _tv_selectdate;
    private MultiTypeAdapter adapter;
    private Items items;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.circleIndicator)
    DropIndicator dropIndicator;
    private boolean canPaging=true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_smooth);
        ButterKnife.bind(this);
        initRecyclerView();
        selectedDate = CalendarDay.today();

        monthWeekMaterialCalendarView.setCurrentDate(selectedDate);
        monthWeekMaterialCalendarView.setSelectedDate(selectedDate);
        //默认是月模式
        setMonthSelector();
        monthWeekMaterialCalendarView.state().edit().setSlideModeChangeListener(new MonthWeekMaterialCalendarView.SlideModeChangeListener() {
            @Override
            public void modeChange(MonthWeekMaterialCalendarView.Mode mode) {
                if (mode.equals(MonthWeekMaterialCalendarView.Mode.MONTH)) {
                    if (!_titlebar_month.isSelected()&&!dropAnimat) {
                        setMonthSelector();
                        clickMonthAnimator();
                    }
                }
                if (mode.equals(MonthWeekMaterialCalendarView.Mode.WEEK)) {
                    if (!_titlebar_week.isSelected()&&!dropAnimat) {
                        setWeekSelector();
                        clickWeekAnimator();
                    }
                }
            }
        }).setSlideDateSelectedlistener(new MonthWeekMaterialCalendarView.SlideDateSelectedlistener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedDate = date;
                _tv_selectdate.setText(new DateFormatTitleFormatter().format(selectedDate));
            }
        }).setSlideOnMonthChangedListener(new MonthWeekMaterialCalendarView.SlideOnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

            }
        }).commit();

        AddDecorator();
    }

    private void clickWeekAnimator() {
        dropIndicator.startAniTo(0, 1, new BaseAnimatorlistener() {
            @Override
            public void onAnimationStart(Animator animator) {
                dropAnimat=true;
                _titlebar_month.setTextColor(getResources().getColor(R.color.white));
                _titlebar_week.setTextColor(getResources().getColor(R.color.white));
                monthWeekMaterialCalendarView.setAnimatStart(true);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                dropAnimat=false;
                _titlebar_month.setTextColor(getResources().getColor(R.color.white));
                _titlebar_week.setTextColor(getResources().getColor(R.color.colorPrimary));
                monthWeekMaterialCalendarView.setAnimatStart(false);
            }
        });
    }
    private  boolean dropAnimat;
    private void clickMonthAnimator() {
        dropIndicator.startAniTo(1, 0, new BaseAnimatorlistener() {
            @Override
            public void onAnimationStart(Animator animator) {
                dropAnimat=true;
                _titlebar_week.setTextColor(getResources().getColor(R.color.white));
                _titlebar_month.setTextColor(getResources().getColor(R.color.white));
                monthWeekMaterialCalendarView.setAnimatStart(true);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                dropAnimat=false;
                _titlebar_week.setTextColor(getResources().getColor(R.color.white));
                _titlebar_month.setTextColor(getResources().getColor(R.color.colorPrimary));
                monthWeekMaterialCalendarView.setAnimatStart(false);
            }
        });
    }

    private void AddDecorator() {
        //150天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -2);
        ArrayList<CalendarDay> dates = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            CalendarDay day = CalendarDay.from(calendar);
            dates.add(day);
            calendar.add(Calendar.DATE, 5);
        }
        //增加有红点标志
        monthWeekMaterialCalendarView.addDecorator(new EventDecorator(Color.RED, dates));
    }

    private void initRecyclerView() {
        adapter = new MultiTypeAdapter();
        adapter.register(SingleItem.class, new SingleItemViewBinder());
        /* Mock the data */
        recyclerView.setLayoutManager(new CustomLinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        items = new Items();
        for (int i = 0; i < 20; i++) {
            SingleItem textItem = new SingleItem();
            items.add(textItem);
        }
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }


    private void setWeekSelector() {
        clearTextSelect();
        //周模式时候设置回默认的DateFormatTitleFormatter 标题显示样式
        _tv_selectdate.setText(new DateFormatTitleFormatter().format(selectedDate));
        _titlebar_month.setSelected(false);
        _titlebar_week.setSelected(true);
        _titlebar_week.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void setMonthSelector() {
        clearTextSelect();
        //月模式时候设置回默认的DateFormatTitleFormatter 标题显示样式
        _tv_selectdate.setText(new DateFormatTitleFormatter().format(selectedDate));
        _titlebar_month.setSelected(true);
        _titlebar_month.setTextColor(getResources().getColor(R.color.colorPrimary));
        _titlebar_week.setSelected(false);
    }

    private void clearTextSelect() {
        _titlebar_week.setTextColor(getResources().getColor(R.color.white));
        _titlebar_month.setTextColor(getResources().getColor(R.color.white));
    }

    @OnClick(R.id.tv_previous)
    public void clickPrevious() {
        monthWeekMaterialCalendarView.goToPrevious();
    }

    @OnClick(R.id.tv_next)
    public void clickNext() {
        monthWeekMaterialCalendarView.goToNext();
    }

    @OnClick(R.id.tv_today)
    public void clickToday() {
        selectedDate = CalendarDay.today();
        monthWeekMaterialCalendarView.setCurrentDate(selectedDate);
        monthWeekMaterialCalendarView.setSelectedDate(selectedDate);
    }

    @OnClick(R.id.tv_setting)
    public void clickSetting() {
        CharSequence[] items = {
                "显示其他月份",
                "显示超出最大和最小日期范围",
                "能否选中其他月，选中会跳转",
                "能否竖直滑动",
                "能否左右滑动"
        };

        final int[] itemValues = {
                MaterialCalendarView.SHOW_OTHER_MONTHS,
                MaterialCalendarView.SHOW_OUT_OF_RANGE,
        };
        int showOtherDates = monthWeekMaterialCalendarView.getShowOtherDates();
        @SuppressWarnings("ResourceType")
        boolean[] initSelections = {
                MaterialCalendarView.showOtherMonths(showOtherDates),
                MaterialCalendarView.showOutOfRange(showOtherDates),
                monthWeekMaterialCalendarView.allowClickDaysOutsideCurrentMonth(),
                monthWeekMaterialCalendarView.isCanDrag(),
                canPaging
        };
        new AlertDialog.Builder(this)
                .setTitle("Show Other Dates")
                .setMultiChoiceItems(items, initSelections, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (which < 2) {
                            int showOtherDates = monthWeekMaterialCalendarView.getShowOtherDates();
                            if (isChecked) {
                                //Set flag
                                showOtherDates |= itemValues[which];
                            } else {
                                //Unset flag
                                showOtherDates &= ~itemValues[which];
                            }
                            monthWeekMaterialCalendarView.setShowOtherDates(showOtherDates);
                        } else if (which == 2) {
                            monthWeekMaterialCalendarView.setAllowClickDaysOutsideCurrentMonth(isChecked);
                        }
                        else if (which == 3) {
                            monthWeekMaterialCalendarView.setCanDrag(isChecked);
                        } else if (which == 4) {
                            canPaging=isChecked;
                            monthWeekMaterialCalendarView.setPagingEnabled(isChecked);
                        }

                    }
                })
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
    @OnClick(R.id.titlebar_month)
    public void clickMonth() {
        if (!_titlebar_month.isSelected()&&!monthWeekMaterialCalendarView.isAnimatStart()&&!dropAnimat) {
            setMonthSelector();
            monthWeekMaterialCalendarView.setMode(MonthWeekMaterialCalendarView.Mode.MONTH);
            clickMonthAnimator();

        }
    }
    @OnClick(R.id.titlebar_week)
    public void clickWeek() {
        if (!_titlebar_week.isSelected()&&!monthWeekMaterialCalendarView.isAnimatStart()&&!dropAnimat) {
            setWeekSelector();
            monthWeekMaterialCalendarView.setMode(MonthWeekMaterialCalendarView.Mode.WEEK);
            clickWeekAnimator();
        }
    }

}
