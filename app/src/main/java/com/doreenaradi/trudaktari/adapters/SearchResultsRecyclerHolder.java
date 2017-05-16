package com.doreenaradi.trudaktari.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doreenaradi.trudaktari.R;

/**
 * Created by apple on 18/04/2017.
 */

public class SearchResultsRecyclerHolder extends RecyclerView.ViewHolder {

    TextView txtName,txtNumber,txtType;
    ImageView imgProfile;
    LinearLayout search_list_item;

    public SearchResultsRecyclerHolder(View itemView) {
        super(itemView);

        txtName= (TextView) itemView.findViewById(R.id.txtName);
        txtNumber= (TextView) itemView.findViewById(R.id.txtNumber);
        txtType= (TextView) itemView.findViewById(R.id.txtType);
        imgProfile = (ImageView) itemView.findViewById(R.id.imgProfile);
        search_list_item = (LinearLayout) itemView.findViewById(R.id.search_list_item);
    }
}
