package com.techtown.startui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LogInActivity extends AppCompatActivity {

    EditText login_id, login_pw;
    Button btn_login_submit, btn_register;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_id = findViewById(R.id.login_id);
        login_pw = findViewById(R.id.login_pw);
        btn_login_submit = findViewById(R.id.login_submit);
        btn_register = findViewById(R.id.register);

        btn_login_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Call user data from all user files to check ID/PW and get the user data on ClassRoomData
                // TODO: https://stackoverflow.com/questions/40404567/how-to-send-verification-email-with-firebase
                ClassRoomData classRoomData = new ClassRoomData();
                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("classRoomData", classRoomData);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Membership.class);
                startActivity(intent);
            }
        });

    }
}
