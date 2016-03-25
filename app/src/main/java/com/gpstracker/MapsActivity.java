
package com.gpstracker;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.maps.SupportMapFragment;

public class MapsActivity extends FragmentActivity implements GPSTrackerListner  {

    private static final String TAG = "LocationActivity";
  //  private GPSTrackerWithMap gpsTracker;
    private GPSTracker gpsTracker;
    private static final long INTERVAL = 1000 * 60 * 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate ...............................");
        //show error dialog if GoolglePlayServices not available

        setContentView(R.layout.activity_maps);
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //gpsTracker = new GPSTrackerWithMap(MapsActivity.this, fm.getMap());
        gpsTracker=new GPSTracker(MapsActivity.this);
        gpsTracker.startLocationTracking(MapsActivity.this,INTERVAL);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        gpsTracker.connectToGoogleAPIClient();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        gpsTracker.disConnectFromGoogleAPIClient();
    }


    @Override
    protected void onPause() {
        super.onPause();
        gpsTracker.stopLocationUpdates();

    }


    @Override
    public void onResume() {
        super.onResume();
        if (gpsTracker.isGoogleCilentConnected()) {
            gpsTracker.startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }

    @Override
    public void onGooglePlayServeiceUnAvailable() {
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v("Changed Location:","Lat: "+String.valueOf(location.getLatitude())+" Longi: "+String.valueOf(location.getLongitude()));
    }
}