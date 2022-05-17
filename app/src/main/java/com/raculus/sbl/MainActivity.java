package com.raculus.sbl;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            TextView textView = findViewById(R.id.textView);
            double lat = loc.getLatitude();
            double lng = loc.getLongitude();
            textView.setText("latitude: " +lat + " longitude: "+lng);
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.textView);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        com.example.gson_test.MyLocationListner myLocationListner = new com.example.gson_test.MyLocationListner();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, myLocationListner);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        myLocationListner.onLocationChanged(location);
        textView.setText("lat: " + lat + " lng: " + lng);

        Get get = new Get();
        String url = new NearbyNode().getUrl(lat, lng);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String string_data = get.HttpBody(url);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NearbyNode nearbyNode = new NearbyNode();
                            ArrayList<NearbyNode> nodeArrayList = nearbyNode.getNearbyNode(string_data);
                            for (NearbyNode node:nodeArrayList) {
                                String name = node.getNodeName();
                                int code = node.getNodeNum();
                                String result = name + " : " + code;
                                textView.setText(textView.getText().toString() + "\n" + result);
                            }

                            progressBar.setVisibility(View.INVISIBLE);
                            textView.setVisibility(View.VISIBLE);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

class Get {
    public String HttpBody(String strUrl) throws IOException{
        URL url = new URL(strUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        return sb.toString();
    }
}