package com.gpstracker;

import android.location.Location;

/**
 * Created by prerana_katyarmal on 3/25/2016.
 */
public interface GPSTrackerListner {
    public void onGooglePlayServeiceUnAvailable();
    void onLocationChanged(Location location);

}
