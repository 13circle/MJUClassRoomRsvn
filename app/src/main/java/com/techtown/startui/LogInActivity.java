package com.techtown.startui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    EditText login_id, login_pw;
    Button btn_login_submit, btn_register;

    ClassRoomData classRoomData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_id = findViewById(R.id.login_id);
        login_pw = findViewById(R.id.login_pw);
        btn_login_submit = findViewById(R.id.login_submit);
        btn_register = findViewById(R.id.register);

        classRoomData = new ClassRoomData();

        btn_login_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(login_id.getText().toString().length() > 0 && login_pw.getText().toString().length() > 0) {

                    // TODO: Call user data from all user files to check ID/PW and get the user data on ClassRoomData
                    classRoomData.setUserId(Integer.valueOf(login_id.getText().toString()));
                    classRoomData.setUserPw(login_pw.getText().toString());

                    // TODO: ▼ ▼ ▼  등록된 테스트 이메일 입력  ▼ ▼ ▼
                    classRoomData.setUserEmail("13circle97@gmail.com"); // TODO: Just for test. MUST BE REMOVED LATER.
                    // TODO: ▲ ▲ ▲  등록된 테스트 이메일 입력  ▲ ▲ ▲

                    classRoomData.readJSON(); // TODO: MUST verify whether a following ID matches to the user

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(classRoomData.getUserEmail(), classRoomData.getUserPw())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("classRoomData", classRoomData);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "해당 계정이 없거나 잘못된 ID/PW 입니다", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {

                    Toast.makeText(getApplicationContext(), "ID/PW 를 입력해주세요", Toast.LENGTH_SHORT).show();

                }

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
