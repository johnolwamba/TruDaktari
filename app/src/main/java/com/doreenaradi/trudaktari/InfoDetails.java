package com.doreenaradi.trudaktari;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class InfoDetails extends AppCompatActivity {
String details = "";
TextView txtDisplay,txtHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_details);

        txtDisplay = (TextView) findViewById(R.id.txtDisplay);
        txtHeader = (TextView) findViewById(R.id.txtHeader);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            details = bundle.getString("detail_type");

            }
            if(details.equals("license")){

                txtDisplay.setText("Any person who wilfully and falsely takes or uses any name, title" +
                        " or addition implying a qualification to practise medicine or surgery or dentistry," +
                                " or who, not being registered or licensed under this Act, practises or professes to practise " +
                                "or publishes his name as practising medicine or surgery or dentistry, or who, not being licensed " +
                                " practises as a private practitioner, shall be guilty of an offence and liable to a fine not exceeding " +
                                "ten thousand shillings, or to imprisonment for a term not exceeding twelve months.\n");

                txtHeader.setText("Penalty for unregistered and unlicensed person practising\n");

            }else if(details.equals("penalty")){

                txtDisplay.setText("A person who wilfully procures or attempts to procure himself to be registered or\n" +
                        "licensed under any of the provisions of this Act by making or producing or causing to be made or produced any false" +
                        " or fraudulent representation or declaration either orally or in writing, and a person aiding or assisting him therein, " +
                        "shall be guilty of an offence and shall be liable to a fine not exceeding three thousand shillings or to imprisonment for " +
                        "a term not exceeding twelve months, or to both; and if a person convicted of an offence under this section is registered" +
                        " or licensed under this Act the Registrar shall forthwith remove his name from the register or cancel his licence, as the case may be.\n");


                txtHeader.setText("Penalty for fraudulently procuring registration or licence\n");

            }else{

                txtDisplay.setText("A licensee shall display his licence in a conspicuous position at the premises to which it relates and any clinical" +
                        " officer who practises without displaying his licence in accordance with this provision" +
                        " commits professional malpractice and is liable to be charged by the Council a fine not exceeding five thousand shillings. \n");


                txtHeader.setText("Display of licence\n");

            }


    }



}
