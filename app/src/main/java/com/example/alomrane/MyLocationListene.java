package com.example.alomrane;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListene {

    public class MyLocationListener implements LocationListener  {
        @Override
        public void onLocationChanged(Location location) {
          //  mTextViewLocation.setText("Lat: " + location.getLatitude() + ", Long: " + location.getLongitude());
        }

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}
