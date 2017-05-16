package com.doreenaradi.trudaktari;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.doreenaradi.trudaktari.adapters.AlertDialogManager;
import com.doreenaradi.trudaktari.adapters.ServiceHandler;
import com.doreenaradi.trudaktari.adapters.Variables;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class ScanActivity extends AppCompatActivity {
    Button btnScan;
    String license_number = "";

    // Progress dialog
    private ProgressDialog pDialog;

    private static Variables address = new Variables();
    // API urls
    private static String URL_CHECK_FACILITY = address.getAddress()+"?action=checkLicense";

    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        btnScan = (Button) findViewById(R.id.btnScan);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQR();
            }
        });




    }


    //product qr code mode
    public void scanQR() {


        if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScanActivity.this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }else{
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            new IntentIntegrator(ScanActivity.this).initiateScan();

        }
    }



    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if (data != null) {
                if(data.getStringExtra("SCAN_RESULT") != null){

                    //get the extras that are returned from the intent
                    license_number = data.getStringExtra("SCAN_RESULT");
                    String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                    Toast.makeText(getApplicationContext(),license_number,Toast.LENGTH_SHORT).show();

                    if(license_number.length() < 1){
                        Toast.makeText(getApplicationContext(),"Please scan a valid facility code",Toast.LENGTH_SHORT).show();
                    }else if(!isNetworkAvailable()){
                        Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                    }else{
                        new verifyFacility().execute();
                    }

                }



            } else if (resultCode == RESULT_CANCELED) {

                // Handle cancel
            }
            //}


        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }


    @Override
    public void onBackPressed (){


    }


    /**
     * Async task class to get json by making HTTP call
     * */
    private class verifyFacility extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ScanActivity.this);
            pDialog.setMessage("checking...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String json = sh.makeServiceCall(URL_CHECK_FACILITY+"&license_number="+license_number, ServiceHandler.GET,null);

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
                                            ScanActivity.this,
                                            "Success",
                                            message,
                                            false);
                                    if (pDialog.isShowing())
                                        pDialog.dismiss();

                                }
                            });


                        }

                        else if(status.equals("error")){
                            final String message = jsonObj.get("message").toString();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    alert.showAlertDialog(
                                            ScanActivity.this,
                                            "Failed",
                                            message,
                                            false);
                                    if (pDialog.isShowing())
                                        pDialog.dismiss();
                                }
                            });

                        }

                        else {
                            // Existing data
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    alert.showAlertDialog(
                                            ScanActivity.this,
                                            "Failed",
                                            "Invalid licence number provided",
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
                                ScanActivity.this,
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
