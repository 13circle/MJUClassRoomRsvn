package com.techtown.startui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class mypage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        Intent intent = getIntent();
        ClassRoomData CRdata = (ClassRoomData)intent.getSerializableExtra("classRoomData");
        // TODO: Read ClassRoomData and fill the account info

    }
}
