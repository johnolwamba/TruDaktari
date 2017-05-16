package com.doreenaradi.trudaktari;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.doreenaradi.trudaktari.adapters.AlertDialogManager;
import com.doreenaradi.trudaktari.adapters.SearchResultsRecyclerAdapter;
import com.doreenaradi.trudaktari.adapters.ServiceHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeView extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button btnSearch;
    EditText txtSearch;
    RecyclerView recyclerView;
    Spinner spinnerSearchType;

    String searchType = "";

    ProgressDialog pDialog;
    AlertDialogManager alert = new AlertDialogManager();
    private static String URL_VERIFY_DOCTORS = "https://6ujyvhcwe6.execute-api.eu-west-1.amazonaws.com/prod?q";
    private static String URL_VERIFY_HOSPITALS = "https://187mzjvmpd.execute-api.eu-west-1.amazonaws.com/prod?q=";

    ArrayList<String> listName = new ArrayList();
    ArrayList<String> listRegistrationDate = new ArrayList();
    ArrayList<String> listSpeciality = new ArrayList();
    ArrayList<String> listQualification = new ArrayList();
    ArrayList<String> listType = new ArrayList();
    ArrayList<String> listAddress = new ArrayList();
    ArrayList<String> listRegistration_number = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView= (RecyclerView) findViewById(R.id.recyclerSearch);
        txtSearch = (EditText) findViewById(R.id.txtSearch);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        spinnerSearchType = (Spinner) findViewById(R.id.spinnerSearchType);



        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchType = spinnerSearchType.getSelectedItem().toString();
               // Toast.makeText(getApplicationContext(), searchType, Toast.LENGTH_SHORT).show();
            if(txtSearch.getText().length() == 0)
            {
                Toast.makeText(getApplicationContext(), "Please enter a doctor or facility name", Toast.LENGTH_SHORT).show();
            }
            else if(!isNetworkAvailable()){
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();

            }
            else if(searchType.length() < 1){
                Toast.makeText(getApplicationContext(), "Please select search category", Toast.LENGTH_SHORT).show();
            }
            else if(searchType.equals("Doctor")){
                    new verifyDoctor().execute();
            }else{
                new verifyHospital().execute();
            }


            }
        });

     DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

     if (id == R.id.nav_search) {

        Intent i = new Intent(HomeView.this,HomeView.class);
         startActivity(i);

        } else if (id == R.id.nav_report) {

         Intent i = new Intent(HomeView.this,ReportView.class);
         startActivity(i);

        } else if (id == R.id.nav_scan) {

         Intent i = new Intent(HomeView.this,ScanActivity.class);
         startActivity(i);

        } else if (id == R.id.nav_usefulinfo) {

         Intent i = new Intent(HomeView.this,UsefulInfoView.class);
         startActivity(i);

        } else if (id == R.id.nav_help) {

         Intent i = new Intent(HomeView.this,AboutView.class);
         startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    /**
     * Async task class to get json by making HTTP call
     * */
    private class verifyDoctor extends AsyncTask<Void, Void, Void> {
        String searchText = txtSearch.getText().toString().trim();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeView.this);
            pDialog.setMessage("Verifying...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
           // params.add(new BasicNameValuePair("phone_number", tel));

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response

            String final_url = URL_VERIFY_DOCTORS+"="+searchText;

            String json = sh.makeServiceCall(final_url, ServiceHandler.GET,null);

            //shows the response that we got from the http request on the logcat
            Log.d("Response: ", "> " + json);
            listName.clear();
            listRegistrationDate.clear();
            listSpeciality.clear();
            listQualification.clear();
            listType.clear();
            listAddress.clear();
            listRegistration_number.clear();

            if (json != null) {

                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {

                        if(jsonObj.has("hits")){
                            JSONObject status = jsonObj.getJSONObject("status");
                            String name = "";
                            String registration_date = "";
                            String specialty = "";
                            String registration_number = "";
                            String qualification = "";
                            String type = "";
                            String address = "";

                            if (status.length() > 0) {
                                JSONObject hitObj = jsonObj.getJSONObject("hits");
                                final JSONArray dataArr = hitObj.getJSONArray("hit");
                                Log.d("Response: ", "> " + dataArr);
                                if(dataArr.length() <1){
                                    // Existing data
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            displayReportOption();
                                        }
                                    });

                                }


                                for (int i = 0; i < dataArr.length(); i++) {
                                    JSONObject catObj = (JSONObject) dataArr.get(i);

                                    JSONObject fieldsObj = (JSONObject) catObj.get("fields");

                                    name = fieldsObj.getString("name");
                                    registration_date = fieldsObj.getString("registration_date");
                                    specialty = fieldsObj.getString("specialty");
                                    qualification = fieldsObj.getString("qualification");
                                    address = fieldsObj.getString("address");
                                    type = fieldsObj.getString("type");
                                    registration_number = fieldsObj.getString("registration_number");

                                   listName.add(name);
                                    listRegistrationDate.add(registration_date);
                                    listSpeciality.add(specialty);
                                    listQualification.add(qualification);
                                    listType.add(type);
                                    listAddress.add(address);
                                    listRegistration_number.add(registration_number);


                                    }

                                    // Existing data
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                    SearchResultsRecyclerAdapter adapter=new SearchResultsRecyclerAdapter(HomeView.this,
                                            listRegistration_number.toArray(new String[listRegistration_number.size()]),
                                            listName.toArray(new String[listName.size()]),listRegistrationDate.toArray(new String[listRegistrationDate.size()]),
                                            listAddress.toArray(new String[listAddress.size()]),listQualification.toArray(new String[listQualification.size()]),
                                            listType.toArray(new String[listType.size()]),
                                            listSpeciality.toArray(new String[listSpeciality.size()]));
                                     recyclerView.setAdapter(adapter);
                                        recyclerView.setHasFixedSize(true);

                                      recyclerView.setLayoutManager(new LinearLayoutManager(HomeView.this));


                                        }
                                    });

                            } else {

                                listName.clear();
                                listRegistrationDate.clear();
                                listSpeciality.clear();
                                listQualification.clear();
                                listType.clear();
                                listAddress.clear();
                                listRegistration_number.clear();

                                // Existing data
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        alert.showAlertDialog(
                                                HomeView.this,
                                                "Error",
                                                "Please try again",
                                                false);
                                    }
                                });

                            }

                        }


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
                                HomeView.this,
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

    public void displayReportOption(){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                HomeView.this);
        builder.setMessage("Your search was not found. Are you sure the spelling is correct? If correct, proceed.")
                .setCancelable(false)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {

                            }

                        })
                .setPositiveButton("Report",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                Intent i = new Intent(HomeView.this,ReportUnregisteredView.class);
                                startActivity(i);
                            }

                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * Async task class to get json by making HTTP call
     * */
    private class verifyHospital extends AsyncTask<Void, Void, Void> {
        String searchText = txtSearch.getText().toString().trim();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeView.this);
            pDialog.setMessage("Verifying...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // params.add(new BasicNameValuePair("phone_number", tel));

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response

            String final_url = URL_VERIFY_HOSPITALS+"="+searchText+"~2";

            String json = sh.makeServiceCall(final_url, ServiceHandler.GET,null);

            //shows the response that we got from the http request on the logcat
            Log.d("Response: ", "> " + json);
            listName.clear();
            listRegistrationDate.clear();
            listSpeciality.clear();
            listQualification.clear();
            listType.clear();
            listAddress.clear();
            listRegistration_number.clear();

            if (json != null) {

                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {

                        if(jsonObj.has("hits")){
                            JSONObject status = jsonObj.getJSONObject("status");
                            String name = "";
                            String registration_date = "";
                            String specialty = "";
                            String registration_number = "";
                            String qualification = "";
                            String type = "";
                            String address = "";

                            if (status.length() > 0) {
                                JSONObject hitObj = jsonObj.getJSONObject("hits");
                                final JSONArray dataArr = hitObj.getJSONArray("hit");
                                Log.d("Response: ", "> " + dataArr);
                                if(dataArr.length() <1){
                                    // Existing data
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            displayReportOption();
                                        }
                                    });

                                }
                                for (int i = 0; i < dataArr.length(); i++) {
                                    JSONObject catObj = (JSONObject) dataArr.get(i);
                                    String hospital_id = catObj.getString("id");
                                    JSONObject fieldsObj = (JSONObject) catObj.get("fields");

                                    name = fieldsObj.getString("name");
                                    //registration_date = fieldsObj.getString("registration_date");
                                    specialty = fieldsObj.getString("facility_type_name");
                                    qualification = fieldsObj.getString("regulatory_body_name");
                                    address = fieldsObj.getString("sub_county_name");
                                    type = fieldsObj.getString("facility_type_name");
                                    registration_number = hospital_id;

                                    listName.add(name);
                                    listRegistrationDate.add(registration_date);
                                    listSpeciality.add(specialty);
                                    listQualification.add(qualification);
                                    listType.add(type);
                                    listAddress.add(address);
                                    listRegistration_number.add(registration_number);
                                }

                                // Existing data
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        SearchResultsRecyclerAdapter adapter=new SearchResultsRecyclerAdapter(HomeView.this,
                                                listRegistration_number.toArray(new String[listRegistration_number.size()]),
                                                listName.toArray(new String[listName.size()]),listRegistrationDate.toArray(new String[listRegistrationDate.size()]),
                                                listAddress.toArray(new String[listAddress.size()]),listQualification.toArray(new String[listQualification.size()]),
                                                listType.toArray(new String[listType.size()]),
                                                listSpeciality.toArray(new String[listSpeciality.size()]));
                                        recyclerView.setAdapter(adapter);
                                        recyclerView.setHasFixedSize(true);

                                        recyclerView.setLayoutManager(new LinearLayoutManager(HomeView.this));


                                    }
                                });

                            } else {

                                listName.clear();
                                listRegistrationDate.clear();
                                listSpeciality.clear();
                                listQualification.clear();
                                listType.clear();
                                listAddress.clear();
                                listRegistration_number.clear();

                                // Existing data
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        alert.showAlertDialog(
                                                HomeView.this,
                                                "Error",
                                                "Please try again",
                                                false);
                                    }
                                });

                            }

                        }


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
                                HomeView.this,
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


}
