package com.example.ambulancetrackingmodule.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ambulancetrackingmodule.R;
import com.example.ambulancetrackingmodule.adapter.RequestAdapter;
import com.example.ambulancetrackingmodule.databinding.FragmentPatientRequestBinding;
import com.example.ambulancetrackingmodule.interfaces.RequestItemClick;
import com.example.ambulancetrackingmodule.model.RequestModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PatientRequestFragment extends Fragment {
    FragmentPatientRequestBinding binding;
    Context mCtx;
    RequestAdapter adapter;
    List<RequestModel> modelList;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCtx = requireContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_patient_request, container, false
        );
        modelList = new ArrayList<>();
        setRcv();
        return binding.getRoot();
    }

    private void setRcv() {
        binding.requestRecycler.setLayoutManager(new LinearLayoutManager(mCtx));
        binding.requestRecycler.hasFixedSize();
        modelList.add(new RequestModel("Kamran", "kamran@gmail.com", "13:05 PM", 31.536111288666852, 74.2984312192782));
        modelList.add(new RequestModel("Abrar", "kamran@gmail.com", "9:05 AM", 31.41909551949637, 74.22992522591711));
        modelList.add(new RequestModel("Salman", "kamran@gmail.com", "12:05 PM", 31.537866978033495, 74.28748780722663));
        modelList.add(new RequestModel("Faraz", "kamran@gmail.com", "00:05 AM", 31.491843534288492, 74.23844664484284));
        modelList.add(new RequestModel("Moshin", "kamran@gmail.com", "02:05 AM", 31.431348070719167, 74.26492365663901));
        adapter = new RequestAdapter(requireContext(), modelList, new RequestItemClick() {
            @Override
            public void requestItemClick(Double lat, Double longi) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q= " + lat + "," + longi);
// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
// Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

// Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
            }
        });
        binding.requestRecycler.setAdapter(adapter);

    }


}