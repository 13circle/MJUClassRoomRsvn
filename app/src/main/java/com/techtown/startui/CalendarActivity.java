package com.techtown.startui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
                tv.setTextColor(Color.rgb(0, 0, 255));
                tv.setText(String.valueOf(c++));

                cal_date.addView(tv);
                cal_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String indexStr = view.getTag().toString();
                        indexStr = indexStr.substring(indexStr.indexOf(":") + 1);
                        String[] indexStrArr = indexStr.split(",");
                        int i = Integer.valueOf(indexStrArr[0]), j = Integer.valueOf(indexStrArr[1]);
                        LinearLayout ll = (LinearLayout)((TableRow)calendar_view.getChildAt(i)).getChildAt(j);
                        if(ll.getChildCount() > 0) {
                            String mthDateStr = ((TextView) ll.getChildAt(0)).getText().toString();
                            int mthDate = Integer.valueOf(mthDateStr);
                            Toast.makeText(getApplicationContext(), String.valueOf(mthDate), Toast.LENGTH_SHORT).show();
                        }

                    }
                }); // onClickListener (cal_date)

            } // for (TableRow)

        } // for (calendar_view)

        prev_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Pass -1 as a param to show prev mth
            }
        });

        next_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Pass 1 as a param to show next mth
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

    private Calendar getBannerMth() {

        Calendar cal = Calendar.getInstance();

        String txt = mth_banner.getText().toString();
        txt = txt.substring(0, txt.indexOf("월"));
        String[] txts = txt.split("년 ");

        cal.set(Calendar.YEAR, Integer.valueOf(txts[0]));
	cal.set(Calendar.MONTH, Integer.valueOf(txts[1]) - 1);

        return cal;

    }

    private void serBannerMonth(Calendar cal) {
    
        int yr = get(Calendar.YEAR);
	int mth = get(Calendar.MONTH) + 1;

	CharSequence txt = yr + ""
	mth_banner.setText(yr + "년 " + mth + "월");

    }

    private int[] getFirstLastDate(Calendar cal) {

        int[] tempArr = new int[2];

        cal.set(Calendar.DAY_OF_MONTH, 1);
        tempArr[0] = cal.get(Calendar.DAY_OF_WEEK);
        tempArr[1] = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        return tempArr;
    }

    private void moveMonth(int addMth) {

        // TODO: Display month in mth_banner
	
	Calendar cal = Calendar.getInstance();
	int 

    }


    /* TODO: <1> Date Movement Funcs
     * [1] private void moveMonth(int addMth)
     * [2] private void setBannerMthCurrent() - DONE
     * [3] private int[] getBannerMth() - DONE
     * [4] private void setBannerMth(Calendar cal) - DONE
     * [5] private int[] getFirstLastDate(Calendar cal) - DONE
     * [6] private void colorCurrentDate()
     *
     * TODO: <2> Click Activation Animation
     * [1] prev_month, next_month
     * [2] cal_date
     */

}
