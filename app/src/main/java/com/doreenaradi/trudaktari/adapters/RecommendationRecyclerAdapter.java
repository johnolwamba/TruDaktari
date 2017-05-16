package com.doreenaradi.trudaktari.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doreenaradi.trudaktari.R;


/**
 * Created by apple on 06/04/2017.
 */

public class RecommendationRecyclerAdapter extends  RecyclerView.Adapter<RecommendationRecyclerHolder> {

    private String[] name;
    private String[] id;
    private String[] comment;
    private String[] date;

    Context context;
    LayoutInflater inflater;
    public RecommendationRecyclerAdapter(Context context,String[] id,String[] name,String[] comment,String[] date) {
        this.context=context;
        this.id = id;
        this.name = name;
        this.comment = comment;
        this.date = date;

        inflater=LayoutInflater.from(context);
    }
    @Override
    public RecommendationRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=inflater.inflate(R.layout.recommendation_list_item, parent, false);

        RecommendationRecyclerHolder viewHolder=new RecommendationRecyclerHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecommendationRecyclerHolder holder, int position) {

        holder.txtName.setText("By: "+name[position]);
        holder.txtDate.setText(date[position]);
        holder.txtRecommendation.setText(comment[position]);
    }




    @Override
    public int getItemCount() {
        return name.length;
    }



}
