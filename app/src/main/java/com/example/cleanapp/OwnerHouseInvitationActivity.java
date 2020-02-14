package com.example.cleanapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cleanapp.Model.HouseInvitationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OwnerHouseInvitationActivity extends AppCompatActivity {

    protected Toolbar myToolbar;
    protected EditText phoneNumberTenant;
    protected Button validationBtn;
    protected HouseInvitationModel myHouseInvitation;

    Context context;

    FirebaseAuth myFirebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference getHouseInvitation, checkHouseInvite;
    String houseID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i  = getIntent();
        houseID = i.getStringExtra("houseID");

        setContentView(R.layout.activity_house_invitation);

        getHouseInvitation = FirebaseDatabase.getInstance().getReference().child("Invitation House");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("House invite");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        phoneNumberTenant = findViewById(R.id.tenantPhoneEditText);
        validationBtn = findViewById(R.id.validBtn);
        myHouseInvitation = new HouseInvitationModel();

        //get n set info Tenant

        validationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context = v.getContext();
                checkDatabase(phoneNumberTenant.getText().toString(), context);
            }
        });


    }

    protected void checkDatabase(String tenantNumber, Context mContext){
        checkHouseInvite = getHouseInvitation.child(tenantNumber);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        checkHouseInvite.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    builder.setMessage("There is an invite for that number already.\nPlease check the number again")
                            .setTitle("ALERT")
                            .setPositiveButton("Ok", ((dialog, which) -> dialog.dismiss()));

                    builder.show();
                    builder.create();

                    Log.d("Data exist", "Error data exist");
                }else{
                    prepHouseInvit();
                    addHouseInviteDB();
                    returntoTenantList();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected  void returntoTenantList(){
        Toast.makeText(OwnerHouseInvitationActivity.this, "Your invitation have been sent",Toast.LENGTH_SHORT).show();
        Intent i = new Intent(OwnerHouseInvitationActivity.this, OwnerHouseActivityTab.class);
        i.putExtra("houseID",houseID);
        startActivity(i);
    }

    protected  void addHouseInviteDB(){
        getHouseInvitation.child(phoneNumberTenant.getText().toString());
        getHouseInvitation.child(phoneNumberTenant.getText().toString()).child("idHouse").setValue(myHouseInvitation.getIdHouse());
        getHouseInvitation.child(phoneNumberTenant.getText().toString()).child("idOwner").setValue(myHouseInvitation.getIdOwner());
        getHouseInvitation.child(phoneNumberTenant.getText().toString()).child("isRead").setValue(myHouseInvitation.getisRead());

    }

    protected void prepHouseInvit(){
        myHouseInvitation.setIdOwner(myFirebaseAuth.getCurrentUser().getUid());
        myHouseInvitation.setIdHouse(houseID);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

}
