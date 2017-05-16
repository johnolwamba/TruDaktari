package com.doreenaradi.trudaktari;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.doreenaradi.trudaktari.adapters.AlertDialogManager;
import com.doreenaradi.trudaktari.adapters.ServiceHandler;
import com.doreenaradi.trudaktari.adapters.Variables;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ReportView extends AppCompatActivity implements OnMapReadyCallback, android.location.LocationListener {

    private MapView mMapView;
    double latitude,longitude;
    private GoogleMap mMap;
    LatLng latitude_longitude;

    private ArrayList<MyMarker> mMyMarkersArray = new ArrayList<MyMarker>();
    private HashMap<Marker, MyMarker> mMarkersHashMap;
    // Progress dialog
    private ProgressDialog pDialog;

    private static Variables address = new Variables();

    // API urls
    private static String URL_LOAD_MAP = address.getAddress()+"?action=getReports";

    // API urls
    private static final String MAPVIEW_BUNDLE_KEY = "AIzaSyBW4FdYQdUxbGEoU8DIJz0suQTgJrMpM94";

    AlertDialogManager alert = new AlertDialogManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_view);



        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        if(isNetworkAvailable()){
            new loadMap().execute();
        }else{
            Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
        }

    }



    // Private class isNetworkAvailable
    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng currentPlace = new LatLng(-1.3099481,36.7831836);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPlace));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(9));

    }




    /**
     * Async task class to get json by making HTTP call
     * */
    private class loadMap extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReportView.this);
            pDialog.setMessage("loading..");
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0){
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String json = sh.makeServiceCall(URL_LOAD_MAP, ServiceHandler.GET,null);
            //shows the response that we got from the http request on the logcat
            Log.d("my_Response: ", "> " + json);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMap.clear();
                    mMarkersHashMap = new HashMap<Marker, MyMarker>();
                   // mMyMarkersArray.clear();
                    pDialog.dismiss();
                }
            });

            if (json != null) {

                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {

                        String status = jsonObj.getString("status");
                        String cat_id="";
                        String category="";
                        String name="";
                        Double mylongitude = 0.0;
                        Double mylatitude = 0.0;

                        if(status.equals("success")){
                            final JSONArray dataArr = jsonObj.getJSONArray("data");
                            for(int i =0; i<dataArr.length();i++){
                                JSONObject catObj = (JSONObject) dataArr.get(i);

                                cat_id = catObj.getString("id");
                                category = catObj.getString("crime_type");
                                name = catObj.getString("doctor_name");
                                mylongitude = catObj.getDouble("longitude");
                                mylatitude = catObj.getDouble("latitude");

                                Log.d("my_lat_long",mylatitude+""+mylongitude);

                                latitude_longitude = new LatLng(mylatitude, mylongitude);
                                mMyMarkersArray.add(new MyMarker(mylatitude,mylongitude,cat_id,name,category));

                            }
                            Log.d("size_of_arraylist",mMyMarkersArray.size()+"");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    if(mMyMarkersArray.size() > 0)
                                    {
                                        for (MyMarker myMarker : mMyMarkersArray)
                                        {
                                            Log.d("well_looped : ", "looped");
                                            // Create user marker with custom icon and other options
                                            MarkerOptions markerOption = new MarkerOptions().position(new LatLng(myMarker.getMlatitude(), myMarker.getMlongitudes()));
                                            try {
                                                markerOption.icon(BitmapDescriptorFactory.defaultMarker(manageMarkerIcon(myMarker.getMcategory())));
                                            }
                                            catch (Exception e){
                                                Log.d("error on click : ",e.getMessage());
                                            }
                                            Marker currentMarker = mMap.addMarker(markerOption);
                                                 mMarkersHashMap.put(currentMarker, myMarker);

                                            mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());

                                        }

                                    }

                                }
                            });
                        }else{
                            // Existing data
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    alert.showAlertDialog(
                                            ReportView.this,
                                            "Error",
                                            "Please try again",
                                            false);
                                    pDialog.dismiss();
                                }
                            });
                        }

                    } else {
//                        // Existing data
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                alert.showAlertDialog(
                                        ReportView.this,
                                        "Failed",
                                        "Failed",
                                        false);
                                pDialog.dismiss();

                            }
                        });
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }

            } else {
                // Error in connection
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        alert.showAlertDialog(
                                ReportView.this,
                                "Error",
                                "No internet connection",
                                false);
                        pDialog.dismiss();
                        alert.notify();
                    }
                });
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            // dismiss the dialog once done
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    //this method handles what happens when the location is changed
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }


    private void plotMarkers(ArrayList<MyMarker> markers)
    {
        Log.d("end_of_loading_codes : ", "plotted");
        Log.d("size_of_markers_array: ", ""+markers.size());

        if(markers.size() > 0)
        {
            for (MyMarker myMarker : markers)
            {
                Log.d("well_looped : ", "looped");
                // Create user marker with custom icon and other options
                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(myMarker.getMlatitude(), myMarker.getMlongitudes()));
                try {
                    markerOption.icon(BitmapDescriptorFactory.defaultMarker(manageMarkerIcon(myMarker.getMcategory())));
                }
                catch (Exception e){
                    Log.d("error on click : ",e.getMessage());
                }
                Marker currentMarker = mMap.addMarker(markerOption);
                mMarkersHashMap.put(currentMarker, myMarker);

                mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
            }

        }
    }

    //this method determines the color of the marker
    private float manageMarkerIcon(String category_type)
    {
        if(category_type.equals("Contractor")) {
            return BitmapDescriptorFactory.HUE_ORANGE;
        }

        else{
            return BitmapDescriptorFactory.HUE_RED;
        }
    }


    //this method shows the information on top of a marker
    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        public MarkerInfoWindowAdapter()
        {
        }
        @Override
        public View getInfoWindow(Marker marker)
        {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker)
        {
            View v  = getLayoutInflater().inflate(R.layout.marker_info, null);

            MyMarker myMarker = mMarkersHashMap.get(marker);

            TextView artisan_name = (TextView)v.findViewById(R.id.artisan_name);
            TextView artisan_type = (TextView)v.findViewById(R.id.artisan_type);
            try {

                artisan_name.setText(myMarker.getMname());
                artisan_type.setText(myMarker.getMcategory());
            }
            catch (Exception e){
                Log.d("error on click 2 : ",e.getMessage());
            }
            return v;
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }



}
