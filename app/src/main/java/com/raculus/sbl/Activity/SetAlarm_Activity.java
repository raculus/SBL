package com.raculus.sbl.Activity;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.raculus.sbl.R;

public class SetAlarm_Activity extends AppCompatActivity {
    AlarmManager alarm_manager;
    EditText editMin = findViewById(R.id.editMin);
    EditText editSec = findViewById(R.id.editSec);
    Button button = findViewById(R.id.btnSetAlarm);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        button.setOnClickListener(view -> {
            setAlarm();
        });

        Intent intent = getIntent();
        int arriveMinutes = intent.getIntExtra("arriveMinutes", 0);
        editMin.setText(arriveMinutes+"");
    }
    void setAlarm(){

    }
}