package com.techtown.startui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;

class MyDateObj {

    private Calendar cal;
    private int year;
    private int month;
    private int firstDayOfWeek;
    private int lastDayOfMonth;

    MyDateObj() {

        this.cal = Calendar.getInstance();
        setMyDateObj(this.cal.get(Calendar.YEAR), this.cal.get(Calendar.MONTH));

    }

    void setCalendarDayOfMth(int dayOfMth) { this.cal.set(Calendar.DAY_OF_MONTH, dayOfMth); }

    Calendar getCalendar() { return this.cal; }

    int getFirstDayOfWeek() { return this.firstDayOfWeek; }

    int getLastDayOfMonth() { return this.lastDayOfMonth; }

    void addMonth(int incr) { if(incr != 0) setMyDateObj(this.year, this.month + incr); }

    private void setMyDateObj(int yr, int mth) {

        this.cal.set(Calendar.YEAR, this.year = yr);
        this.cal.set(Calendar.MONTH, this.month = mth);
        this.cal.set(Calendar.DAY_OF_MONTH, 1);

        this.firstDayOfWeek = this.cal.get(Calendar.DAY_OF_WEEK);
        this.lastDayOfMonth = this.cal.getActualMaximum(Calendar.DAY_OF_MONTH);

    }

}

public class CalendarActivity extends AppCompatActivity {

    Calendar calendar;
    TableLayout calendar_view;
    TextView prev_month, mth_banner, next_month;
    final MyDateObj mdo = new MyDateObj();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendar_view = findViewById(R.id.calendar_view);
        prev_month = findViewById(R.id.prev_month);
        mth_banner = findViewById(R.id.mth_banner);
        next_month = findViewById(R.id.next_month);
        calendar = Calendar.getInstance();

        setBannerMthCurrent();

        for(int i = 1, c = 1, li = calendar_view.getChildCount(); i < li; i++) {

            TableRow tr = (TableRow) calendar_view.getChildAt(i);

            for(int j = 0, lj = tr.getChildCount(); j < lj; j++) {

                LinearLayout cal_date = (LinearLayout) tr.getChildAt(j);
                cal_date.setTag("calendarCell:" + i + "," + j);

                TextView tv = new TextView(this);
                tv.setWidth(100);
                tv.setPadding(10, 0, 0, 0);
                tv.setTextColor(Color.rgb(255, 255, 255));
                tv.setText("");

                cal_date.addView(tv);
                cal_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String indexStr = view.getTag().toString();
                        indexStr = indexStr.substring(indexStr.indexOf(":") + 1);
                        String[] indexStrArr = indexStr.split(",");
                        int i = Integer.valueOf(indexStrArr[0]), j = Integer.valueOf(indexStrArr[1]);
                        String mthDateStr = ((TextView)((LinearLayout)((TableRow)calendar_view.getChildAt(i)).getChildAt(j)).getChildAt(0)).getText().toString();

                        if(mthDateStr.length() > 0) {

                            mdo.setCalendarDayOfMth(Integer.valueOf(mthDateStr));

                            ClassRoomData classRoomData = new ClassRoomData(mdo.getCalendar());
                            Intent intent = new Intent(getApplicationContext(), TimeTableActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("classRoomData", classRoomData);
                            intent.putExtras(bundle);
                            startActivity(intent);

                        }

                    }
                }); // onClickListener (cal_date)

            } // for (TableRow)

        } // for (calendar_view)

        moveMonth(0);

        prev_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                moveMonth(-1);

            }
        });

        next_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                moveMonth(1);

            }
        });

    } // onCreate

    private void setBannerMthCurrent() {

        Calendar cal = Calendar.getInstance();
        int yr = cal.get(Calendar.YEAR);
        int mth = cal.get(Calendar.MONTH) + 1;

        CharSequence txt = yr + "년 " + mth + "월";
        mth_banner.setText(txt);

    }

    private void MDOToBannerMth() {

        Calendar cal = mdo.getCalendar();
    
        int yr = cal.get(Calendar.YEAR);
        int mth = cal.get(Calendar.MONTH) + 1;

        CharSequence txt = yr + "년 " + mth + "월";
        mth_banner.setText(txt);

    }

    private void moveMonth(int addMth) {

        mdo.addMonth(addMth);

        MDOToBannerMth();

        for(int i = 1, iMax = calendar_view.getChildCount(); i < iMax; i++) {

            TableRow tr = (TableRow) calendar_view.getChildAt(i);

            for(int j = 0; j < 7; j++) {

                TextView tv = (TextView)((LinearLayout) tr.getChildAt(j)).getChildAt(0);

                tv.setText("");

            }

        }

        for(int i = 1, iMax = calendar_view.getChildCount(), dCnt = 1; i < iMax; i++) {

            TableRow tr = (TableRow) calendar_view.getChildAt(i);

            for(int j = (i == 1) ? (mdo.getFirstDayOfWeek() - 1) : 0, dMax = mdo.getLastDayOfMonth(); j < 7 && dCnt <= dMax; j++, dCnt++) {

                TextView tv = (TextView)((LinearLayout) tr.getChildAt(j)).getChildAt(0);

                tv.setText(String.valueOf(dCnt));

            }

        }

    }


    /* TODO: <1> Date Movement Funcs
     * [1] private void moveMonth(int addMth)
     * [2] private void setBannerMthCurrent() - DONE
     * [5] private void colorCurrentDate()
     *
     * TODO: <2> Click Activation Animation
     * [1] prev_month, next_month
     * [2] cal_date
     */

}
