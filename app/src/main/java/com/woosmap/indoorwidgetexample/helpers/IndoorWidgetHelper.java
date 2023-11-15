package com.woosmap.indoorwidgetexample.helpers;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.woosmap.indoorwidgetexample.R;
import com.woosmap.indoorwidgetexample.listeners.IndoorWidgetListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

public class IndoorWidgetHelper {
    private static IndoorWidgetHelper _instance;
    private static final String TAG = "IndoorWidgetHelper";
    private Context context;
    private WebView contentView;
    private IndoorWidgetListener indoorWidgetListener;
    private boolean indoorVenueLoaded;
    private TextToSpeech textToSpeech;
    public static IndoorWidgetHelper getInstance(Context context, WebView contentView){
        if (_instance == null){
            _instance = new IndoorWidgetHelper(context, contentView);
        }
        return _instance;
    }
    private IndoorWidgetHelper(Context context, WebView contentView){
        this.context = context;
        this.contentView = contentView;
        initializeTextToSpeech();
    }
    public IndoorWidgetListener getIndoorWidgetListener() {
        return indoorWidgetListener;
    }
    public void setIndoorWidgetListener(IndoorWidgetListener indoorWidgetListener) {
        this.indoorWidgetListener = indoorWidgetListener;
    }

    public void initializeIndoorMaps(){
        renderIndoorJSWidget();
    }

    private void renderIndoorJSWidget(){
        try{
            String pageContent = getTextContentFromAssetFile("indoor.html");
            String woosmapKey = context.getString(R.string.public_key);
            pageContent = pageContent.replace("{API_KEY}",woosmapKey);
            pageContent = pageContent.replace("{DOMAIN}","sdk.woosmap.com");
            pageContent = pageContent.replace("{MAP_LANGUAGE}", Locale.getDefault().getLanguage());

            pageContent = pageContent.replace("{DEFAULT_VENUE}","");
            pageContent = pageContent.replace("{FORCE_EXTRUSION}","");

            contentView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
                }
            });
            contentView.setWebViewClient(new WebViewClient());
            contentView.addJavascriptInterface(new WebviewInterface(context), "nativeWebView");
            contentView.clearCache(true);
            contentView.clearHistory();
            contentView.getSettings().setJavaScriptEnabled(true);
            contentView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            contentView.setWebContentsDebuggingEnabled(true);
            contentView.loadDataWithBaseURL("http://" + context.getPackageName(), pageContent,"text/html","UTF-8", null);
            indoorVenueLoaded = false;
        }
        catch (Exception ex){
            Log.e(TAG, ex.toString());
        }
    }

    public void setUserLocation(double lat, double lng, int floor, Bundle extra) {
        contentView.post(() -> {
            float bearing = 0;
            if (extra.containsKey("location")){
                Location location = extra.getParcelable("location");
                if (location.hasBearing()){
                    bearing = location.getBearing();
                }
            }
            contentView.evaluateJavascript(String.format(Locale.ENGLISH, "setUserLocation(%f,%f,%d,%f);", lat, lng, floor,bearing), value -> {});
        });
    }

    public void changeFloor(int floor) {
        contentView.post(() -> {
            contentView.evaluateJavascript(String.format(Locale.ENGLISH, "setFloor(%d);", floor), value -> {

            });
        });
    }

    private void initializeTextToSpeech(){
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = textToSpeech.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    }
                }
                else{
                    Log.e(TAG, "Text to speech not initialized");
                }
            }
        });
    }

    private void handleInstructionSpeech(JSONObject data){
        JSONObject feature;
        String instruction;
        try{
            feature = data.getJSONObject("data").getJSONObject("feature");

            if (data.getJSONObject("data").getBoolean("voiceInstructionsEnabled")){
                instruction = feature.getJSONObject("properties").getString("instruction");
                if (!textToSpeech.isSpeaking()){
                    textToSpeech.speak(instruction, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }
        catch (Exception ex){
            Log.e(TAG,ex.toString());
        }
    }

    private String getTextContentFromAssetFile(String fileName) {
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                stringBuilder.append(mLine);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return stringBuilder.toString();
    }

    class WebviewInterface {
        private Context context;

        public WebviewInterface(Context context){
            this.context = context;
        }

        @JavascriptInterface
        public void postMessage(String data){
            try{

                JSONObject jsonObject = new JSONObject(data);
                if (jsonObject.getString("event").equalsIgnoreCase("indoor_venue_loaded")){
                    indoorVenueLoaded = true;
                    indoorWidgetListener.onIndoorVenueLoaded(jsonObject.getJSONObject("data"));
                }
                if (jsonObject.getString("event").equalsIgnoreCase("plugin_loaded")){
                    indoorWidgetListener.onPluginLoaded(jsonObject.getJSONObject("data"));
                }
                if (jsonObject.getString("event").equalsIgnoreCase("indoor_highlight_step")){
                    indoorWidgetListener.onIndoorHighlightStep(jsonObject.getJSONObject("data"));
                    handleInstructionSpeech(jsonObject);
                }
                if (jsonObject.getString("event").equalsIgnoreCase("indoor_level_changed")){
                    indoorWidgetListener.onIndoorLevelChanged(jsonObject.getJSONObject("data"));
                }
                if (jsonObject.getString("event").equalsIgnoreCase("indoor_feature_selected")){
                    indoorWidgetListener.onIndoorFeatureSelected(jsonObject.getJSONObject("data"));
                }
                if (jsonObject.getString("event").equalsIgnoreCase("indoor_user_location")){
                    indoorWidgetListener.onIndoorUserLocation(jsonObject.getJSONObject("data"));
                }
            }
            catch (JSONException ex){
                Log.e(TAG, ex.toString());
            }
        }
    }
}
