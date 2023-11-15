package com.woosmap.indoorwidgetexample.listeners;

import android.os.Bundle;

public interface LocationServiceListener {
    void onLocationChanged(double lat, double lng, int floor, float accuracy, Bundle extra);
    void onFloorChanged(int floor);
    void onVenueEntered(Bundle extra);
    void onVenueExited(Bundle extra);
    void onLocationError(String error);
    void onExtraInfo(String info,Bundle extra);
    void onUserOutsideVenue();
    void onLocationServiceReady();
    void requiresBluetooth();
    void requiresWifi();
    void requiresCompassCalibration();
    void requiresLocation();
}
