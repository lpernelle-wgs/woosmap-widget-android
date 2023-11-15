package com.woosmap.indoorwidgetexample

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.woosmap.indoorwidgetexample.helpers.IndoorWidgetHelper
import com.woosmap.indoorwidgetexample.listeners.IndoorWidgetListener
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private val REQUEST_FINE_LOCATION = 1
    private val TAG = "MainActivity"
    private lateinit var indoorWidgetHelper:IndoorWidgetHelper
    private lateinit var indoorWidgetListener: IndoorWidgetListener
    private var indoorVenueLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        requestLocationPermissions();
    }

    override fun onBackPressed() {
        if (indoorVenueLoaded){
            indoorWidgetHelper.initializeIndoorMaps();
        }
        else{
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
        }
    }
}