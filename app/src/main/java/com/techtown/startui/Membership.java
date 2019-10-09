package com.techtown.startui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class Membership extends AppCompatActivity {

    ClassRoomData classRoomData;

    Button email_auth_number, register_member;

    EditText id_register, pw_register, name_register;
    EditText phone_number, email_register, auth_number;

    int sentEmailNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        classRoomData = new ClassRoomData();

        sentEmailNumber = 0;

        email_auth_number = findViewById(R.id.email_auth_number);
        email_auth_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email_register = findViewById(R.id.email_register);

                String email = email_register.getText().toString();

                Random random = new Random();
                sentEmailNumber = random.nextInt(888888) + 111111;

                if(email.length() > 0) {

                    String subject = "[명지대학교 강의실 대여 시스템] 인증번호 확인";

                    String message = "안녕하십니까 명지대학교 강의실 대여 시스템입니다.\n\n";
                    message += "다음의 인증번호를 확인하여 주시기 바랍니다.\n\n";
                    message += "인증번호: " + sentEmailNumber + "\n\n";
                    message += "본 인증번호는 명지대학교 강의실 대여 시스템에서 보냈습니다." + '\n';
                    message += "인증 메일을 신청하신 본인이 아니시라면 아래의 연락처로 연락 주시면 감사드리겠습니다:" + '\n';
                    message += "E-Mail: 13circle97@gmail.com" + '\n';
                    message += "Cell#: 010-2070-2981" + '\n';

                    // TODO: Send Email Using Firebase

                } else {

                    Toast.makeText(getApplicationContext(), "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();

                }

            }
        });

        register_member = findViewById(R.id.register_member);
        register_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth_number = findViewById(R.id.auth_number);
                if(auth_number.getText().toString().length() > 0) {
                    if (sentEmailNumber == Integer.valueOf(auth_number.getText().toString())) {

                        id_register = findViewById(R.id.id_register);
                        pw_register = findViewById(R.id.pw_register);
                        name_register = findViewById(R.id.name_register);
                        phone_number = findViewById(R.id.phone_number);
                        email_register = findViewById(R.id.email_register);

                        classRoomData.setUserId(Integer.valueOf(id_register.getText().toString()));
                        classRoomData.setUserPw(pw_register.getText().toString());
                        classRoomData.setUserName(name_register.getText().toString());
                        classRoomData.setPhoneNumber(phone_number.getText().toString());
                        classRoomData.setUserEmail(email_register.getText().toString());

                        // TODO: Call JSON-related methods in ClassRoomData to write the file

                    } else {

                        Toast.makeText(getApplicationContext(), "이메일을 인증해주세요", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

    }
}
