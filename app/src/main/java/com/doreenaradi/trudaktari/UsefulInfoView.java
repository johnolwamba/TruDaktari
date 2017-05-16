package com.doreenaradi.trudaktari;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UsefulInfoView extends AppCompatActivity {
TextView txtLicense,txtPenalty,txtNoReg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useful_info_view);

        txtLicense = (TextView)findViewById(R.id.txtLicense);
        txtPenalty = (TextView)findViewById(R.id.txtPenalty);
        txtNoReg = (TextView)findViewById(R.id.txtNoReg);


        txtLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("detail_type","license");
                Intent intent = new Intent(UsefulInfoView.this,InfoDetails.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        txtPenalty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("detail_type","penalty");
                Intent intent = new Intent(UsefulInfoView.this,InfoDetails.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });


        txtNoReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("detail_type","register");
                Intent intent = new Intent(UsefulInfoView.this,InfoDetails.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });






    }



}
