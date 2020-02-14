package com.example.cleanapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cleanapp.OwnerMainActivity;
import com.example.cleanapp.R;

public class OwnerProfileFragment extends Fragment {

    String userid, userFullName, userNumber, userAddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        final OwnerMainActivity activity = (OwnerMainActivity)getActivity();

        //Bundle results = activity.getUserID();            Getting the user ID
        //userid = results.getString("userID");

        return view;
    }
}
