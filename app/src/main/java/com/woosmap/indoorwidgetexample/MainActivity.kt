package com.woosmap.indoorwidgetexample

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.woosmap.indoorwidgetexample.helpers.IndoorWidgetHelper
import com.woosmap.indoorwidgetexample.listeners.IndoorWidgetListener
import com.woosmap.indoorwidgetexample.listeners.LocationServiceListener
import com.woosmap.indoorwidgetexample.locationproviders.AbstractLocationProvider
import com.woosmap.indoorwidgetexample.locationproviders.GPSLocationProvider
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private val REQUEST_FINE_LOCATION = 1
    private val TAG = "MainActivity"

    private lateinit var indoorWidgetHelper:IndoorWidgetHelper
    private lateinit var indoorWidgetListener: IndoorWidgetListener

    private var indoorVenueLoaded = false

    private lateinit var locationProvider: AbstractLocationProvider
    private lateinit var locationServiceListener: LocationServiceListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /// Initialze the Indoor Widget
        indoorWidgetHelper = IndoorWidgetHelper.getInstance(applicationContext, findViewById(R.id.web_view))
        indoorWidgetListener = object : IndoorWidgetListener {
            override fun onPluginLoaded(data: JSONObject) {
                Log.d(TAG, "plugin loaded")
            }
            override fun onIndoorVenueLoaded(data: JSONObject) {
                Log.d(TAG, "venue loaded")
                indoorVenueLoaded = true;
            }
            override fun onIndoorLevelChanged(data: JSONObject) {
                Log.d(TAG, "level changed")
            }
            override fun onIndoorFeatureSelected(data: JSONObject) {
                Log.d(TAG, "feature selected")
            }
            override fun onIndoorUserLocation(data: JSONObject) {
                Log.d(TAG, "user location updated")
            }
            override fun onIndoorHighlightStep(data: JSONObject) {
                Log.d(TAG, "indoor step highlighted")
            }
        }
        indoorWidgetHelper.indoorWidgetListener = indoorWidgetListener
        indoorWidgetHelper.initializeIndoorMaps()
    }

    override fun onStart() {
        super.onStart()
        requestLocationPermissions()
    }

    override fun onStop() {
        stopLocationUpdates()
        super.onStop()
    }

    override fun onDestroy() {
        if (locationProvider!=null){
            locationProvider.destroy()
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (indoorVenueLoaded){ /// if an indoor venue is loaded then go back to loading the world map.
            indoorWidgetHelper.loadWorldMap()
            indoorVenueLoaded = false
        }
        else{ /// Else quit.
            super.onBackPressed()
        }
    }

    /***
     * Requests `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION` permissions
     */
    private fun requestLocationPermissions(){
        // Check if ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION permissions are granted
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) { // Not granted. So request them.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION
            )
        } else { // Location permissions are granted. Initialize the location provider
            initializeLocationProvider()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Location permissions are granted. Initialize the location provider
                    initializeLocationProvider()
                } else {
                    Toast.makeText(this, "Fine location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Initialize the location provider.
     * In our case we are using GPS.
     * **/
    private fun initializeLocationProvider(){
        locationServiceListener = object : LocationServiceListener{
            override fun onLocationChanged(
                lat: Double,
                lng: Double,
                floor: Int,
                accuracy: Float,
                extra: Bundle?
            ) {
                ///New location is reported by location provider.
                ///Update it on the map.
                indoorWidgetHelper.setUserLocation(lat, lng, floor, extra)
            }

            override fun onFloorChanged(floor: Int) {
                ///Location provider detected floor change.
                ///We will change the floor on the map.
                indoorWidgetHelper.changeFloor(floor)
            }

            override fun onVenueEntered(extra: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onVenueExited(extra: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onLocationError(error: String?) {
                TODO("Not yet implemented")
            }

            override fun onExtraInfo(info: String?, extra: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onUserOutsideVenue() {
                TODO("Not yet implemented")
            }

            override fun onLocationServiceReady() {
                ///Location provider is ready. We can start requesting location updates.
                startLocationUpdates()
            }

            override fun requiresBluetooth() {
                TODO("Not yet implemented")
            }

            override fun requiresWifi() {
                TODO("Not yet implemented")
            }

            override fun requiresCompassCalibration() {
                TODO("Not yet implemented")
            }

            override fun requiresLocationPermission() {
                requestLocationPermissions()
            }

        }
        locationProvider = GPSLocationProvider()
        locationProvider.locationServiceListener = locationServiceListener
        locationProvider.initialize(Bundle(), applicationContext)
    }
    private fun startLocationUpdates(){
        locationProvider.startPositioning()
    }

    private fun stopLocationUpdates(){
        locationProvider.stopPositioning()
    }
}