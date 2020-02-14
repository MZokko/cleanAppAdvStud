package com.example.cleanapp.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.cleanapp.Model.HouseInvitationModel;
import com.example.cleanapp.Model.TaskAssignCardModel;
import com.example.cleanapp.Model.UserModel;
import com.example.cleanapp.R;
import com.example.cleanapp.ViewHolder.TenantTaskListViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TenantHomeFragment extends Fragment {

    DatabaseReference getUserDetails, getInvite, addTenant, getTask, getTenantTask;

    TextView notificationText;
    Button agreeBtn;
    CardView homepageCard;

    private RecyclerView taskListRecyclerView;
    TenantTaskListViewAdapter adapter;

    ArrayList<TaskAssignCardModel> taskArrayList;

    protected UserModel userModel = new UserModel();
    protected HouseInvitationModel inviteModel = new HouseInvitationModel();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tenant_homepage, container, false);

        String userID = getUserKeyFireAuth();

        notificationText = (TextView) view.findViewById(R.id.notificationTextView);
        agreeBtn = (Button) view.findViewById(R.id.agreeBtn);

        homepageCard = (CardView) view.findViewById(R.id.tenantHomepageCardView);
        homepageCard.setVisibility(view.GONE);

        taskListRecyclerView = (RecyclerView) view.findViewById(R.id.taskListRecyclerView);
        taskListRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        taskListRecyclerView.setVisibility(view.GONE);

        taskArrayList = new ArrayList<>();

        getUserDetails = FirebaseDatabase.getInstance().getReference().child("User").child(userID);
        getUserDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userDetails.clear();
                userModel = dataSnapshot.getValue(UserModel.class);
                userModel.setUserKey(userID);
                getInviteData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTenant = FirebaseDatabase.getInstance().getReference().child("House").child(inviteModel.getIdOwner())
                        .child(inviteModel.getIdHouse()).child("Tenant").child(userID);
                addTenant.child("name").setValue(userModel.getUserFullName());
                addTenant.child("number").setValue(userModel.getUserPhone());
                getInvite = FirebaseDatabase.getInstance().getReference().child("Invitation House").child(userModel.getUserPhone());
                getInvite.child("isRead").setValue(true);


                // Refresh the screen
                Fragment frg = getFragmentManager().findFragmentById(R.id.fragment_container);
                FragmentTransaction ft = getFragmentManager().beginTransaction();

                ft.detach(frg);
                ft.attach(frg);
                ft.commit();

            }
        });

        return view;
    }

    void getInviteData(){
        getInvite = FirebaseDatabase.getInstance().getReference().child("Invitation House").child(userModel.getUserPhone());
        getInvite.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    inviteModel = dataSnapshot.getValue(HouseInvitationModel.class);

                    if(inviteModel.getisRead() == true){
                        Log.d("Read", "ID house" + inviteModel.getIdHouse() + "Get id owner" + inviteModel.getIdOwner() + "Get phone number" + userModel.getUserPhone());
                        taskListRecyclerView.setVisibility(getView().VISIBLE);
                        getTaskList();

                    }else{
                        homepageCard.setVisibility(getView().VISIBLE);
                        notificationText.setText("You have an invite from " + inviteModel.getIdHouse());
                        agreeBtn.setVisibility(getView().VISIBLE);
                    }
                }else{
                    homepageCard.setVisibility(getView().VISIBLE);
                    notificationText.setText("You don't have an invite yet");
                    agreeBtn.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void getTaskList(){
        getTask = FirebaseDatabase.getInstance().getReference().child("House").child(inviteModel.getIdOwner()).child(inviteModel.getIdHouse()).child("TaskAssign");
        getTask.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d("ID", "Room ID: " + ds.getKey());

                    // Go to find specific phone number
                    getOwnTask(ds.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void getOwnTask(String roomID){
// Find the tenantArrayList number on each child
        getTenantTask = FirebaseDatabase.getInstance().getReference().child("House").child(inviteModel.getIdOwner()).child(inviteModel.getIdHouse()).child("TaskAssign").child(roomID);
        getTenantTask.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                taskArrayList.clear();
                // For each room id
                for (DataSnapshot ds1 : dataSnapshot1.getChildren()) {
                    String phoneNumber = dataSnapshot1.child("TenantNumber").getValue().toString();

                    if(phoneNumber.equals(userModel.getUserPhone())){
                        TaskAssignCardModel taskAssign = new TaskAssignCardModel();
                        taskAssign = dataSnapshot1.getValue(TaskAssignCardModel.class);
                        taskAssign.setRoomID(roomID);
                        taskArrayList.add(taskAssign);
                        Log.d("IF correct: ", "Tenant Numb: " + phoneNumber);
                    }
                    break;
                }
                adapter = new TenantTaskListViewAdapter(TenantHomeFragment.this, taskArrayList, inviteModel.getIdOwner(), inviteModel.getIdHouse());
                taskListRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    protected String getUserKeyFireAuth(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String fireAuthUserKey="";

        if (user != null) {
            fireAuthUserKey = user.getUid();
        }

        return fireAuthUserKey;
    }


}
