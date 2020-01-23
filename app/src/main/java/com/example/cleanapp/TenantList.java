package com.example.cleanapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TenantList extends AppCompatActivity {

    protected FloatingActionButton addTenantfloatingActionButton;
    String houseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i  = getIntent();
        houseID = i.getStringExtra("houseID");

        setContentView(R.layout.activity_tenant_list);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tenant list");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        addTenantfloatingActionButton = findViewById(R.id.addTenantfloatingActionButton);
        addTenantfloatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TenantList.this, HouseInvitation.class);
                i.putExtra("houseID",houseID);
                startActivity(i);
            }
        });




    }
}
