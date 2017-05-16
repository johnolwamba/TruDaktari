package com.doreenaradi.trudaktari;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.doreenaradi.trudaktari.adapters.SearchResultsRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchResultsView extends AppCompatActivity {

    RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results_view);

        recyclerView= (RecyclerView) findViewById(R.id.recyclerSearch);
//
//        Bundle bundle = getIntent().getExtras();
//        if(bundle!=null){
//            card_id = bundle.getString("card_id");
//            bank_name = bundle.getString("bank_name");
//            card_number = bundle.getString("card_number");
//            spending_limit = bundle.getString("spending_limit");
//            phone_number = bundle.getString("phone_number");
//            limit_duration = bundle.getString("limit_duration");
      //  }
//
//       // public SearchResultsRecyclerAdapter(Context context,String[] registration_number,String[] name,
////                                                    String[] registration_date,String[] address,String[] qualification,String[] type,String[] specialty)
////
//                SearchResultsRecyclerAdapter adapter=new SearchResultsRecyclerAdapter(SearchResultsView.this,
//                 listBankName.toArray(new String[listBankName.size()]),
//                listCardNo.toArray(new String[listCardNo.size()]),listCardID.toArray(new String[listCardID.size()]),
//                 listCardNo.toArray(new String[listCardNo.size()]),listCardID.toArray(new String[listCardID.size()]),
//                listShared.toArray(new Boolean[listShared.size()]));
//        recyclerView.setAdapter(adapter);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(ManageCardsView.this));


    }




}
