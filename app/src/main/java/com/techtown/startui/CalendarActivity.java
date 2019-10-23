package com.techtown.startui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    Calendar calendar;
    TableLayout calendar_view;
    TextView prev_month, mth_banner, next_month;
    ClassRoomData classRoomData;
    final MyDateObj mdo = new MyDateObj();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Intent intent = getIntent();
        classRoomData = (ClassRoomData) intent.getSerializableExtra("classRoomData");

        calendar_view = findViewById(R.id.calendar_view);
        prev_month = findViewById(R.id.prev_month);
        mth_banner = findViewById(R.id.mth_banner);
        next_month = findViewById(R.id.next_month);
        calendar = Calendar.getInstance();

        setBannerMthCurrent();

        for(int i = 2, li = calendar_view.getChildCount(); i < li; i++) {

            TableRow tr = (TableRow) calendar_view.getChildAt(i);

            for(int j = 0, lj = tr.getChildCount(); j < lj; j++) {

                LinearLayout cal_date = (LinearLayout) tr.getChildAt(j);
                cal_date.setTag("calendarCell:" + i + "," + j);
                cal_date.setClickable(true); cal_date.setOrientation(LinearLayout.VERTICAL);

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

                            switch(j + 1) {

                                case 1: case 7:
                                    Toast.makeText(getApplicationContext(), "주말은 예약할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    break;

                                default:
                                    mdo.setCalendarDayOfMth(Integer.valueOf(mthDateStr));

                                    classRoomData.setCalendar(mdo.getCalendar());
                                    Intent intent = new Intent(getApplicationContext(), TimeTableActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("classRoomData", classRoomData);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                            }

                        }

                    } // onClick
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

        Button to_mypage = findViewById(R.id.to_mypage);
        to_mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("classRoomData", classRoomData);
                intent.putExtras(bundle);
                startActivity(intent);

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

        for(int i = 2, iMax = calendar_view.getChildCount(); i < iMax; i++) {

            TableRow tr = (TableRow) calendar_view.getChildAt(i);

            for(int j = 0; j < 7; j++) {

                TextView tv = (TextView)((LinearLayout) tr.getChildAt(j)).getChildAt(0);

                tv.setText("");

            }

        }

        Calendar cal = Calendar.getInstance();

        for(int i = 2, iMax = calendar_view.getChildCount(), dCnt = 1; i < iMax; i++) {

            TableRow tr = (TableRow) calendar_view.getChildAt(i);

            for(int j = (i == 2) ? (mdo.getFirstDayOfWeek() - 1) : 0, dMax = mdo.getLastDayOfMonth(); j < 7 && dCnt <= dMax; j++, dCnt++) {

                LinearLayout ll = (LinearLayout) tr.getChildAt(j);

                TextView tv = (TextView)ll.getChildAt(0);

                int yr = cal.get(Calendar.YEAR), yr2 = mdo.getCalendar().get(Calendar.YEAR);
                int mth = cal.get(Calendar.MONTH), mth2 = mdo.getCalendar().get(Calendar.MONTH);
                int date = cal.get(Calendar.DAY_OF_MONTH);

                if(yr == yr2 && mth == mth2 && date == dCnt) ll.setBackgroundResource(R.drawable.calendar_cell_today_background);
                else ll.setBackgroundResource(R.drawable.calendar_cell_background);

                tv.setText(String.valueOf(dCnt));

            }

        }

    }

}
