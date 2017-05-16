package com.doreenaradi.trudaktari;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.doreenaradi.trudaktari.adapters.AlertDialogManager;
import com.doreenaradi.trudaktari.adapters.ServiceHandler;
import com.doreenaradi.trudaktari.adapters.Variables;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

public class RateView extends AppCompatActivity {
     RatingBar ratingBar;
    Button btnSubmit;
    EditText txtName,txtPhone,txtComment,txtDoctorName;
    String myrating = "";
    String doctor_registration_number = "";

    // Progress dialog
    private ProgressDialog pDialog;

    private static Variables address = new Variables();
    // API urls
    private static String URL_RATE = address.getAddress()+"?action=rateUser";

    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_view);
        addListenerOnRatingBar();

        txtName = (EditText) findViewById(R.id.txtName);
        txtPhone = (EditText) findViewById(R.id.txtPhone);
        txtComment = (EditText) findViewById(R.id.txtComment);
        txtDoctorName = (EditText) findViewById(R.id.txtDoctorName);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);


        addListenerOnButton();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            String name = "";
            name = bundle.getString("name");
            doctor_registration_number = bundle.getString("registration_number");
            txtDoctorName.setText(name);
            txtDoctorName.setEnabled(false);
        }


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(txtDoctorName.getText().toString().trim().length() < 1){
                    Toast.makeText(getApplicationContext(),"Please enter doctor/facility name",Toast.LENGTH_SHORT).show();
                }else if(txtComment.getText().toString().trim().length() < 1){
                    Toast.makeText(getApplicationContext(),"Please enter comment",Toast.LENGTH_SHORT).show();
                }else if(txtName.getText().toString().trim().length() < 1){
                    Toast.makeText(getApplicationContext(),"Please provide us with your name",Toast.LENGTH_SHORT).show();
                }else if(!isNetworkAvailable()){
                    Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                }else{
                    new addRating().execute();
                }

            }
        });


    }


    public void addListenerOnRatingBar() {
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
        public void onRatingChanged(RatingBar ratingBar, float rating,  boolean fromUser) {
            myrating = String.valueOf(rating);

            }
        });
    }

    public void addListenerOnButton() {
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        myrating = String.valueOf(ratingBar.getRating());
       Toast.makeText(RateView.this, String.valueOf(ratingBar.getRating()), Toast.LENGTH_LONG).show();
    }




    /**
     * Async task class to get json by making HTTP call
     * */
    private class addRating extends AsyncTask<Void, Void, Void> {
        String doctor_name = txtDoctorName.getText().toString().trim();
        String rater_name = txtName.getText().toString().trim();
        String rater_phone = txtPhone.getText().toString().trim();
        String comment = txtComment.getText().toString().trim();
        String user_rating = myrating;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RateView.this);
            pDialog.setMessage("loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String json = sh.makeServiceCall(URL_RATE+"&doctor_name="+doctor_name+"&rater_name="+rater_name+"&phone="
                    +rater_phone+"&comment="+comment+"&rate="+user_rating+"&doctor_registration_number="+doctor_registration_number, ServiceHandler.GET,null);

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
                                            RateView.this,
                                            "Success",
                                            message,
                                            false);
                                    if (pDialog.isShowing())
                                        pDialog.dismiss();

                                    txtName.setText("");
                                    txtPhone.setText("");
                                    txtComment.setText("");
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
                                            RateView.this,
                                            "Failed",
                                            message,
                                            false);
                                    if (pDialog.isShowing())
                                        pDialog.dismiss();
                                    txtName.setText("");
                                    txtPhone.setText("");
                                    txtComment.setText("");
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
                                            RateView.this,
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
                                RateView.this,
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
