package com.amy.monthweekmaterialcalendarview;

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

import com.amy.monthweek.materialcalendarview.ILayoutManager;
import com.amy.monthweek.materialcalendarview.MonthWeekMaterialCalendarView;
import com.amy.monthweekmaterialcalendarview.decorators.ColorDecorator;
import com.amy.monthweekmaterialcalendarview.decorators.EnableOneToTenDecorator;
import com.amy.monthweekmaterialcalendarview.decorators.EventDecorator;
import com.amy.monthweekmaterialcalendarview.decorators.HighlightWeekendsDecorator;
import com.amy.monthweekmaterialcalendarview.decorators.MySelectorDecorator;
import com.amy.monthweekmaterialcalendarview.decorators.OneDayDecorator;
import com.amy.monthweekmaterialcalendarview.decorators.RemindDecorator;
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
 * Created by Administrator on 2018/1/4 0004.
 */

public class CalendarActivity extends AppCompatActivity {
    @BindView(R.id.slidelayout)
    MonthWeekMaterialCalendarView monthWeekMaterialCalendarView;
    @BindView(R.id.calendarView_week_mode)
    MaterialCalendarView calendarViewWeekMode;
    @BindView(R.id.calendarView_month_mode)
    MaterialCalendarView calendarViewMonthMode;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
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
                    if (!_titlebar_month.isSelected()) {
                        setMonthSelector();
                    }
                }
                if (mode.equals(MonthWeekMaterialCalendarView.Mode.WEEK)) {
                    if (!_titlebar_week.isSelected()) {
                        setWeekSelector();

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

    @OnClick(R.id.titlebar_month)
    public void clickMonth() {
        if (!_titlebar_month.isSelected()&&!monthWeekMaterialCalendarView.isAnimatStart()) {
            setMonthSelector();
            monthWeekMaterialCalendarView.setMode(MonthWeekMaterialCalendarView.Mode.MONTH);
        }
    }
    @OnClick(R.id.titlebar_week)
    public void clickWeek() {
        if (!_titlebar_week.isSelected()&&!monthWeekMaterialCalendarView.isAnimatStart()) {
            setWeekSelector();
            monthWeekMaterialCalendarView.setMode(MonthWeekMaterialCalendarView.Mode.WEEK);
        }
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
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    @OnClick(R.id.tv_decorator)
    public void clickdecorator() {

        new AlertDialog.Builder(this).setItems(new CharSequence[]{"普通效果","农历效果", "不同select颜色","带提醒图标","不允许点击","任意一张图背景"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                monthWeekMaterialCalendarView.removeDecorators();
                if (i == 0) {
                    monthWeekMaterialCalendarView.setShowLunar(false);
                }
                if (i == 1) {
                    monthWeekMaterialCalendarView.setShowLunar(true);
                }
                if (i == 2) {
                    int[] colors = new int []{Color.parseColor("#228BC34A")
                            ,Color.parseColor("#8BC34A")
                            ,Color.parseColor("#FFEB3B")
                            ,Color.parseColor("#00BCD4")
                            ,Color.parseColor("#BDBDBD")
                    };
                    monthWeekMaterialCalendarView.addDecorator(new ColorDecorator(new CalendarDay(2018,2,1),colors[1]));
                    monthWeekMaterialCalendarView.addDecorator(new ColorDecorator(new CalendarDay(2018,2,5),colors[2]));
                    monthWeekMaterialCalendarView.addDecorator(new ColorDecorator(new CalendarDay(2018,2,9),colors[3]));
                }
                if (i == 3) {
                    monthWeekMaterialCalendarView.addDecorator(new RemindDecorator(CalendarActivity.this));
                }
                if (i == 4) {
                    monthWeekMaterialCalendarView.addDecorator(new EnableOneToTenDecorator());
                }
                if (i == 5) {
                    monthWeekMaterialCalendarView.addDecorators( new MySelectorDecorator(CalendarActivity.this),
                            new HighlightWeekendsDecorator(),
                            oneDayDecorator);
                }



            }
        }).show();
    }
}

