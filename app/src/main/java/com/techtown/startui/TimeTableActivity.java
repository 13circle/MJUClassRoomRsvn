package com.techtown.startui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutput;
import java.util.ArrayList;

public class TimeTableActivity extends AppCompatActivity {

    TableLayout time_table;
    ArrayList<TextView> selectedCells;
    Boolean isAllSelected;
    int prev_i, prev_j;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        TextView time_table_banner = findViewById(R.id.time_table_banner);
        time_table = findViewById(R.id.time_table);
        selectedCells = new ArrayList<>();

        Intent intent = getIntent();
        ClassRoomData CRdata = (ClassRoomData)intent.getSerializableExtra("classRoomData");

        time_table_banner.setText(CRdata.getYear() + "년 " + (CRdata.getMonth() + 1) + "월 " + CRdata.getDate() + "일");

        final int init_hr = 9;
        final int fin_hr = 20;

        prev_i = prev_j = 0; isAllSelected = false;

        for(int i = 1, hr = init_hr, clen = ((TableRow)time_table.getChildAt(0)).getChildCount(); hr < fin_hr; hr++, i++) {

            TableRow tr = new TableRow(this);
            TextView tv = new TextView(this);

            String startTime = ((hr < 10) ? ("0" + hr) : hr) + ":00";
            String endTime = (((hr + 1) < 10) ? ("0" + (hr + 1)) : (hr + 1)) + ":00";
            tv.setText(startTime + "~" + endTime);
            tv.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.table_header_cell_background, null));
            tv.setGravity(Gravity.CENTER);
            tr.addView(tv);

            for(int j = 1; j < clen; j++) {
                tv = new TextView(this);
                tv.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.table_cell_background, null));
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
                        if(j == prev_j || prev_j == 0) {
                            if (isAllSelected) {
                                for(int c = 0; c < selectedCells.size(); c++) {
                                    selectedCells.get(c).setSelected(false);
                                }
                                selectedCells.clear();
                                prev_i = prev_j = 0;
                                isAllSelected = false;
                            } else {
                                if(selectedCells.size() == 0) {
                                    selectedCells.add(cell);
                                    cell.setSelected(true);
                                } else {
                                    for(int c = prev_i + 1; c <= i; c++) {
                                        TextView tv = (TextView) ((TableRow)time_table.getChildAt(c)).getChildAt(j);
                                        tv.setSelected(true);
                                        selectedCells.add(tv);
                                    }
                                    isAllSelected = true;
                                }
                                prev_i = i; prev_j = j;
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "같은 강의실의 시간대를 먼저 설정해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                tr.addView(tv);
            }

            time_table.addView(tr);

        }

        Button to_mypage = findViewById(R.id.to_mypage);
        to_mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClassRoomData classRoomData = new ClassRoomData();
                Intent intent = new Intent(getApplicationContext(), mypage.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("classRoomData", classRoomData);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }
}
