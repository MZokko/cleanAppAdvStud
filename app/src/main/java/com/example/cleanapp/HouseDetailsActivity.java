package com.example.cleanapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.cleanapp.Model.House;
import com.example.cleanapp.Model.Room;
import com.example.cleanapp.ViewHolder.HomeViewAdapter;
import com.example.cleanapp.ViewHolder.RoomViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HouseDetailsActivity extends AppCompatActivity {

    DatabaseReference mDatabase, getRoom;
    private RecyclerView roomListRecycleView;
    RoomViewAdapter adapter;
    ArrayList<Room> rooms;

    protected FloatingActionButton tenantListFloatingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_details);

        Intent intent = getIntent();
        String houseID = intent.getStringExtra("houseID");
        String userID = getUserKeyFireAuth();

        tenantListFloatingButton = findViewById(R.id.tenantListFloatingButton);
        tenantListFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HouseDetailsActivity.this, TenantList.class);
                i.putExtra("houseID",houseID);
                startActivity(i);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("House Management");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        rooms = new ArrayList<>();
        roomListRecycleView = (RecyclerView)findViewById(R.id.roomListRecyclerView);
        roomListRecycleView.setLayoutManager(new GridLayoutManager(this,1));

        getRoom = FirebaseDatabase.getInstance().getReference().child("House").child(userID).child(houseID).child("Rooms");
        getRoom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rooms.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot ds : children) {
                    String roomID = ds.getKey();
                    Room room = new Room();
                    room = ds.getValue(Room.class);
                    room.setRoomID(roomID);
                    room.setHouseID(houseID);
                    rooms.add(room);
                }
                adapter = new RoomViewAdapter(HouseDetailsActivity.this, rooms);
                roomListRecycleView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    protected String getUserKeyFireAuth(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String fireAuthUserKey="";

        if (user != null) {
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            fireAuthUserKey = user.getUid();
        }

        return fireAuthUserKey;
    }
}
