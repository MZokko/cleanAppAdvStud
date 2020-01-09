package com.example.cleanapp.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cleanapp.HouseDetailsActivity;
import com.example.cleanapp.Model.Room;
import com.example.cleanapp.R;

import java.util.ArrayList;

public class RoomViewAdapter extends RecyclerView.Adapter<RoomViewAdapter.RoomViewHolder> {

    ArrayList<Room> roomArrayList;
    private Context mContext;

    public RoomViewAdapter(HouseDetailsActivity mContext, ArrayList<Room> rooms) {
        mContext = mContext;
        roomArrayList = rooms;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.listview_rooms, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        String roomName = roomArrayList.get(position).getRoomName();
        String room_desc = roomArrayList.get(position).getRoomdescription();

        holder.room_name.setText(roomName);
        holder.room_description.setText(room_desc);
    }

    @Override
    public int getItemCount() {
        return  roomArrayList.size();
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder {

        public TextView room_name;
        public CardView cardView;
        public TextView room_description;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            room_name = (TextView) itemView.findViewById(R.id.room_name_Textview);
            room_description = (TextView) itemView.findViewById(R.id.room_description_TextView);
            cardView = (CardView) itemView.findViewById(R.id.room_cardview);

        }
    }
}
