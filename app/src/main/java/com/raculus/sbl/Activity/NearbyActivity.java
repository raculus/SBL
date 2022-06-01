package com.raculus.sbl.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.raculus.sbl.Get;
import com.raculus.sbl.OpenAPI.NearbyStation;
import com.raculus.sbl.ListView_Adapter.NearbyStation_Adapter;
import com.raculus.sbl.OpenAPI.Station;
import com.raculus.sbl.R;

import java.io.IOException;
import java.util.ArrayList;

public class NearbyActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    Station station = (Station) intent.getSerializableExtra("bus");
                    Log.e("Nearby", station.getRouteNum()+"");
                }
            }
    );

    public static NearbyActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        activity = NearbyActivity.this;
        TextView textView = findViewById(R.id.textView);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        ListView listView = findViewById(R.id.listView);
        Context con = this;

        /*
         * gps 위치 가져오기
         */
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final int PERMISSIONS_REQUEST_READ_LOCATION = 0x00000001;
            final String[] PERMISSIONS = {
              Manifest.permission.ACCESS_COARSE_LOCATION,
              Manifest.permission.ACCESS_FINE_LOCATION
            };
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Context context = this;
                new AlertDialog.Builder(this)
                        .setTitle("위치 권한이 왜 필요하나요?")
                        .setMessage("주변 정류장 검색을 위해 필요합니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which){
                                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, PERMISSIONS_REQUEST_READ_LOCATION);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_READ_LOCATION);
            }
            return;
        }

        Location location = locationManager.getLastKnownLocation(locationProvider);
        if(location == null){
            Log.e("location", "null");
            textView.setText("location is null");
            return;
        }

        double lat = location.getLatitude(); //위도
        double lng = location.getLongitude(); //경도
        Log.e("lat, lng", lat +", "+ lng);

        textView.setText("lat: " + lat + " lng: " + lng);

        /*
         * openapi에서 json불러오기 
         */
        Get get = new Get();
        String url = new NearbyStation().getUrl(lat, lng);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String string_data = get.HttpBody(url);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NearbyStation nearbyStation = new NearbyStation();
                            ArrayList<NearbyStation> nodeArrayList = nearbyStation.getNearbyStation(string_data);

                            progressBar.setVisibility(View.INVISIBLE);

                            /*
                            * listview에 값 넣기
                            * */
                            if(nodeArrayList.size() > 0){
                                final NearbyStation_Adapter myAdapter = new NearbyStation_Adapter(con, nodeArrayList);

                                listView.setAdapter(myAdapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                        NearbyStation nearby = myAdapter.getItem(position);
                                        selectBus(nearby);
                                    }
                                });
                            }
                            else{
                                textView.setText("근처에 정류장이 없습니다.");
                                textView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void selectBus(NearbyStation nearby){
        Intent intent = new Intent(getApplicationContext(), StationActivity.class);
        intent.putExtra("stationName", nearby.getNodeName());
        intent.putExtra("stationNum", nearby.getNodeNum());
        intent.putExtra("stationId", nearby.getNodeId());
        intent.putExtra("cityCode", nearby.getCityCode());

        mStartForResult.launch(intent);
    }
}