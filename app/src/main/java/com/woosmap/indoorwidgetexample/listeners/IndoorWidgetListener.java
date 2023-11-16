package com.woosmap.indoorwidgetexample.listeners;

import org.json.JSONObject;

/**
 * Interface to raise events received from Indoor Widget
 * */
public interface IndoorWidgetListener {
    /**
     * Invoked when maps JS plugin in loaded.
     */
    void onPluginLoaded(JSONObject data);
    /**
     * Invoked when Indoor venue is loaded.
     */
    void onIndoorVenueLoaded(JSONObject data);
    /**
     * Invoked when Indoor level is changed.
     */
    void onIndoorLevelChanged(JSONObject data);
    /**
     * Invoked when Indoor feature is selected.
     */
    void onIndoorFeatureSelected(JSONObject data);
    /**
     * Invoked when Indoor location is changed.
     */
    void onIndoorUserLocation(JSONObject data);
    /**
     * Invoked when Indoor navigation step is highlighted.
     */
    void onIndoorHighlightStep(JSONObject data);
}
