package com.techtown.startui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class searchActivity extends AppCompatActivity {

    EditText startHourTxt, endHourTxt;
    Button submitBtn;

    ClassRoomData classRoomData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        startHourTxt = findViewById(R.id.search_start_hour);
        endHourTxt = findViewById(R.id.search_end_hour);
        submitBtn = findViewById(R.id.search_submit);

        Intent intent = getIntent();
        classRoomData = (ClassRoomData)intent.getSerializableExtra("classRoomData");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!startHourTxt.getText().toString().equals("") && !endHourTxt.getText().toString().equals("")) {
                    int stHour = Integer.valueOf(startHourTxt.getText().toString());
                    int endHour = Integer.valueOf(endHourTxt.getText().toString());
                    if(stHour <= endHour) {

                        classRoomData.setStartTimeHourToMs(stHour);
                        classRoomData.setEndTimeHourToMs(endHour);

                        Intent resultIntent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("classRoomData", classRoomData);
                        resultIntent.putExtras(bundle);
                        setResult(RESULT_OK, resultIntent);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "시작 시간이 종료 시간보다 큽니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "검색 시간대를 모두 입력해주십시오.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent resultIntent = new Intent();
        setResult(RESULT_CANCELED, resultIntent);
        finish();
    }
}
