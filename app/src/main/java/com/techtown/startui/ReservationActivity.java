package com.techtown.startui;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReservationActivity extends AppCompatActivity {

    EditText users_num;
    Spinner spinner;
    Button rsvn_confirm_btn;
    ClassRoomData classRoomData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        Intent intent = getIntent();
        classRoomData = (ClassRoomData)intent.getSerializableExtra("classRoomData");

        users_num = findViewById(R.id.users_num);
        spinner = findViewById(R.id.spinner);
        rsvn_confirm_btn = findViewById(R.id.rsvn_confirm_btn);

        rsvn_confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!users_num.getText().toString().isEmpty()) {
                    classRoomData.setNumUsers(Integer.valueOf(users_num.getText().toString()));
                    classRoomData.setUsage(spinner.getSelectedItem().toString());
                    Intent resultIntent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("classRoomData", classRoomData);
                    resultIntent.putExtras(bundle);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "이용인원을 기입해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("classRoomData", classRoomData);
        resultIntent.putExtras(bundle);
        setResult(RESULT_CANCELED, resultIntent);
        finish();
    }
}























