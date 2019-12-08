package com.techtown.startui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class TimeTableActivity extends AppCompatActivity {

    TableLayout time_table;
    ArrayList<TextView> selectedCells;
    Boolean isAllSelected;
    ClassRoomData classRoomData;
    int numClassRoom;
    int prev_i, prev_j;

    DatabaseReference mRef, calendarRef;

    ArrayList<ArrayList<ClassRoomData>> cdList;
    HashMap<String, Integer> crMap;
    HashMap<Integer, Integer> timeMap;

    MyFirebase fb;
    String key;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        TextView time_table_banner = findViewById(R.id.time_table_banner);
        time_table = findViewById(R.id.time_table);
        selectedCells = new ArrayList<>();

        Intent intent = getIntent();
        classRoomData = (ClassRoomData)intent.getSerializableExtra("classRoomData");

        final TableRow headerList = findViewById(R.id.header_btn_list);
        ((Button)headerList.getChildAt(0)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), searchActivity.class);
                startActivity(intent);
            }
        });
        for(int i = 1; i < headerList.getChildCount(); i++) {
            headerList.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("classRoomData", classRoomData);
                    intent.putExtras(bundle);
                    intent.putExtra("favorite", ((Button)view).getText().toString());
                    startActivity(intent);
                }
            });
        }

        time_table_banner.setText(classRoomData.getYear() + "년 " + (classRoomData.getMonth() + 1) + "월 " + classRoomData.getDate() + "일");

        final int init_hr = 9;
        final int fin_hr = 20;

        prev_i = prev_j = 0; isAllSelected = false;

        String yr_mth = classRoomData.getYear() + "_" + (classRoomData.getMonth() + 1);
        String date = String.valueOf(classRoomData.getDate());

        mRef = FirebaseDatabase.getInstance().getReference();

        calendarRef = mRef.child("calendar").child(yr_mth).child(date);

        mRef.child("trigger").setValue(true);

        numClassRoom = ((TableRow)time_table.getChildAt(0)).getChildCount() - 1;

        cdList = new ArrayList<>(); timeMap = new HashMap<>(); crMap = new HashMap<>();

        for(int i = 1, clen = numClassRoom + 1, hr = init_hr; hr < fin_hr; hr++, i++) {

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

                        TextView cell = getCellFromTable(i, j);
                        if(cell.getText().toString().isEmpty()) {
                            if((j == prev_j || prev_j == 0) && (prev_i <= i)) {
                                TextView selection_message = findViewById(R.id.selection_message);
                                RelativeLayout to_reservation = findViewById(R.id.to_reservation);
                                if (isAllSelected) {
                                    cancel_selection();
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
                                            TextView tv = getCellFromTable(c, j);
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
                        } else {
                            Toast.makeText(getApplicationContext(), "이미 예약되어 있습니다.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                tr.addView(tv);
            }

            time_table.addView(tr);

        }

        for(int i = 0; i < numClassRoom; cdList.add(new ArrayList<ClassRoomData>()), i++);
        for(int i = 1; i <= numClassRoom; crMap.put(getCellFromTable(0, i).getText().toString(), i++));
        for(int i = 1, hr = init_hr; hr < fin_hr; timeMap.put(parse_time_range_by_row_index(i)[0], i++), hr++);

        fb = new MyFirebase(classRoomData);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fb.readReservationForTable(dataSnapshot, cdList, crMap);
                writeRsvnToTable(); clearRsvnTable();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        Button confirm_reserve = findViewById(R.id.confirm_reserve);
        confirm_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView classRoom = getCellFromTable(0, parse_tag(selectedCells.get(0).getTag().toString())[1]);
                classRoomData.setClassRoom(classRoom.getText().toString());

                int[] startTimeIndices = parse_tag(selectedCells.get(0).getTag().toString());
                int[] endTimeIndices = parse_tag(selectedCells.get(selectedCells.size() - 1).getTag().toString());

                int startTime = parse_time_range_by_row_index(startTimeIndices[0])[0];
                int endTime = parse_time_range_by_row_index(endTimeIndices[0])[1];

                classRoomData.setStartTimeHourToMs(startTime);
                classRoomData.setEndTimeHourToMs(endTime);

                Intent intent = new Intent(getApplicationContext(), ReservationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("classRoomData", classRoomData);
                intent.putExtras(bundle);
                startActivityForResult(intent, 3000);

            }
        });

        Button cancel_reserve = findViewById(R.id.cancel_reserve);
        cancel_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel_selection();
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
                startActivityForResult(intent, 2000);
            }
        });

    }

    private void cancel_selection() {
        for(int i = 0; i < selectedCells.size(); i++) {
            selectedCells.get(i).setSelected(false);
        }
        selectedCells.clear();
    }

    private int[] parse_tag(String tagStr) {
        int[] startEndTimes = new int[2];
        String[] indexStrArr = tagStr.substring(tagStr.indexOf(":") + 1).split(",");
        int i = Integer.valueOf(indexStrArr[0]), j = Integer.valueOf(indexStrArr[1]);
        startEndTimes[0] = i; startEndTimes[1] = j;
        return startEndTimes;
    }

    private int[] parse_time_range(String timeRange) {
        int[] timeRanges = new int[2];
        String[] timeRangeStrs;
        //XX:00~YY:00 >> XX:~YY: >> XX~YY
        timeRange = timeRange.replaceAll("00", "");
        timeRange = timeRange.replaceAll(":", "");
        timeRangeStrs = timeRange.split("~");
        if(timeRangeStrs[0].charAt(0) == '0') timeRangeStrs[0] = timeRangeStrs[0].replaceFirst("0", "");
        if(timeRangeStrs[1].charAt(0) == '0') timeRangeStrs[1] = timeRangeStrs[1].replaceFirst("0", "");
        timeRanges[0] = Integer.valueOf(timeRangeStrs[0]);
        timeRanges[1] = Integer.valueOf(timeRangeStrs[1]);
        return timeRanges;
    }

    private int[] parse_time_range_by_row_index(int row_index) {
        return parse_time_range(getCellFromTable(row_index, 0).getText().toString());
    }

    private TextView getCellFromTable(int i, int j) {
        return (TextView) ((TableRow)time_table.getChildAt(i)).getChildAt(j);
    }

    private void writeRsvnToTableCell(ClassRoomData crData) {
        if(crData != null) {
            int init_i = timeMap.get(crData.getStartTimeMsToHour());
            int fin_i = timeMap.get(crData.getEndTimeMsToHour() - 1);
            int j = crMap.get(crData.getClassRoom());
            TextView temp;
            for(int i = init_i; i <= fin_i; i++) {
                temp = getCellFromTable(i, j);
                temp.setText(String.valueOf(crData.getUserId()));
                temp.setBackgroundResource(R.drawable.reserved_table_cell_background);
            }
        }
    }
    private void writeRsvnToTable() {
        for(int i = 0, size_r = cdList.size(); i < size_r; i++) {
            for(int j = 0, size_c = cdList.get(i).size(); j < size_c; j++) {
                writeRsvnToTableCell(cdList.get(i).get(j));
            }
        }
    }

    private void clearRsvnTable() {
        for(int i = 0; i < cdList.size(); cdList.get(i++).clear());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case 3000:
                    classRoomData = (ClassRoomData)data.getSerializableExtra("classRoomData");

                    key = fb.writeReservation(classRoomData);

                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            fb.insert(dataSnapshot, key, classRoomData);
                            fb.readReservationForTable(dataSnapshot, cdList, crMap);
                            writeRsvnToTable(); clearRsvnTable();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });

                    mRef.child("trigger").setValue(Math.random());

                    cancel_selection(); prev_i = prev_j = 0; isAllSelected = false;
                    findViewById(R.id.to_reservation).setVisibility(View.INVISIBLE);

                    break;

                case 2000:
                    int clen = numClassRoom;
                    int rlen = time_table.getChildCount() - 1;
                    for(int i = 1; i < rlen; i++) {
                        for(int j = 1; j < clen; j++) {
                            TextView temp = getCellFromTable(i, j);
                            temp.setText("");
                            temp.setBackgroundResource(R.drawable.table_cell_background);
                        }
                    }
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            fb.readReservationForTable(dataSnapshot, cdList, crMap);
                            writeRsvnToTable(); clearRsvnTable();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                    break;
            }
        } else if(resultCode == RESULT_CANCELED) {
            switch (requestCode) {
                case 3000:

                    Toast.makeText(getApplicationContext(), "예약이 취소되었습니다.", Toast.LENGTH_SHORT).show();

                    cancel_selection();
                    prev_i = prev_j = 0;
                    isAllSelected = false;
                    findViewById(R.id.to_reservation).setVisibility(View.INVISIBLE);

                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(selectedCells.size() > 0) {
            cancel_selection();
            prev_i = prev_j = 0;
            isAllSelected = false;
            findViewById(R.id.selection_message).setVisibility(View.INVISIBLE);
            findViewById(R.id.to_reservation).setVisibility(View.INVISIBLE);
        }
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("classRoomData", classRoomData);
        resultIntent.putExtras(bundle);
        setResult(RESULT_OK, resultIntent);
        finish();
        super.onBackPressed();
    }
}
