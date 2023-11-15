package com.woosmap.indoorwidgetexample.locationproviders;

import android.content.Context;
import android.os.Bundle;
import com.woosmap.indoorwidgetexample.listeners.LocationServiceListener;

public abstract class AbstractLocationProvider {
    private LocationServiceListener listener;
    public abstract void initialize(Bundle extras, Context context);
    public abstract void startPositioning();
    public abstract void stopPositioning();
    public abstract void destroy();
    public abstract void onPause();
    public abstract void onResume();
    public void setLocationServiceListener(LocationServiceListener listener){
        this.listener = listener;
    }
    public LocationServiceListener getLocationServiceListener(){
        return listener;
    }
}
