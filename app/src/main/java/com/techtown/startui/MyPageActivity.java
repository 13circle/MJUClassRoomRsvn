package com.techtown.startui;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MyPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        Intent intent = getIntent();
        ClassRoomData CRdata = (ClassRoomData)intent.getSerializableExtra("classRoomData");
        // TODO: Read ClassRoomData and fill the account info

    }
}
