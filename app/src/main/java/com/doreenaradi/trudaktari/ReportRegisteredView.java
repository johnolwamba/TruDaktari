package com.doreenaradi.trudaktari;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.doreenaradi.trudaktari.adapters.AlertDialogManager;
import com.doreenaradi.trudaktari.adapters.ServiceHandler;
import com.doreenaradi.trudaktari.adapters.Variables;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

public class ReportRegisteredView extends AppCompatActivity {
    Spinner spinnerCategory;
    EditText txtName,txtPhone,txtReport,txtDoctorName;
    Button btnSubmit,btnSelectLocation;
    private static final int PLACE_PICKER_REQUEST = 1000;
    private GoogleApiClient mClient;

    String finallatitude = "";
    String finallongitude = "";
    String crime_type = "";
    // Progress dialog
    private ProgressDialog pDialog;

    private static Variables address = new Variables();
    // API urls
    private static String URL_REPORT = address.getAddress()+"?action=reportUser";

    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_registered_view);
        txtName = (EditText) findViewById(R.id.txtName);
        txtPhone = (EditText) findViewById(R.id.txtPhone);
        txtReport = (EditText) findViewById(R.id.txtReport);
        txtDoctorName = (EditText) findViewById(R.id.txtDoctorName);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSelectLocation = (Button) findViewById(R.id.btnSelectLocation);
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);

        mClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();


        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            String name = "";
            name = bundle.getString("name");
            txtDoctorName.setText(name);
            txtDoctorName.setEnabled(false);
        }


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crime_type = spinnerCategory.getSelectedItem().toString();
                if(crime_type.length() <1){
                    Toast.makeText(getApplicationContext(),"Please select the type of crime commited",Toast.LENGTH_SHORT).show();
                }else if(finallatitude.length() < 1){
                    Toast.makeText(getApplicationContext(),"Please select a location",Toast.LENGTH_SHORT).show();
                }else if(txtDoctorName.getText().toString().trim().length() < 1){
                    Toast.makeText(getApplicationContext(),"Please enter offenders name",Toast.LENGTH_SHORT).show();
                }else if(txtReport.getText().toString().trim().length() < 1){
                    Toast.makeText(getApplicationContext(),"Please enter report details",Toast.LENGTH_SHORT).show();
                }else if(txtName.getText().toString().trim().length() < 1){
                    Toast.makeText(getApplicationContext(),"Please provide us with your name",Toast.LENGTH_SHORT).show();
                }else if(!isNetworkAvailable()){
                    Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                }else{
                    new addReport().execute();
                }

            }
        });



        btnSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(ReportRegisteredView.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        mClient.connect();
    }
    @Override
    protected void onStop() {
        mClient.disconnect();
        super.onStop();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                StringBuilder stBuilder = new StringBuilder();
                String placename = String.format("%s", place.getName());
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                String address = String.format("%s", place.getAddress());
                stBuilder.append("Name: ");
                stBuilder.append(placename);
                stBuilder.append("\n");
                stBuilder.append("Latitude: ");
                stBuilder.append(latitude);
                stBuilder.append("\n");
                stBuilder.append("Logitude: ");
                stBuilder.append(longitude);
                stBuilder.append("\n");
                stBuilder.append("Address: ");
                stBuilder.append(address);

                finallatitude = String.valueOf(place.getLatLng().latitude);
                finallongitude = String.valueOf(place.getLatLng().longitude);
                Toast.makeText(getApplicationContext(),"Location selected",Toast.LENGTH_SHORT).show();
            }
        }
    }



    /**
     * Async task class to get json by making HTTP call
     * */
    private class addReport extends AsyncTask<Void, Void, Void> {
        String doctor_name = txtDoctorName.getText().toString().trim();
        String reporter_name = txtName.getText().toString().trim();
        String reporter_phone = txtPhone.getText().toString().trim();
        String report_details = txtReport.getText().toString().trim();
        String latitude = finallatitude;
        String longitude = finallongitude;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReportRegisteredView.this);
            pDialog.setMessage("loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String json = sh.makeServiceCall(URL_REPORT+"&doctor_name="+doctor_name+"&reporter_name="+reporter_name+"&reporter_phone="
                    +reporter_phone+"&report_details="+report_details+"&latitude="+latitude
                    +"&longitude="+longitude+"&crime_type="+crime_type, ServiceHandler.GET,null);

            Log.d("my_url",URL_REPORT+"&doctor_name="+doctor_name+"&reporter_name="+reporter_name+"&reporter_phone="
                    +reporter_phone+"&report_details="+report_details+"&latitude="+latitude
                    +"&longitude="+longitude+"&crime_type="+crime_type);

            //shows the response that we got from the http request on the logcat
            Log.d("Response: ", "> " + json);
            //result = jsonStr;
            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    String token = null;
                    if (jsonObj != null) {
                        String status = jsonObj.get("status").toString();
                        if (status.equals("success")) {
                            // Existing data
                            final String message = jsonObj.get("message").toString();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    alert.showAlertDialog(
                                            ReportRegisteredView.this,
                                            "Success",
                                            message,
                                            false);
                                    if (pDialog.isShowing())
                                        pDialog.dismiss();

                                    txtName.setText("");
                                    txtPhone.setText("");
                                    txtReport.setText("");
                                    txtDoctorName.setText("");
                                }
                            });


                        }

                        else if(status.equals("error")){
                            final String message = jsonObj.get("message").toString();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    alert.showAlertDialog(
                                            ReportRegisteredView.this,
                                            "Failed",
                                            message,
                                            false);
                                    if (pDialog.isShowing())
                                        pDialog.dismiss();
                                    txtName.setText("");
                                    txtPhone.setText("");
                                    txtReport.setText("");
                                    txtDoctorName.setText("");
                                }
                            });

                        }

                        else {
                            // Existing data
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    alert.showAlertDialog(
                                            ReportRegisteredView.this,
                                            "Failed",
                                            "Invalid report Provided",
                                            false);
                                    pDialog.dismiss();
                                }
                            });


                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                //Log.e("JSON Data", "Didn't receive any data from server!");
                // Error in connection
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        alert.showAlertDialog(
                                ReportRegisteredView.this,
                                "Error",
                                "No internet connection",
                                false);
                        pDialog.dismiss();
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
            //add intent
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

}
