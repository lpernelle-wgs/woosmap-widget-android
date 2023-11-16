package com.woosmap.indoorwidgetexample.locationproviders;

import android.content.Context;
import android.os.Bundle;
import com.woosmap.indoorwidgetexample.listeners.LocationServiceListener;

/**
 * Abstract class that defines the structure that should be implemented by location providers.
 * Location providers can be FusedLocation provider for GPS
 * or any other third party indoor location providers such as IndoorAtlas, Situm, Polestar etc.
 * */
public abstract class AbstractLocationProvider {
    /**
     * Should be used to bubble the events raised by location provider
     * */
    private LocationServiceListener listener;
    /**
     * Should be used to initialize the provider.
     * Any additional parameters that may be required by the location provider can be passed
     * in the bundle. (Such as API key and secret)
     * */
    public abstract void initialize(Bundle extras, Context context);
    /**
     * Should be used to initiate location updates
     * */
    public abstract void startPositioning();
    /**
     * Should be used to stop location updates
     * */
    public abstract void stopPositioning();
    /**
     * Should be used for tear down and cleanup task
     * */
    public abstract void destroy();
    /**
     * Should be used if/when location updates need to be paused
     * */
    public abstract void onPause();
    /**
     * Should be used if/when location updates need to resume
     * */
    public abstract void onResume();
    /**
     * The setter
     * */
    public void setLocationServiceListener(LocationServiceListener listener){
        this.listener = listener;
    }
    /**
     * The getter
     * */
    public LocationServiceListener getLocationServiceListener(){
        return listener;
    }
}
