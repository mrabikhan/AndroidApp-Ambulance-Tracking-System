package com.example.ambulancetrackingmodule.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ambulancetrackingmodule.R;
import com.example.ambulancetrackingmodule.fragment.AccountDetailsFragment;
import com.example.ambulancetrackingmodule.fragment.AddAmbulance;
import com.example.ambulancetrackingmodule.fragment.AddPhoneNumberFragment;
import com.example.ambulancetrackingmodule.fragment.AidTopsFragment;
import com.example.ambulancetrackingmodule.fragment.DriverAccountDetailsFragment;
import com.example.ambulancetrackingmodule.fragment.PatientRequestFragment;
import com.example.ambulancetrackingmodule.fragment.SearchNearHospital;
import com.example.ambulancetrackingmodule.utils.Constants;

public class FragmentControllerActivity extends AppCompatActivity {

    private String mFragValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_controller);
        mFragValue = Constants.FRAG_VAL;
        if (mFragValue.equals("nearHospital")) {
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragmentContainer, new SearchNearHospital()).commit();
        } else if (mFragValue.equals("account")) {
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragmentContainer, new AccountDetailsFragment()).commit();
        } else if (mFragValue.equals("addNumber")) {
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragmentContainer, new AddPhoneNumberFragment()).commit();
        } else if (mFragValue.equals("addAmbulance")) {
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragmentContainer, new AddAmbulance()).commit();
        } else if (mFragValue.equals("patientRequest")) {
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragmentContainer, new PatientRequestFragment()).commit();
        } else if (mFragValue.equals("tip_for_aid")) {
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragmentContainer, new AidTopsFragment()).commit();
        } else if (mFragValue.equals("driver_account")) {
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragmentContainer, new DriverAccountDetailsFragment()).commit();
        }
//
    }

}