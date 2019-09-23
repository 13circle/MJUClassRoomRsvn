package com.example.mjuclassroomrsvn;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    TableLayout calendarTable;
    TableLayout.LayoutParams tableRowParams;
    TableRow tableRow;
    TextView monthBanner;
    ImageView arrowLeft, arrowRight;

    static int row, column;
    static boolean isClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        this.setTitle("예약하실 날짜를 선택하세요");

        calendarTable = findViewById(R.id.calendarTable);
        tableRowParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        monthBanner = findViewById(R.id.monthBanner);

        arrowLeft = findViewById(R.id.arrowLeft);
        arrowLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setSelected(event.getAction()==MotionEvent.ACTION_DOWN);
                Calendar cal = Calendar.getInstance();
                String[] yrMth = ((String)monthBanner.getText()).split(".");
                cal.set(Calendar.YEAR, Integer.valueOf(yrMth[0]));
                cal.set(Calendar.MONTH, Integer.valueOf(yrMth[1]));
                cal.add(Calendar.MONTH, -1);
                setMonthCalendar(cal);
                return true;
            }
        });
        arrowRight = findViewById(R.id.arrowRight);
        arrowRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setSelected(event.getAction()==MotionEvent.ACTION_DOWN);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, 1);
                setMonthCalendar(cal);
                return true;
            }
        });

        setMonthCalendar(Calendar.getInstance());

    }

    private void setMonthCalendar(Calendar cal) {
        int i, j, d, dMax, yr, mth; isClicked = false;

        calendarTable.removeAllViews();

        yr = cal.get(Calendar.YEAR);
        mth = cal.get(Calendar.MONTH);
        monthBanner.setText(yr + "." + (mth + 1));
        Toast.makeText(getApplicationContext(), cal.get(Calendar.YEAR) + "/" + cal.get(Calendar.MONTH), Toast.LENGTH_SHORT).show();
        dMax = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        j = cal.get(Calendar.DAY_OF_WEEK) - 1;
        for(i = 0, d = 1; i < 5; i++) {
            tableRow = new TableRow(this);
            tableRow.setLayoutParams(tableRowParams);
            if(i != 0 || ((j == 0) && (i == 0)))  for(j = 0; j < 7 && d <= dMax; tableRow.addView(getTextView(d++, i, j), j++));
            else {
                for(int t = 0; t < j; tableRow.addView(getTextView(0, i, t), t++));
                while(j < 7) tableRow.addView(getTextView(d++, i, j), j++);
            }
            calendarTable.addView(tableRow, i);
        }

    }

    private TextView getTextView(int d, final int i, final int j) {

        final TextView tv = new TextView(this);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv.setTextColor(getResources().getColor(android.R.color.black));
        tv.setPadding(18, 18, 18, 18);
        tv.setTextSize(12);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        tv.setHeight(300);
        tv.setText(String.valueOf((d > 0) ? d : ""));
        if(d > 0) {
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv.setBackgroundColor(Color.rgb(175, 177, 179));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                    if(isClicked) {
                        TableRow tr = (TableRow) calendarTable.getChildAt(row);
                        TextView td = (TextView) tr.getChildAt(column);
                        td.setBackgroundColor(Color.rgb(138, 182, 225));
                        td.setTypeface(Typeface.DEFAULT);
                        if((row == i) && (column == j)) isClicked = false;
                    } else isClicked = true;
                    row = i; column = j;
                }
            });
        }
        return tv;

    }
}
