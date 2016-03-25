package com.gpstracker;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by prerana_katyarmal on 3/25/2016.
 */
public class GPSTrackerWithMap implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final long INTERVAL = 1000 * 60 * 1; //1 minute
    private static final long FASTEST_INTERVAL = 1000 * 60 * 1; // 1 minute
    private static final String TAG = "GPSTrackerWithMap";
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private GoogleMap mGoogleMap;
    private Context mContext;

    private GPSTrackerListner gpsTrackerListner;

    GPSTrackerWithMap(Context context, GoogleMap googleMap) {
        mContext = context;
        mGoogleMap = googleMap;

        gpsTrackerListner = (GPSTrackerListner) context;

        //check if google service is available or not
        if (!isGooglePlayServicesAvailable()) {
            gpsTrackerListner.onGooglePlayServeiceUnAvailable();
        }
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void connectToGoogleAPIClient() {
        mGoogleApiClient.connect();
    }
    public void disConnectFromGoogleAPIClient(){
        mGoogleApiClient.disconnect();
    }
    public boolean isGoogleCilentConnected(){
        return  mGoogleApiClient.isConnected();
    }
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, (Activity) mContext, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //send updated location to calling activity
        gpsTrackerListner.onLocationChanged(location);

        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        addMarker();
    }

    private void addMarker() {
        MarkerOptions options = new MarkerOptions();

        // following four lines requires 'Google Maps Android API Utility Library'
        // https://developers.google.com/maps/documentation/android/utility/
        // I have used this to display the time as title for location markers
        // you can safely comment the following four lines but for this info

       /* IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setStyle(IconGenerator.STYLE_PURPLE);
        options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(mLastUpdateTime)));
        options.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());*/

        LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        options.position(currentLatLng);
        Marker mapMarker = mGoogleMap.addMarker(options);
        long atTime = mCurrentLocation.getTime();
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
        mapMarker.setTitle(mLastUpdateTime);
        Log.d(TAG, "Marker added.............................");
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
                13));
        Log.d(TAG, "Zoom done.............................");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "Connection failed: " + connectionResult.toString());

    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d("", "Location update started ..............: ");
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }
}
