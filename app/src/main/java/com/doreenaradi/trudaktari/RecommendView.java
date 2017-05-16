package com.doreenaradi.trudaktari;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doreenaradi.trudaktari.adapters.AlertDialogManager;
import com.doreenaradi.trudaktari.adapters.RecommendationRecyclerAdapter;
import com.doreenaradi.trudaktari.adapters.ServiceHandler;
import com.doreenaradi.trudaktari.adapters.Variables;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecommendView extends AppCompatActivity {
    RecyclerView recyclerView;

    ArrayList<String> listName = new ArrayList();
    ArrayList<String> listID = new ArrayList();
    ArrayList<String> listComment = new ArrayList();
    ArrayList<String> listDate = new ArrayList();
    String id = "";
    String doctor_name = "";
    String doctor_registration_number = "";
    String userRecommendation = "",userName = "", doctorName="";

    // Progress dialog
    private ProgressDialog pDialog;

    private static Variables address = new Variables();
    // API urls
    private static String URL_RECOMMENDATIONS = address.getAddress()+"?action=getRecommendations";
    private static String URL_RECOMMEND_USER = address.getAddress()+"?action=recommendUser";

    AlertDialogManager alert = new AlertDialogManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            doctor_name = bundle.getString("name");
            doctor_registration_number = bundle.getString("registration_number");
        }



        recyclerView= (RecyclerView) findViewById(R.id.recommendationRecyclerView);
        if(isNetworkAvailable()){
            new myRecommendations().execute();
        }else{
            Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendDialog();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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


    /**
     * Async task class to get json by making HTTP call
     * */
    private class myRecommendations extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RecommendView.this);
            pDialog.setMessage("loading..");
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected Void doInBackground(Void... arg0){
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String json = sh.makeServiceCall(URL_RECOMMENDATIONS+"&registration_number="+doctor_registration_number, ServiceHandler.GET,null);

            //shows the response that we got from the http request on the logcat
            Log.d("my_Response: ", "> " + json+URL_RECOMMENDATIONS+"&registration_number="+doctor_registration_number);

            listName.clear();
            listID.clear();
            listComment.clear();
            listDate.clear();

            if (json != null) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.dismiss();
                    }
                });

                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {

                        String status = jsonObj.getString("status");
                        String id="";
                        String name="";
                        String comment="";
                        String date="";

                        if(status.equals("success")){
                            final JSONArray dataArr = jsonObj.getJSONArray("data");

                            if(dataArr.length()<1){
                                // Existing data
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"No recommendations at the moment",Toast.LENGTH_SHORT).show();
                                        pDialog.dismiss();
                                    }
                                });
                            }
                            for(int i =0; i<dataArr.length();i++){
                                JSONObject catObj = (JSONObject) dataArr.get(i);

                                id = catObj.getString("doctor_registration_number");
                                name = catObj.getString("recommender_name");
                                comment = catObj.getString("recommendation");
                                //date = catObj.getString("08-05-2017");

                                listName.add(name);
                                listID.add(id);
                                listComment.add(comment);
                                listDate.add(date);

                            }
                            // Existing data
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pDialog.dismiss();
                                    RecommendationRecyclerAdapter adapter=new RecommendationRecyclerAdapter(RecommendView.this,listID.toArray(new String[listID.size()]),
                                            listName.toArray(new String[listName.size()]),listComment.toArray(new String[listComment.size()]),listDate.toArray(new String[listDate.size()]));
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(RecommendView.this));
                                }
                            });
                        }else{
                            listName.clear();
                            listID.clear();

                            // Existing data
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    alert.showAlertDialog(
                                            RecommendView.this,
                                            "Recommendation",
                                            "Please try again",
                                            false);
                                    pDialog.dismiss();


                                }
                            });

                        }

                    } else {
                        // Existing data
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                alert.showAlertDialog(
                                        RecommendView.this,
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
                                RecommendView.this,
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





    public void recommendDialog(){
        // custom dialog
        final Dialog dialog = new Dialog(RecommendView.this);
        dialog.setContentView(R.layout.add_recomendation);
        dialog.setTitle("Recommend");

        TextView text = (TextView) dialog.findViewById(R.id.textView30);

        final EditText txtRecommendation = (EditText) dialog.findViewById(R.id.txtRecommendation);
        final EditText txtName = (EditText) dialog.findViewById(R.id.txtName);
        final EditText txtDoctorName = (EditText) dialog.findViewById(R.id.txtDoctorName);
        Button dialogButtonSubmit = (Button) dialog.findViewById(R.id.btnSubmit);
        // if button is clicked, close the custom dialog
        dialogButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtRecommendation.getText().toString().trim().length() > 0 ){
                    userRecommendation = txtRecommendation.getText().toString().trim();
                    userName = txtName.getText().toString().trim();
                    doctorName = txtDoctorName.getText().toString().trim();
                    new recommendUser().execute();
                }else{
                    Toast.makeText(getApplicationContext(),"Please fill in the recommendation",Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    /**
     * Async task class to get json by making HTTP call
     * */
    private class recommendUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RecommendView.this);
            pDialog.setMessage("loading...");
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            String url = URL_RECOMMEND_USER+"&recommender_name=" + userName
                    +"&doctor_name="+doctorName+"&recommendation="+userRecommendation
                    +"&doctor_registration_number=" +doctor_registration_number;
            // Making a request to url and getting response
            String json = sh.makeServiceCall(url, ServiceHandler.GET, null);

            //shows the response that we got from the http request on the logcat
            Log.d("my_Response: ", "> " + json + "URL:" + url);

            if(json != null){
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {

                        String status = jsonObj.getString("status");
                        if(status.equals("success")){
                            final String message = jsonObj.getString("message");
                            // Existing data
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                    new myRecommendations().execute();
                                }
                            });
                        }else{
                            final String message = jsonObj.getString("message");
                            // Existing data
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } else {
//                        // Existing data
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                alert.showAlertDialog(
                                        RecommendView.this,
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
                                RecommendView.this,
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





}
