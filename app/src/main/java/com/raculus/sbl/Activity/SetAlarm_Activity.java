package com.raculus.sbl.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.raculus.sbl.AlarmReceiver;
import com.raculus.sbl.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SetAlarm_Activity extends AppCompatActivity {

    private Button btnSet, btnCancel;
    private EditText editMin, editSec;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        btnSet = (Button)findViewById(R.id.btnSetAlarm);
        editMin = findViewById(R.id.editMin);
        editSec = findViewById(R.id.editSec);


        btnSet.setOnClickListener(v->{
            String strEditMin = String.valueOf(editMin.getText());
            String strEditSec = String.valueOf(editSec.getText());
            int intEditMin = Integer.parseInt(strEditMin);
            int intEditSec = Integer.parseInt(strEditSec);

            //오늘 시간
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
            String strDate = dateFormat.format(date);
            String[] dateArr = strDate.split(":");

            int hour = Integer.parseInt(dateArr[0]);
            int minute = Integer.parseInt(dateArr[1]);
            int sec = Integer.parseInt(dateArr[2]);

            minute += intEditMin;
            minute -= 1;
            sec += intEditSec;

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR,hour);
            calendar.set(Calendar.MINUTE,minute);
            calendar.set(Calendar.SECOND, sec);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }

            AlarmManager alarmManager=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                Intent intent = new Intent(this, AlarmReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 1, intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                //TODO: FLAG_UPDATE_CURRENT에서는 알림이 정상작동
                //      하지만 IMMUTABLE에서는 작동x
                //      (안드로이드30버전+ 에서는 IMMUTABLE, MUTTABLE만 사용 가능)

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, alarmIntent);

                Toast.makeText(this,"알람이 저장되었습니다.",Toast.LENGTH_LONG).show();
            }
        });
    }
}