package com.example.ambulancetrackingmodule.activity.Patient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.ambulancetrackingmodule.R;
import com.example.ambulancetrackingmodule.activity.WelcomeActivity;
import com.example.ambulancetrackingmodule.databinding.ActivityPatientLoginBinding;
import com.example.ambulancetrackingmodule.presenter.showLoading;
import com.example.ambulancetrackingmodule.utils.DialogLoading;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class PatientLoginActivity extends AppCompatActivity implements showLoading.UpdateUI {
    ActivityPatientLoginBinding binding;
    DialogLoading mDialog;
    //Firebase
    private FirebaseAuth auth;
    //Context]
    private Context mCtx;

    //log
    private static final String TAG = "PatientLoginActivity";

    //Working Variables
    private String mEmail;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_login);
        setContentView(binding.getRoot());
        findViews();
        onClickListeners();
    }

    private void findViews() {
        auth = FirebaseAuth.getInstance();
        mCtx = getApplicationContext();
        mDialog = new DialogLoading(PatientLoginActivity.this);
    }

    private void onClickListeners() {
        binding.pregistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatientLoginActivity.this, PatientSignUpActivity.class));

            }
        });
        binding.patientLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                mEmail=binding.email.getText().toString();
                mPassword=binding.password.getText().toString();
                if (TextUtils.isEmpty(mEmail) && TextUtils.isEmpty(mPassword)) {
                    hideProgressBar();
                    Toast.makeText(getApplicationContext(), "Complete All Field", Toast.LENGTH_SHORT).show();
                }  else {
                    auth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                hideProgressBar();
                                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                            } else {
                                hideProgressBar();
                                Toast.makeText(getApplicationContext(), "Login Unsuccessfully", Toast.LENGTH_SHORT).show();
                            }

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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
    }
}