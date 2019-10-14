package com.techtown.startui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.gms.common.util.NumberUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutput;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Membership extends AppCompatActivity {

    ClassRoomData classRoomData;

    Button email_auth_number, register_member;

    EditText id_register, pw_register, name_register;
    EditText phone_number, email_register;

    FirebaseAuth auth;
    FirebaseUser user;

    boolean isVerificationSent;

    // Firebase Authentication reference: https://stackoverflow.com/questions/40404567/how-to-send-verification-email-with-firebase

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        classRoomData = new ClassRoomData();

        auth = FirebaseAuth.getInstance();

        isVerificationSent = false;

        email_auth_number = findViewById(R.id.email_auth_number);
        email_auth_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email_register = findViewById(R.id.email_register);
                pw_register = findViewById(R.id.pw_register);

                final String email = email_register.getText().toString();
                final String password = pw_register.getText().toString();

                if(!isFormEmpty()) {

                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        sendVerificationEmail();
                                        Intent intent = new Intent(getApplicationContext(), EmailVerificationActivity.class);
                                        intent.putExtra("email", email);
                                        intent.putExtra("password", password);
                                        startActivity(intent);
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

                boolean isFormCompleted = !isFormEmpty();
                if(isFormCompleted && isVerificationSent) {

                    if (auth.getCurrentUser().isEmailVerified()) {

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

                        classRoomData.writeJSON();

                        Toast.makeText(getApplicationContext(), "가입되셨습니다", Toast.LENGTH_SHORT).show();
                        finish();

                    } else  {

                        Toast.makeText(getApplicationContext(), "인증 메일을 확인해주세요", Toast.LENGTH_SHORT).show();

                    }

                } else if(isFormCompleted) {

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
                ret = true;
                Toast.makeText(getApplicationContext(), "모든 양식을 작성해주세요.", Toast.LENGTH_SHORT).show();
                break;
            }
        }

        if(!ret) {

            Pattern pattern; Matcher matcher;

            // Student ID Number
            String id = arrayList.get(0).getText().toString();
            if (!(id.length() == 8 && id.charAt(0) == '6')) {
                Toast.makeText(getApplicationContext(), "올바른 학번을 입력해주세요.", Toast.LENGTH_SHORT).show();
                ret = true;
            }

            // Password
            String pw = arrayList.get(1).getText().toString(), warnMsg = "";
            boolean hasEnoughLength, hasAlpha, hasNumber, hasSpecialCharacter;
            hasEnoughLength = (pw.length() >= 8);
            pattern = Pattern.compile("[a-z]", Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(pw);
            hasAlpha = matcher.find();
            pattern = Pattern.compile("[0-9]");
            matcher = pattern.matcher(pw);
            hasNumber = matcher.find();
            pattern = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(pw);
            hasSpecialCharacter = matcher.find();
            if(!hasEnoughLength) warnMsg = "비밀번호는 8자리 이상이어야 합니다.";
            if(!hasAlpha) warnMsg += ((warnMsg.length() == 0) ? "" : "\n") + "비밀번호에 문자가 있어야 합니다.";
            if(!hasNumber) warnMsg += ((warnMsg.length() == 0) ? "" : "\n") + "비밀번호에 숫자가 있어야 합니다.";
            if(!hasSpecialCharacter) warnMsg += ((warnMsg.length() == 0) ? "" : "\n") + "비밀번호에 특수문자가 있어야 합니다.";
            if(!(hasEnoughLength && hasAlpha && hasNumber && hasSpecialCharacter)) {
                Toast.makeText(getApplicationContext(), warnMsg, Toast.LENGTH_SHORT).show();
                ret = true;
            }

            // Phone Number
            pattern = Pattern.compile("[^0-9]");
            matcher = pattern.matcher(arrayList.get(3).getText().toString());
            if(matcher.find()) {
                Toast.makeText(getApplicationContext(), "전화번호에 번호만 입력해주세요", Toast.LENGTH_SHORT).show();
                ret = true;
            }

            // Email
            String email = arrayList.get(4).getText().toString();
            if(!email.contains("@") || !email.contains(".")) {
                Toast.makeText(getApplicationContext(), "올바른 이메일 형식을 작성해주세요.", Toast.LENGTH_SHORT).show();
                ret = true;
            }

        }

        return ret;
    }

    private void sendVerificationEmail() {

        user = auth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            isVerificationSent = true;
                            //
                        } else {
                            Toast.makeText(getApplicationContext(), "인증 메일 발송에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private Boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
