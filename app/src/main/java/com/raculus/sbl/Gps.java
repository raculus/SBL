package com.raculus.sbl;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

class MyLocationListner implements LocationListener {
    double latitude, longitude;

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    @Override
    public void onLocationChanged(Location loc) {
        this.latitude = loc.getLatitude();
        this.longitude = loc.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}