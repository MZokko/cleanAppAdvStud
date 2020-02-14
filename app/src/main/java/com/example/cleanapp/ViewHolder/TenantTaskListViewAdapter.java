package com.example.cleanapp.ViewHolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cleanapp.Fragment.TenantHomeFragment;
import com.example.cleanapp.Model.TaskAssignCardModel;
import com.example.cleanapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TenantTaskListViewAdapter extends RecyclerView.Adapter<TenantTaskListViewAdapter.TenantListViewHolder> {

    ArrayList<TaskAssignCardModel> taskArrayList;
    String ownerid, houseid;
    Context context;

    public TenantTaskListViewAdapter(TenantHomeFragment mContext, ArrayList<TaskAssignCardModel> tasks, String ownerID, String houseID) {
        mContext = mContext;
        taskArrayList = tasks;
        ownerid = ownerID;
        houseid = houseID;
    }

    @NonNull
    @Override
    public TenantListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.listview_rooms, parent, false);
        return new TenantListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TenantListViewHolder holder, int position) {

        String roomName = taskArrayList.get(position).getRoomName();
        String room_desc = taskArrayList.get(position).getRoomDescription();
        Boolean taskComplete = taskArrayList.get(position).getisDone();
        String roomID = taskArrayList.get(position).getRoomID();
        Log.d("Room id", roomID);
        holder.room_name.setText(roomName);
        holder.room_description.setText("Task completed: " + taskComplete.toString());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context = v.getContext();

                // Make a dialog box to show more detail
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(roomName + "\n\nTask to do: \n" + room_desc)
                        .setTitle("VERIFY")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                taskArrayList.clear();
                                DatabaseReference changeStatus = FirebaseDatabase.getInstance().getReference()
                                        .child("House").child(ownerid).child(houseid).child("TaskAssign").child(roomID);
                                changeStatus.child("isDone").setValue(true);
                            }
                        });

                builder.show();
                builder.create();
            }
        });

    }

    @Override
    public int getItemCount() {
        return taskArrayList.size();
    }

    public class TenantListViewHolder extends RecyclerView.ViewHolder {

        public TextView room_name;
        public CardView cardView;
        public TextView room_description;

        public TenantListViewHolder(@NonNull View itemView) {
            super(itemView);

            room_name = (TextView) itemView.findViewById(R.id.room_name_Textview);
            room_description = (TextView) itemView.findViewById(R.id.room_description_TextView);
            cardView = (CardView) itemView.findViewById(R.id.room_cardview);
        }
    }
}
