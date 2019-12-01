package com.techtown.startui;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class MyPageActivity extends AppCompatActivity {

    ClassRoomData classRoomData;

    DatabaseReference mRef;

    TextView show_user_name, show_user_id, show_user_email, show_phone_number;
    TextView show_date, show_time, show_purpose, show_place, show_personnel;

    Button cancel_button;

    String current_uid;
    long current_start_time;

    boolean isCancelled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        mRef = FirebaseDatabase.getInstance().getReference();

        show_user_name = findViewById(R.id.show_user_name);
        show_user_id = findViewById(R.id.show_user_id);
        show_user_email = findViewById(R.id.show_user_email);
        show_phone_number = findViewById(R.id.show_phone_number);
        show_date = findViewById(R.id.show_date);
        show_time = findViewById(R.id.show_time);
        show_purpose = findViewById(R.id.show_purpose);
        show_place = findViewById(R.id.show_place);
        show_personnel = findViewById(R.id.show_personnel);

        cancel_button = findViewById(R.id.cancelButton);

        current_uid = ""; current_start_time = 0; isCancelled = false;

        Intent intent = getIntent();
        classRoomData = (ClassRoomData)intent.getSerializableExtra("classRoomData");

        mRef.child("trigger").setValue(true);

        String msg = "예약 없음";
        show_place.setText(msg);
        show_personnel.setText(msg);
        show_purpose.setText(msg);
        show_time.setText(msg);
        show_date.setText(msg);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                show_user_name.setText(classRoomData.getUserName());
                show_user_id.setText(String.valueOf(classRoomData.getUserId()));
                show_user_email.setText(classRoomData.getUserEmail());
                show_phone_number.setText(classRoomData.getPhoneNumber());

                DataSnapshot userRef = dataSnapshot.child("users").child(String.valueOf(classRoomData.getUserId()));
                DataSnapshot myResvList = userRef.child("myResvList");
                DataSnapshot rsvnRef = dataSnapshot.child("reservations");

                HashMap<Long, String> keyMap = new HashMap<>();
                ArrayList<Long> startTimeList = new ArrayList<>();
                for(DataSnapshot ds : myResvList.getChildren()) {
                    Long startTime = rsvnRef.child(ds.getKey()).child("startTime").getValue(Long.class);
                    keyMap.put(startTime, ds.getKey());
                    startTimeList.add(startTime);
                }

                if(!startTimeList.isEmpty()) {

                    Collections.sort(startTimeList);

                    current_uid = keyMap.get(startTimeList.get(startTimeList.size() - 1));
                    DataSnapshot currentRsvnRef = rsvnRef.child(current_uid);

                    current_start_time = currentRsvnRef.child("startTime").getValue(Long.class);
                    classRoomData.setStartTime(current_start_time);
                    classRoomData.setEndTime(currentRsvnRef.child("endTime").getValue(Long.class));
                    classRoomData.setClassRoom(currentRsvnRef.child("classRoom").getValue(String.class));
                    classRoomData.setNumUsers(currentRsvnRef.child("numUsers").getValue(Integer.class));
                    classRoomData.setUsage(currentRsvnRef.child("usage").getValue(String.class));

                    show_place.setText(classRoomData.getClassRoom());
                    show_personnel.setText(String.valueOf(classRoomData.getNumUsers()));
                    show_purpose.setText(classRoomData.getUsage());
                    show_time.setText(classRoomData.getStartTimeMsToHour() + ":00~" + classRoomData.getEndTimeMsToHour() + ":00");

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(classRoomData.getStartTime());
                    show_date.setText(calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(current_uid.isEmpty() || current_start_time == 0)) {
                    isCancelled = true;

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(current_start_time);
                    mRef.child("calendar").child(calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1))
                            .child(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))).child(current_uid).removeValue();
                    mRef.child("reservations").child(current_uid).removeValue();
                    mRef.child("users").child(String.valueOf(classRoomData.getUserId()))
                            .child("myResvList").child(current_uid).removeValue();

                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            show_user_name.setText(classRoomData.getUserName());
                            show_user_id.setText(String.valueOf(classRoomData.getUserId()));
                            show_user_email.setText(classRoomData.getUserEmail());
                            show_phone_number.setText(classRoomData.getPhoneNumber());

                            DataSnapshot userRef = dataSnapshot.child("users").child(String.valueOf(classRoomData.getUserId()));
                            DataSnapshot myResvList = userRef.child("myResvList");
                            DataSnapshot rsvnRef = dataSnapshot.child("reservations");

                            HashMap<Long, String> keyMap = new HashMap<>();
                            ArrayList<Long> startTimeList = new ArrayList<>();
                            for(DataSnapshot ds : myResvList.getChildren()) {
                                Long startTime = rsvnRef.child(ds.getKey()).child("startTime").getValue(Long.class);
                                keyMap.put(startTime, ds.getKey());
                                startTimeList.add(startTime);
                            }

                            if(!startTimeList.isEmpty()) {

                                Collections.sort(startTimeList);

                                current_uid = keyMap.get(startTimeList.get(startTimeList.size() - 1));
                                DataSnapshot currentRsvnRef = rsvnRef.child(current_uid);

                                current_start_time = currentRsvnRef.child("startTime").getValue(Long.class);
                                classRoomData.setStartTime(current_start_time);
                                classRoomData.setEndTime(currentRsvnRef.child("endTime").getValue(Long.class));
                                classRoomData.setClassRoom(currentRsvnRef.child("classRoom").getValue(String.class));
                                classRoomData.setNumUsers(currentRsvnRef.child("numUsers").getValue(Integer.class));
                                classRoomData.setUsage(currentRsvnRef.child("usage").getValue(String.class));

                                show_place.setText(classRoomData.getClassRoom());
                                show_personnel.setText(String.valueOf(classRoomData.getNumUsers()));
                                show_purpose.setText(classRoomData.getUsage());
                                show_time.setText(classRoomData.getStartTimeMsToHour() + ":00~" + classRoomData.getEndTimeMsToHour() + ":00");

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(classRoomData.getStartTime());
                                show_date.setText(calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH));
                            } else {
                                String msg = "예약 없음";
                                show_place.setText(msg);
                                show_personnel.setText(msg);
                                show_purpose.setText(msg);
                                show_time.setText(msg);
                                show_date.setText(msg);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //
                        }
                    });
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        if(isCancelled) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("classRoomData", classRoomData);
            resultIntent.putExtras(bundle);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else super.onBackPressed();
    }
}
