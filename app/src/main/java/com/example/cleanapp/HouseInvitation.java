package com.example.cleanapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.*;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.cleanapp.Model.HouseInvitationModel;

public class HouseInvitation extends AppCompatActivity {

    protected Toolbar myToolbar;
    protected EditText phoneNumberTenant;
    protected Button validationBtn;
    protected HouseInvitationModel myHouseInvitation;
    FirebaseAuth myFirebaseAuth;
    DatabaseReference getHouseInvitation = FirebaseDatabase.getInstance().getReference().child("Invitation House");
    String houseID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i  = getIntent();
        houseID = i.getStringExtra("houseID");

        setContentView(R.layout.activity_house_invitation);




        //myFirebaseAuth = FirebaseAuth.getInstance();

        myToolbar = findViewById(R.id.toolbar2);

        phoneNumberTenant = findViewById(R.id.editText);
        validationBtn = findViewById(R.id.valdBtn);
        myHouseInvitation = new HouseInvitationModel();
        //get n set the info owner to fill house invitation model
        //myHouseInvitation.setIdOwner(myFirebaseAuth.getCurrentUser().getUid());
        //datasnapshot from house .userID

        //get n set info Tenant

        validationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("STATE","Start click action");
                //start the houseInvitation
                //prepHouseInvit();
                //add myHouseInvitation to db
                Log.d("FIREPATH", getHouseInvitation.child(phoneNumberTenant.getText().toString()).toString());
                getHouseInvitation.child(phoneNumberTenant.getText().toString());
                getHouseInvitation.child(phoneNumberTenant.getText().toString()).child("House id").setValue(houseID);
                Log.d("try",houseID);
            }
        });


    }

    protected void prepHouseInvit()
    {
        myHouseInvitation.setIdOwner(myFirebaseAuth.getCurrentUser().getUid());
        myHouseInvitation.setIdHouse(houseID);
        myHouseInvitation.setOwnerPhone(Integer.parseInt(phoneNumberTenant.getText().toString()));

    }
}
