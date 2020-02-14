package com.example.cleanapp.ViewHolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cleanapp.Fragment.TenantListFragment;
import com.example.cleanapp.Model.TenantListModel;
import com.example.cleanapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class OwnerTenantViewAdapter extends RecyclerView.Adapter<OwnerTenantViewAdapter.TenantViewHolder> {

    private Context mContext;
    ArrayList<TenantListModel> userTenant;
    Context context;
    String houseID;


    public OwnerTenantViewAdapter(TenantListFragment mContext, ArrayList<TenantListModel> t, String HouseID){
        mContext = mContext;
        userTenant = t;
        houseID = HouseID;
    }


    @NonNull
    @Override
    public TenantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.listview_tenant, parent, false);
        return new TenantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnerTenantViewAdapter.TenantViewHolder holder, int position) {

//        holder.house_Name.setText(houseID);
        String num = userTenant.get(position).getNumber();
        holder.tenant_phone.setText(num);
        holder.tenant_name.setText(userTenant.get(position).getName());
        //holder.teant_profileImg.setImageIcon();
        String TenantID = userTenant.get(position).getIdTenant();
        Log.d("IS IT ?",TenantID);



        // Clicking the house will bring it to house view, where user can see rooms inside the house.

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context= v.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure, You want to remove the tenant from your house?" )
                        .setTitle("Delete Tenant")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //read db ref
                                DatabaseReference myRef= FirebaseDatabase.getInstance().getReference()
                                        .child("House")
                                        .child(getUserKeyFireAuth())
                                        .child(houseID)
                                        .child("Tenant")
                                        .child(TenantID);
                                myRef.removeValue();

                                //remove from db
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
    }

    @Override
    public int getItemCount() {
        return userTenant.size();
    }

    public class TenantViewHolder extends  RecyclerView.ViewHolder{

        public TextView tenant_name,tenant_phone;
        public CardView cardView;
        public ImageView teant_profileImg;

        public TenantViewHolder(@NonNull View itemView) {
            super(itemView);
            tenant_name = (TextView) itemView.findViewById(R.id.tenant_name_Textview);
            teant_profileImg = (ImageView) itemView.findViewById(R.id.TenantimageView2);
            tenant_phone = (TextView) itemView.findViewById(R.id.tenant_phone_TextView);
            cardView = (CardView) itemView.findViewById(R.id.tenant_cardview);


        }
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



