package com.amy.monthweekmaterialcalendarview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button)
    public void goCalendarSmoothActivity() {
        Intent intent=new Intent(this,CalendarSmoothActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.button2)
    public void goCalendarActivity() {
        Intent intent=new Intent(this,CalendarActivity.class);
        startActivity(intent);
    }


}
