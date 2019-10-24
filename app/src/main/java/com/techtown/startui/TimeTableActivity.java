package com.techtown.startui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.PriorityQueue;

public class TimeTableActivity extends AppCompatActivity {

    TableLayout time_table;
    ArrayList<TextView> selectedCells;
    Boolean isAllSelected;
    ClassRoomData classRoomData;
    int prev_i, prev_j;

    PriorityQueue<TextView> PQ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        TextView time_table_banner = findViewById(R.id.time_table_banner);
        time_table = findViewById(R.id.time_table);
        selectedCells = new ArrayList<>();

        Intent intent = getIntent();
        classRoomData = (ClassRoomData)intent.getSerializableExtra("classRoomData");

        time_table_banner.setText(classRoomData.getYear() + "년 " + (classRoomData.getMonth() + 1) + "월 " + classRoomData.getDate() + "일");

        PQ = new PriorityQueue<TextView>();

        final int init_hr = 9;
        final int fin_hr = 20;

        prev_i = prev_j = 0; isAllSelected = false;

        for(int i = 1, hr = init_hr, clen = ((TableRow)time_table.getChildAt(0)).getChildCount(); hr < fin_hr; hr++, i++) {

            TableRow tr = new TableRow(this);
            TextView tv = new TextView(this);

            String startTime = ((hr < 10) ? ("0" + hr) : hr) + ":00";
            String endTime = (((hr + 1) < 10) ? ("0" + (hr + 1)) : (hr + 1)) + ":00";
            tv.setText(startTime + "~" + endTime);
            tv.setBackgroundResource(R.drawable.table_header_cell_background);
            tv.setGravity(Gravity.CENTER);
            tr.addView(tv);

            for(int j = 1; j < clen; j++) {
                tv = new TextView(this);
                tv.setBackgroundResource(R.drawable.table_cell_background);
                tv.setGravity(Gravity.CENTER); tv.setClickable(true);
                tv.setTag("time_table:" + i + "," + j);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String indexStr = view.getTag().toString();
                        indexStr = indexStr.substring(indexStr.indexOf(":") + 1);
                        String[] indexStrArr = indexStr.split(",");
                        int i = Integer.valueOf(indexStrArr[0]), j = Integer.valueOf(indexStrArr[1]);


                            TextView cell = (TextView) ((TableRow)time_table.getChildAt(i)).getChildAt(j);
                            if((j == prev_j || prev_j == 0) && (prev_i <= i)) {
                            TextView selection_message = findViewById(R.id.selection_message);
                            RelativeLayout to_reservation = findViewById(R.id.to_reservation);

                            if (isAllSelected) {
                                for (int c = 0; c < selectedCells.size(); c++) {
                                    selectedCells.get(c).setSelected(false);
                                }
                                selectedCells.clear();
                                prev_i = prev_j = 0;
                                isAllSelected = false;

                                selection_message.setVisibility(View.INVISIBLE);
                                to_reservation.setVisibility(View.INVISIBLE);

                            } else {
                                if (selectedCells.size() == 0) {
                                    selectedCells.add(cell);
                                    cell.setSelected(true);

                                    selection_message.setVisibility(View.VISIBLE);

                                } else {
                                    for (int c = prev_i + 1; c <= i; c++) {
                                        TextView tv = (TextView) ((TableRow) time_table.getChildAt(c)).getChildAt(j);
                                        tv.setSelected(true);
                                        selectedCells.add(tv);
                                    }
                                    isAllSelected = true;
                                    selection_message.setVisibility(View.INVISIBLE);
                                    to_reservation.setVisibility(View.VISIBLE);
                                }
                                prev_i = i;
                                prev_j = j;
                            }
                        } else if(prev_i > i) {
                            Toast.makeText(getApplicationContext(), "종료 시간은 시작 시간 뒤에 와야 합니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "같은 강의실의 시간대를 먼저 설정해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                tr.addView(tv);
            }

            time_table.addView(tr);

        }

        Button confirm_reserve = findViewById(R.id.confirm_reserve);
        confirm_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TextView cell = (TextView) ((TableRow)time_table.getChildAt(i)).getChildAt(j);

                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference myResvList = mRef.child("users").child(String.valueOf(classRoomData.getUserId())).child("myResvList");
                DatabaseReference calendarRef = mRef.child("calendar").child(classRoomData.getYear() + "_" + (classRoomData.getMonth() + 1)).child(String.valueOf(classRoomData.getDate()));
                DatabaseReference resvRef = mRef.child("reservations");
                String key = myResvList.push().getKey();
                myResvList.child(key).setValue(true); calendarRef.child(key).setValue(true);
                resvRef.child(key).child("userId").setValue(classRoomData.getUserId());

                Intent intent = new Intent(getApplicationContext(), ReservationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("classRoomData", classRoomData);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        Button cancel_reserve = findViewById(R.id.cancel_reserve);
        cancel_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int c = 0; c < selectedCells.size(); c++) {
                    selectedCells.get(c).setSelected(false);
                }
                selectedCells.clear();
                prev_i = prev_j = 0;
                isAllSelected = false;

                findViewById(R.id.to_reservation).setVisibility(View.INVISIBLE);
            }
        });

        Button to_mypage2 = findViewById(R.id.to_mypage2);
        to_mypage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("classRoomData", classRoomData);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    private int[] parse_txtv_tag() {

        int[] startEndTimes = new int[2];
        /*
        String indexStr = view.getTag().toString();
        indexStr = indexStr.substring(indexStr.indexOf(":") + 1);
        String[] indexStrArr = indexStr.split(",");
        int i = Integer.valueOf(indexStrArr[0]), j = Integer.valueOf(indexStrArr[1]);

         */
        return startEndTimes;
    }
}
