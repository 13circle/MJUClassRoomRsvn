package com.techtown.startui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {

    EditText login_id, login_pw;
    Button btn_login_submit, btn_register;

    ClassRoomData classRoomData;

    DatabaseReference mRef;

    long backPressedTime;

    boolean isSignedIn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        backPressedTime = 0;
        isSignedIn = false;

        login_id = findViewById(R.id.login_id);
        login_pw = findViewById(R.id.login_pw);
        btn_login_submit = findViewById(R.id.login_submit);
        btn_register = findViewById(R.id.register);

        classRoomData = new ClassRoomData();
        mRef = FirebaseDatabase.getInstance().getReference();

        btn_login_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(login_id.getText().toString().length() > 0 && login_pw.getText().toString().length() > 0) {

                    classRoomData.setUserId(Integer.valueOf(login_id.getText().toString()));
                    classRoomData.setUserPw(login_pw.getText().toString());

                    mRef.child("logInStatus").child(String.valueOf(classRoomData.getUserId())).setValue(true);

                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            classRoomData.setUserEmail(dataSnapshot.child("users").child(String.valueOf(classRoomData.getUserId())).child("userEmail").getValue(String.class));
                            if(classRoomData.getUserEmail().isEmpty()) {
                                Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                mRef.child("logInStatus").child(String.valueOf(classRoomData.getUserId())).setValue(false);
                                return;
                            } else {
                                classRoomData.setUserName(dataSnapshot.child("users").child(String.valueOf(classRoomData.getUserId())).child("userName").getValue(String.class));
                            }
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            auth.signInWithEmailAndPassword(classRoomData.getUserEmail(), classRoomData.getUserPw())
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), classRoomData.getUserName() + " 님께서 로그인 하셨습니다", Toast.LENGTH_SHORT).show(); isSignedIn = true;
                                                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable("classRoomData", classRoomData);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "해당 계정이 없거나 잘못된 ID/PW 입니다", Toast.LENGTH_SHORT).show();
                                                mRef.child("logInStatus").child(String.valueOf(classRoomData.getUserId())).setValue(false);
                                            }
                                        } // onComplete - OnCompleteListener<AuthResult>
                                    }); // signInWithEmailAndPassword
                        } // onDataChange

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "해당 계정이 없거나 잘못된 ID/PW 입니다", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(getApplicationContext(), MembershipActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(backPressedTime == 0) {
            Toast.makeText(getApplicationContext(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            backPressedTime = System.currentTimeMillis();
        } else {
            int seconds = (int) (System.currentTimeMillis() - backPressedTime);
            if(seconds > 2000) {
                Toast.makeText(getApplicationContext(), "한 번 더 누르면 종료됩니다." , Toast.LENGTH_SHORT).show();
                backPressedTime = 0;
            } else {
                if(isSignedIn) {
                    mRef.child("logInStatus").child(String.valueOf(String.valueOf(classRoomData.getUserId()))).setValue(false);
                }
                super.onBackPressed();
            }
        }

    }

}
