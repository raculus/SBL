package com.raculus.sbl.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.raculus.sbl.Get;
import com.raculus.sbl.MainActivity;
import com.raculus.sbl.R;
import com.raculus.sbl.OpenAPI.Station;
import com.raculus.sbl.ListView_Adapter.Station_Adapter;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class StationActivity extends AppCompatActivity {

    private void itemClick(Station station){
        int resultCode = Activity.RESULT_OK;
        //값 넘기기
        Log.d("itemclick: ", station.getRouteNum()+"");
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("bus", (Serializable) station);
        intent.putExtra("num", station.getRouteNum());

        //액티비티 닫기
        NearbyActivity nearbyActivity = NearbyActivity.activity;
        nearbyActivity.setResult(resultCode);
        this.finish();
        nearbyActivity.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        TextView textView = findViewById(R.id.textView);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        ListView listView = findViewById(R.id.listView);
        Context context = this;

        Intent intent = getIntent();
        String stationName = intent.getStringExtra("stationName");
        String stationId = intent.getStringExtra("stationId");
        int cityCode = intent.getIntExtra("cityCode", 0);
        int stationNum = intent.getIntExtra("stationNum", 0);

        String title = String.format("정류장: %s (%d)", stationName, stationNum);
        textView.setVisibility(View.VISIBLE);
        textView.setText(title);

        /*
         *  60초 마다
         *  openapi에서 json불러오기
         */
        Timer timer = new Timer();


        TimerTask TT = new TimerTask() {
            int i = 0;
            @Override
            public void run() {
                Get get = new Get();
                String url = new Station().getUrl(cityCode, stationId);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String string_data = get.HttpBody(url);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Station station = new Station();
                                    ArrayList<Station> stationArrayList = station.getStation(string_data);

                                    progressBar.setVisibility(View.INVISIBLE);

                                    /*
                                     * listview에 값 넣기
                                     * */
                                    if(stationArrayList.size() > 0){
                                        final Station_Adapter myAdapter = new Station_Adapter(context, stationArrayList);

                                        listView.setAdapter(myAdapter);

                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                                Station s = myAdapter.getItem(position);
                                                itemClick(s);
                                            }
                                        });
                                    }
                                    else{
                                        Log.e("정류장 없음", "");
                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

        };
        timer.schedule(TT, 0, 1*60000); //Timer 1분마다 실행
    }
}