package com.techtown.startui;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class MyPageActivity extends AppCompatActivity {

    ClassRoomData classRoomData;

    DatabaseReference mRef;

    MyFirebase fb;

    TextView show_user_name, show_user_id, show_user_email, show_phone_number;

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

        current_uid = ""; current_start_time = 0; isCancelled = false;

        Intent intent = getIntent();
        classRoomData = (ClassRoomData)intent.getSerializableExtra("classRoomData");

        mRef.child("trigger").setValue(true);

        show_user_name.setText(classRoomData.getUserName());
        show_user_id.setText(String.valueOf(classRoomData.getUserId()));
        show_user_email.setText(classRoomData.getUserEmail());
        show_phone_number.setText(classRoomData.getPhoneNumber());

        fb = new MyFirebase(classRoomData);

        fb.writeFavoriteClassRoom(intent.getStringExtra("favorite"));

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> favList = fb.readFavoriteClassRoom(dataSnapshot);
                for(int i = 0; i < favList.size(); addFavoriteClassRoomBtn(favList.get(i++)));
                ArrayList<ArrayList<String>> rsvnList = fb.readReservationForUser(dataSnapshot);
                for(int i = 0; i < rsvnList.size(); addReservationView(rsvnList.get(i++)));
            } // onDataChange
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addFavoriteClassRoomBtn(String favCR) {
        LinearLayout favList = findViewById(R.id.favorite_list);
        Button fav = new Button(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(convertDPtoPX(15), 0, convertDPtoPX(15), 0);
        fav.setLayoutParams(lp);
        fav.setBackgroundResource(R.drawable.button_selector_white);
        fav.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
        fav.setTextColor(Color.rgb(0, 0, 0));
        fav.setText(favCR);
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fb.deleteFavoriteClassRoom(((Button)view).getText().toString());
                LinearLayout favList = findViewById(R.id.favorite_list);
                favList.removeView(view);
            }
        });
        favList.addView(fav);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addReservationView(ArrayList<String> rsvnInfo) {
        LinearLayout rsvn_list = findViewById(R.id.my_rsvn_list);

        TableLayout rsvn = new TableLayout(getApplicationContext());
        rsvn.setColumnStretchable(1, true);
        rsvn.setBackgroundResource(R.color.colorPrimaryDark);
        int px_dp = convertDPtoPX(15);
        rsvn.setPadding(px_dp, px_dp, px_dp, px_dp);
        TableLayout.LayoutParams tlp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tlp.setMargins(0, 0, 0, convertDPtoPX(10));
        rsvn.setLayoutParams(tlp);

        TableRow[] trArr = new TableRow[5];
        for(int i = 0; i < 5; trArr[i++] = new TableRow(getApplicationContext()));
        for(int i = 0; i < 5; i++) {
            String txt1, txt2; txt1 = txt2 = "";
            TextView tv1 = new TextView(getApplicationContext());
            TextView tv2 = new TextView(getApplicationContext());
            int defaultTxtClr = Color.rgb(255, 255, 255);
            int defaultTxtAppr = R.style.TextAppearance_AppCompat_Medium;
            tv1.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, convertDPtoPX(40))); tv1.setTextColor(defaultTxtClr);
            tv1.setTextAppearance(defaultTxtAppr); tv1.setGravity(Gravity.CENTER); tv1.setTextColor(defaultTxtClr);
            tv2.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, convertDPtoPX(40))); tv2.setTextColor(defaultTxtClr);
            tv2.setTextAppearance(defaultTxtAppr); tv2.setGravity(Gravity.CENTER); tv2.setTextColor(defaultTxtClr);
            trArr[i].addView(tv1); trArr[i].addView(tv2);
            switch(i) {
                case 0:
                    txt1 = "장소"; txt2 = rsvnInfo.get(0);
                    Button cancel_button = new Button(getApplicationContext());
                    cancel_button.setLayoutParams(new TableRow.LayoutParams(convertDPtoPX(46), ViewGroup.LayoutParams.WRAP_CONTENT));
                    cancel_button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    cancel_button.setBackgroundResource(R.drawable.button_selector_circle);
                    cancel_button.setTextAppearance(R.style.TextAppearance_AppCompat_Large);
                    cancel_button.setText("X"); cancel_button.setTextColor(Color.rgb(0, 0, 0));
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            isCancelled = true;

                            TableLayout tl = (TableLayout) ((ViewGroup) view.getParent()).getParent();
                            String date_str = ((TextView)((TableRow)(tl.getChildAt(1))).getChildAt(1)).getText().toString();
                            String time_str = ((TextView)((TableRow)(tl.getChildAt(2))).getChildAt(1)).getText().toString();

                            int[] dateArr = parseDateStrToInt(date_str);
                            int[] timeArr = parseTimeStrToInt(time_str);

                            Calendar cal = Calendar.getInstance();
                            cal.set(dateArr[0], (dateArr[1] - 1), dateArr[2]);
                            cal.set(Calendar.HOUR_OF_DAY, timeArr[0] - 1); cal.set(Calendar.MINUTE, 0);

                            classRoomData.setCalendar(cal);
                            classRoomData.setStartTime(cal.getTimeInMillis());

                            mRef.child("trigger").setValue(true);

                            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    fb.deleteReservation(dataSnapshot, classRoomData);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                            });

                            ((LinearLayout)findViewById(R.id.my_rsvn_list)).removeView(tl);
                        }
                    });
                    trArr[0].addView(cancel_button);
                    break;
                case 1:
                    txt1 = "날짜"; txt2 = rsvnInfo.get(1);
                    break;
                case 2:
                    txt1 = "시간"; txt2 = rsvnInfo.get(2);
                    break;
                case 3:
                    txt1 = "인원"; txt2 = rsvnInfo.get(3);
                    break;
                case 4:
                    txt1 = "이용목적"; txt2 = rsvnInfo.get(4);
                    break;
            } // switch
            tv1.setText(txt1); tv2.setText(txt2);
        } // for
        for(int i = 0; i < 5; rsvn.addView(trArr[i++]));
        rsvn_list.addView(rsvn);
    }

    private int convertDPtoPX(int dp_value) {
        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        return (int)(dp_value * scale + 0.5f);
    }

    private int[] parseDateStrToInt(String date_str) {
        // XXXX/XX/XX >> XXXX, XX, XX
        String[] strs = date_str.split("/");
        int[] dates = new int[3];
        for(int i = 0; i < strs.length; dates[i] = Integer.valueOf(strs[i++]));
        return dates;
    }

    private int[] parseTimeStrToInt(String time_str) {
        // XX:00~XX:00 >> XX, XX
        String[] strs = time_str.split(":00~");
        int[] times = new int[2];
        strs[1] = strs[1].substring(0, strs[1].lastIndexOf(":"));
        for(int i = 0; i < strs.length; times[i] = Integer.valueOf(strs[i++]));
        Log.i("parseTimeStrToInt", times[0] + ":" + times[1]);
        return times;
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
