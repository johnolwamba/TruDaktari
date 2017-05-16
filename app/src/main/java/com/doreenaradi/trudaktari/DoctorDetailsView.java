package com.doreenaradi.trudaktari;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DoctorDetailsView extends AppCompatActivity {
    ImageView imageProfile;
    TextView txtNoReg,txtDate,txtAddress,txtQualifications,txtSpeciality,txtName;
    Button btnRate,btnReport,btnRecommend;
    String name = "";
    String registration_number = "";
    String type = "";
    String specialty = "";
    String registration_date = "";
    String qualification = "";
    String address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details_view);

        imageProfile = (ImageView) findViewById(R.id.imageProfile);
        txtNoReg = (TextView) findViewById(R.id.txtNoReg);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtQualifications = (TextView) findViewById(R.id.txtQualifications);
        txtSpeciality = (TextView) findViewById(R.id.txtSpeciality);
        txtName = (TextView) findViewById(R.id.txtName);

        btnRate = (Button) findViewById(R.id.btnRate);
        btnReport = (Button) findViewById(R.id.btnReport);
        btnRecommend = (Button) findViewById(R.id.btnRecommend);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){

            String name = "";
            String registration_number = "";
            String type = "";
            String specialty = "";
            String registration_date = "";
            String qualification = "";
            String address = "";


            name = bundle.getString("name");
            registration_number = bundle.getString("registration_number");
            type = bundle.getString("type");
            specialty = bundle.getString("specialty");
            registration_date = bundle.getString("registration_date");
            qualification = bundle.getString("qualification");
            address = bundle.getString("address");

            txtNoReg.setText(registration_number);
            txtDate.setText(registration_date);
            txtAddress.setText(address);
            txtQualifications.setText(qualification);
            txtSpeciality.setText(specialty);
            txtName.setText(name);

        }



        btnRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("name",txtName.getText().toString().trim());
                bundle.putString("registration_number",txtNoReg.getText().toString().trim());
                Intent i = new Intent(getApplicationContext(),RecommendView.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("name",txtName.getText().toString().trim());
                bundle.putString("registration_number",txtNoReg.getText().toString().trim());
                Intent i = new Intent(getApplicationContext(),RateView.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("name",txtName.getText().toString().trim());
                bundle.putString("registration_number",txtNoReg.getText().toString().trim());
                Intent i = new Intent(getApplicationContext(),ReportRegisteredView.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });


    }


}
