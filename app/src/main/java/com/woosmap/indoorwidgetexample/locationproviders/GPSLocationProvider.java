package com.woosmap.indoorwidgetexample.locationproviders;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class GPSLocationProvider extends AbstractLocationProvider {
    protected LocationCallback mLocationCallback;
    protected PendingIntent mLocationIntent;
    protected LocationRequest mLocationRequest;
    protected FusedLocationProviderClient mFusedLocationClient;
    private Context context;

    @Override
    public void initialize(Bundle extras, Context context) {
        try {
            this.context = context;

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                getLocationServiceListener().requiresLocationPermission();
                return;
            }
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Bundle extra = new Bundle();
                    extra.putString("provider", "GPS");
                    extra.putParcelable("location", locationResult.getLastLocation());
                    getLocationServiceListener().onLocationChanged(
                            locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude(),
                            0,
                            locationResult.getLastLocation().getAccuracy(),
                            extra);
                }
            };
            getLocationServiceListener().onLocationServiceReady();
        } catch (SecurityException e) {
            getLocationServiceListener().onLocationError(e.toString());
        }
    }

    @Override
    public void startPositioning() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationServiceListener().onLocationError("Location access not granted");
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);//Looper.myLooper());
    }

    @Override
    public void stopPositioning() {
        if (mFusedLocationClient!=null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void destroy() {
        stopPositioning();
        mFusedLocationClient = null;
        mLocationRequest = null;
        mLocationCallback = null;
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }
}
