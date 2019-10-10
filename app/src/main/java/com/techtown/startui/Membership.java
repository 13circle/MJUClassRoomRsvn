package com.techtown.startui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Membership extends AppCompatActivity {

    ClassRoomData classRoomData;

    Button email_auth_number, register_member;

    EditText id_register, pw_register, name_register;
    EditText phone_number, email_register;

    Boolean isEmailAuthenticated;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        classRoomData = new ClassRoomData();

        auth = FirebaseAuth.getInstance();

        isEmailAuthenticated = false;

        email_auth_number = findViewById(R.id.email_auth_number);
        email_auth_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email_register = findViewById(R.id.email_register);
                pw_register = findViewById(R.id.pw_register);

                String email = email_register.getText().toString();
                String password = pw_register.getText().toString();

                if(!isFormEmpty()) {

                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        user = auth.getCurrentUser();
                                        sendVerificationEmail();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(getApplicationContext(), "이미 가입된 사용자이거나 승인된 이메일 주소가 아닙니다.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }

            }
        });

        register_member = findViewById(R.id.register_member);
        register_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmailAuthenticated) {

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

                    // TODO: Check Account is Verified or not

                } else {

                    Toast.makeText(getApplicationContext(), "이메일을 인증해주세요", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private boolean isFormEmpty() {

        boolean ret = false;

        ArrayList<EditText> arrayList = new ArrayList<>();

        arrayList.add((EditText) findViewById(R.id.id_register));
        arrayList.add((EditText) findViewById(R.id.pw_register));
        arrayList.add((EditText) findViewById(R.id.name_register));
        arrayList.add((EditText) findViewById(R.id.phone_number));
        arrayList.add((EditText) findViewById(R.id.email_register));

        for(int i = 0; i < arrayList.size(); i++) {
            if(arrayList.get(i).getText().toString().length() == 0) {
                ret = true; break;
            }
        }

        if(ret) Toast.makeText(getApplicationContext(), "모든 양식을 작성해 주세요.", Toast.LENGTH_SHORT).show();

        return ret;
    }

    private void sendVerificationEmail() {

        auth.getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "이메일이 인증되었습니다.", Toast.LENGTH_SHORT).show();
                            isEmailAuthenticated = true;
                        } else {
                            Toast.makeText(getApplicationContext(), "이메일 인증에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}
