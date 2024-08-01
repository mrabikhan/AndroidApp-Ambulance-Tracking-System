package com.example.ambulancetrackingmodule.fragment;

import static com.example.ambulancetrackingmodule.utils.Constants.AmbulanceNumber;
import static com.example.ambulancetrackingmodule.utils.Constants.P_PHONE_NO;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ambulancetrackingmodule.R;
import com.example.ambulancetrackingmodule.activity.Driver.DriverMapsActivity;
import com.example.ambulancetrackingmodule.activity.Patient.MapsActivity;
import com.example.ambulancetrackingmodule.databinding.FragmentAddAmbulanceBinding;
import com.example.ambulancetrackingmodule.model.addNumber;
import com.example.ambulancetrackingmodule.presenter.showLoading;
import com.example.ambulancetrackingmodule.utils.Constants;
import com.example.ambulancetrackingmodule.utils.DialogLoading;
import com.example.ambulancetrackingmodule.utils.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddAmbulance extends Fragment implements showLoading.UpdateUI {
    FragmentAddAmbulanceBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DialogLoading mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_ambulance, container, false);
        findViews();
        onClickListeners();
        return binding.getRoot();
    }

    private void findViews() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDialog = new DialogLoading(requireActivity());
    }

    private void onClickListeners() {
        binding.phoneBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), DriverMapsActivity.class));
            }
        });
        binding.addAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                String amb_number=binding.addAmbulanceNumber.getText().toString();
                if (TextUtils.isEmpty(amb_number)){
                    Toast.makeText(requireContext(), "Add your ambulance number", Toast.LENGTH_SHORT).show();
                }else {
                    DatabaseReference reference = mDatabase.getReference().child("AmbulanceNumber").child(mAuth.getUid());
                    addNumber users = new addNumber(amb_number,mAuth.getUid());
                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            hideProgressBar();
                            Toast.makeText(requireContext(), "Ambulance added successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(requireContext(), DriverMapsActivity.class));
                            SharedPrefManager.setStringValue(requireContext(), AmbulanceNumber, amb_number, Constants.SHARED_PREF_FILE_KEY);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressBar();
                            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void showProgressBar() {
        mDialog.startLoadingDialog();
    }

    @Override
    public void hideProgressBar() {
        mDialog.dismissDialog();
    }
}