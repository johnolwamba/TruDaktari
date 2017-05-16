package com.doreenaradi.trudaktari.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doreenaradi.trudaktari.R;


/**
 * Created by apple on 06/04/2017.
 */

public class RecommendationRecyclerHolder extends RecyclerView.ViewHolder  {

    TextView txtName,txtDate,txtRecommendation;
    LinearLayout recommendation_list_item;

    public RecommendationRecyclerHolder(View itemView) {
        super(itemView);

        txtName= (TextView) itemView.findViewById(R.id.txtName);
        txtDate= (TextView) itemView.findViewById(R.id.txtDate);
        txtRecommendation= (TextView) itemView.findViewById(R.id.txtRecommendation);
        recommendation_list_item = (LinearLayout) itemView.findViewById(R.id.recommendation_list_item);
    }


}
