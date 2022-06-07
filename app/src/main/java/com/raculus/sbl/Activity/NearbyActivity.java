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
import android.widget.Toast;

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
                    Station station = (Station) intent.getSerializableExtra("Station");

                    intent = new Intent();
                    intent.putExtra("Station", station);
                    setResult(result.getResultCode(), intent);
                    finish();
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
                String title = getResources().getString(R.string.diag_grant_gps_title);
                String msg = getResources().getString(R.string.diag_grant_gps_msg);
                String ok = getResources().getString(R.string.ok);
                new AlertDialog.Builder(this)
                        .setTitle(title)
                        .setMessage(msg)
                        .setPositiveButton(ok, new DialogInterface.OnClickListener(){
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
            String msg = getResources().getString(R.string.err_null_gps);
            Log.e("location", msg);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            return;
        }

        double lat = location.getLatitude(); //위도
        double lng = location.getLongitude(); //경도
        Log.d("GPS: ", "lat: "+lat +", lng: "+ lng);
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
                            if(nodeArrayList == null){
                                String msg = getResources().getString(R.string.err_api_server);
                                Toast.makeText(con, msg, Toast.LENGTH_LONG).show();
                                finish();
                                return;
                            }
                            else if(nodeArrayList.size() == 0){
                                String msg = getResources().getString(R.string.err_no_bus_nearby);
                                Toast.makeText(con, msg, Toast.LENGTH_LONG).show();
                                finish();
                                return;
                            }

                            progressBar.setVisibility(View.INVISIBLE);

                            /*
                            * listview에 값 넣기
                            * */
                            if(nodeArrayList.size() > 0){
                                final NearbyStation_Adapter myAdapter = new NearbyStation_Adapter(con, nodeArrayList, lat, lng);

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
                                String msg = getResources().getString(R.string.err_no_bus_stop);
                                textView.setText(msg);
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
    private void selectBus(NearbyStation nearbyStation){
        Intent intent = new Intent(NearbyActivity.this, StationActivity.class);
        intent.putExtra("NearbyStation", nearbyStation);

        mStartForResult.launch(intent);
    }
}