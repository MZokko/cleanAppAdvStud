package com.example.cleanapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cleanapp.Model.Room;
import com.example.cleanapp.Model.TaskAssignCardModel;
import com.example.cleanapp.Model.TenantListModel;
import com.example.cleanapp.OwnerTaskHistory;
import com.example.cleanapp.R;
import com.example.cleanapp.ViewHolder.OwnerTaskAssignViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class OwnerCalendarFragment extends Fragment {

    //retrieve the info from db
    DatabaseReference getRoom, getTenant,getTaskAssign, addTaskHistory;


    //prep array for assignation
    ArrayList<Room> rooms = new ArrayList<>();
    ArrayList<TenantListModel> tenantArrayList = new ArrayList<>();
    ArrayList<TaskAssignCardModel> taskAssignCard = new ArrayList<>();
    ArrayList<Integer> garbage = new ArrayList<Integer>();
    ArrayList<TaskAssignCardModel>assignationList = new ArrayList<>();

    String houseID;
    String userID;

    View view;
    //recycler
    OwnerTaskAssignViewAdapter adapter;
    RecyclerView TaskAssignRecycler;

    public OwnerCalendarFragment(String houseID) {
        Bundle bundle = new Bundle();
        bundle.putString("house", houseID);
        this.setArguments(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_owner_calendar, container, false);
        TaskAssignRecycler = (RecyclerView) view.findViewById(R.id.recyclerViewAssignTask);
        TaskAssignRecycler.setLayoutManager(new GridLayoutManager(this.getContext(), 1));
        adapter = new OwnerTaskAssignViewAdapter(assignationList, OwnerCalendarFragment.this);
        TaskAssignRecycler.setAdapter(adapter);


        Bundle bundle = getArguments();
        houseID = bundle.getString("house");
        userID = getUserKeyFireAuth();

        //prep arrays

        //btn
        Button btnRandom = view.findViewById(R.id.button);
        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTaskHistory();
            }
        });

        Button historyBtn = view.findViewById(R.id.buttonHistory);
        historyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), OwnerTaskHistory.class);
            intent.putExtra("HouseID", houseID);
            intent.putExtra("UserID", userID);
            startActivity(intent);
        });
        //prep array

        //load Tenant array on create
        getTenant = FirebaseDatabase.getInstance().getReference().child("House").child(userID).child(houseID).child("Tenant");
        getTenant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tenantArrayList.clear();
                Iterable<DataSnapshot> childrenTenant = dataSnapshot.getChildren();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String tenantID = dataSnapshot1.getKey();
                    //start model
                    TenantListModel t = new TenantListModel();
                    //fill model

                    t = dataSnapshot1.getValue(TenantListModel.class);
                    t.setIdTenant(tenantID);
                    //add to array
                    tenantArrayList.add(t);
                }
                Log.d("TENANT LIST", "onDataChange: " + tenantArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //load Room Array
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
                Log.d("TAG", "onDataChange: " + rooms);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        assignationList = new ArrayList<>();
        getTaskAssign = FirebaseDatabase.getInstance().getReference().child("House").child(userID).child(houseID).child("TaskAssign");
        getTaskAssign.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                assignationList.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot ds : children){
                    String taskAssignCardModelID = ds.getKey() ;
                    TaskAssignCardModel mytaskAssignCardModel = new TaskAssignCardModel();
                    mytaskAssignCardModel = ds.getValue(TaskAssignCardModel.class);
                    assignationList.add(mytaskAssignCardModel);
                }
                adapter = new OwnerTaskAssignViewAdapter(assignationList, OwnerCalendarFragment.this);
                TaskAssignRecycler.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("tas assign", "Failed to read value.", databaseError.toException());
            }
        });
        return view;
    }

    protected String getUserKeyFireAuth() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String fireAuthUserKey = "";

        if (user != null) {
            fireAuthUserKey = user.getUid();
        }

        return fireAuthUserKey;
    }

    protected void saveTaskHistory(){

        String date = new SimpleDateFormat("yyyy-MMM-dd", Locale.getDefault()).format(new Date());
        Log.d("Date", "Current date: " + date);

        addTaskHistory = FirebaseDatabase.getInstance().getReference().child("House").child(userID).child(houseID).child("TaskHistory");

        getTaskAssign = FirebaseDatabase.getInstance().getReference().child("House").child(userID).child(houseID).child("TaskAssign");
        getTaskAssign.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot ds : children){
                    Boolean isDone = (Boolean) ds.child("isDone").getValue();
                    Log.d("isDone", "Is done? " + isDone.toString());
                    if(isDone == false){
                        String tenantName = (String) ds.child("TenantName").getValue();
                        count++;
                        Log.d("Check done", "Tenant Name: " + tenantName + " Count: " + count);
                        addTaskHistory.child(date).child(tenantName).setValue("Task not completed: " + count);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("tas assign", "Failed to read value.", databaseError.toException());
            }
        });

        prepArrayRoomAndRand();
    }

    protected void prepArrayRoomAndRand() {

        int totalTask = rooms.size();
        int count = 1;
        while (rooms.size() > 0) {
            TaskAssignCardModel taskAssignCardModel = new TaskAssignCardModel();

            int randNumGarb = (int) (Math.random() * ((rooms.size() - 1) + 1)) + 0;

            int tenantIndex = count % tenantArrayList.size();

            taskAssignCardModel.setRoomName(rooms.get(randNumGarb).getRoomName());
            taskAssignCardModel.setRoomDescription(rooms.get(randNumGarb).getRoomdescription());
            taskAssignCardModel.setTenantName(tenantArrayList.get(tenantIndex).getName());
            taskAssignCardModel.setTenantNumber(tenantArrayList.get(tenantIndex).getNumber());

            taskAssignCard.add(taskAssignCardModel);
            //need to track in db
            String roomID = rooms.get(randNumGarb).getRoomID();
            String tenantID = tenantArrayList.get(tenantIndex).getIdTenant();
            //Write to database
            //write on House....OWNERID...HouseID ... tenantArrayList...TenanID
            getRoom = FirebaseDatabase.getInstance().getReference().child("House").child(userID).child(houseID).child("TaskAssign");

            //add to house task assign
            getRoom.child(roomID).child("TenantName").setValue(taskAssignCardModel.getTenantName());
            getRoom.child(roomID).child("TenantNumber").setValue(taskAssignCardModel.getTenantNumber());
            getRoom.child(roomID).child("isDone").setValue(false);
            getRoom.child(roomID).child("roomDescription").setValue(taskAssignCardModel.getRoomDescription());
            getRoom.child(roomID).child("roomName").setValue(taskAssignCardModel.getRoomName());

            count++;
            rooms.remove(randNumGarb);
        }
    }

    protected void taskAssignUi(){
        assignationList = new ArrayList<>();
        getTaskAssign = FirebaseDatabase.getInstance().getReference().child("House").child(userID).child(houseID).child("TaskAssign");
        getTaskAssign.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                assignationList.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot ds : children){
                    String taskAssignCardModelID = ds.getKey() ;
                    TaskAssignCardModel mytaskAssignCardModel = new TaskAssignCardModel();
                    mytaskAssignCardModel = ds.getValue(TaskAssignCardModel.class);
                    assignationList.add(mytaskAssignCardModel);
                }
                adapter = new OwnerTaskAssignViewAdapter(assignationList, OwnerCalendarFragment.this);
                TaskAssignRecycler.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("tas assign", "Failed to read value.", databaseError.toException());
            }
        });
    }

}

