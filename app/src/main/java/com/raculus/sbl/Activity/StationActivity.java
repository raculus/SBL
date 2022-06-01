package com.raculus.sbl.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.raculus.sbl.Get;
import com.raculus.sbl.OpenAPI.NearbyStation;
import com.raculus.sbl.R;
import com.raculus.sbl.OpenAPI.Station;
import com.raculus.sbl.ListView_Adapter.Station_Adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class StationActivity extends AppCompatActivity {

    private void itemClick(Station station){
        int resultCode = Activity.RESULT_OK;
        //값 넘기기
        Intent intent = new Intent();
        intent.putExtra("Station", station);

        //액티비티 닫기
        setResult(resultCode, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        TextView textView = findViewById(R.id.textView);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        ListView listView = findViewById(R.id.listView);
        Context context = this;

        //NearbyActivity intent에서 값 가져오기
        Intent intent = getIntent();
        NearbyStation nearbyStation = (NearbyStation) intent.getSerializableExtra("NearbyStation");
        String stationName = nearbyStation.getNodeName();
        String stationId = nearbyStation.getNodeId();
        int cityCode = nearbyStation.getCityCode();
        int stationNum = nearbyStation.getNodeNum();

        String title = String.format("%s", stationName);
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
                                        String msg = getResources().getString(R.string.err_no_bus);
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                        finish();
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