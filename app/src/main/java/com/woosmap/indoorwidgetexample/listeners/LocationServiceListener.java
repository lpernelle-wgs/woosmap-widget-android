package com.woosmap.indoorwidgetexample.listeners;

import android.os.Bundle;

/**
 * Interface to raise events received from `AbstractLocationProvider` implementation.
 * */
public interface LocationServiceListener {
    /**
     * Should be raised when a new location is received
     * */
    void onLocationChanged(double lat, double lng, int floor, float accuracy, Bundle extra);
    /**
     * Should be raised when user floor change is detected
     * */
    void onFloorChanged(int floor);
    /**
     * Should be raised when a user enters a venue proximity
     * */
    void onVenueEntered(Bundle extra);
    /**
     * Should be raised when a user exits a venue proximity
     * */
    void onVenueExited(Bundle extra);
    /**
     * Should be raised location provider encounters any error
     * */
    void onLocationError(String error);
    /**
     * Should be raised to pass on any kind of additional information from location provider
     * */
    void onExtraInfo(String info,Bundle extra);
    /**
     * Should be raised location is detected outside the venue
     * */
    void onUserOutsideVenue();
    /**
     * Should be raised location provider is initialized and is ready to be used.
     * */
    void onLocationServiceReady();
    /**
     * Should be raised if/when location provider needs a bluetooth adapter to be turned on.
     * */
    void requiresBluetooth();
    /**
     * Should be raised if/when location provider needs Wifi
     * */
    void requiresWifi();
    /**
     * Should be raised if/when location provider requires compass calibration
     * */
    void requiresCompassCalibration();
    /**
     * Should be raised when location provider needs location permissions to be granted
     * */
    void requiresLocationPermission();
}
