package com.doreenaradi.trudaktari.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.doreenaradi.trudaktari.DoctorDetailsView;
import com.doreenaradi.trudaktari.R;
import com.squareup.picasso.Picasso;

/**
 * Created by apple on 18/04/2017.
 */

public class SearchResultsRecyclerAdapter extends  RecyclerView.Adapter<SearchResultsRecyclerHolder> {

    private String[] name;
    private String[] registration_number;
    private String[] registration_date;
    private String[] address;
    private String[] qualification;
    private String[] type;
    private String[] specialty;

    Context context;
    LayoutInflater inflater;
    public SearchResultsRecyclerAdapter(Context context,String[] registration_number,String[] name,
                                        String[] registration_date,String[] address,String[] qualification,String[] type,String[] specialty) {
        this.context=context;
        this.registration_number = registration_number;
        this.name = name;
        this.registration_date = registration_date;
        this.address = address;
        this.qualification = qualification;
        this.type = type;
        this.specialty = specialty;

        inflater=LayoutInflater.from(context);
    }
    @Override
    public SearchResultsRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=inflater.inflate(R.layout.search_list_item, parent, false);

        SearchResultsRecyclerHolder viewHolder=new SearchResultsRecyclerHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchResultsRecyclerHolder holder, int position) {

        holder.txtName.setText(name[position]);
        holder.txtNumber.setText(registration_number[position]);
        holder.txtType.setText(type[position]);
        holder.search_list_item.setOnClickListener(clickListener);
        holder.search_list_item.setTag(holder);
    }


    View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            SearchResultsRecyclerHolder vholder = (SearchResultsRecyclerHolder) v.getTag();
            int position = vholder.getPosition();
            Log.d("clicked","clicked"+position);

            Bundle bundle = new Bundle();
            bundle.putString("name",name[position]);
            bundle.putString("registration_number",registration_number[position]);
            bundle.putString("type",type[position]);
            bundle.putString("specialty",specialty[position]);
            bundle.putString("registration_date",registration_date[position]);
            bundle.putString("qualification",qualification[position]);
            bundle.putString("address",address[position]);
            Intent i = new Intent(context.getApplicationContext(),DoctorDetailsView.class);
            i.putExtras(bundle);
            context.startActivity(i);

        }
    };


        @Override
    public int getItemCount() {
        return name.length;
    }





}


