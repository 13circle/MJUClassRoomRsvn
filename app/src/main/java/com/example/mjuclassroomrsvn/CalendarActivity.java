package com.example.mjuclassroomrsvn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                switch(dayOfWeek) {
                    case Calendar.SATURDAY:
                    case Calendar.SUNDAY:
                        Toast.makeText(getApplicationContext(), "주말은 예약하실 수 없습니다!", Toast.LENGTH_SHORT);
                        return;
                    default: break;
                }

                /* @TODO : Move to [RESERVATION_CLASS] as below:
                Intent intent = new Intent(getApplicationContext(), [RESERVATION_CLASS].class);
                intent.putExtra("year", year);
                intent.putExtra("month", month + 1);
                intent.putExtra("dayOfMonth", dayOfMonth);
                startActivity(intent);
                 */

                /* @TODO : [RESERVATION_CLASS] must receive data as below:
                * Intent intent = getIntent();
                * int year = intent.getExtras().getInt("year");
                * int month = intent.getExtras().getInt("month");
                * int dayOfMonth = intent.getExtras().getInt("dayOfMonth");
                * */

            }
        });
    }
}
