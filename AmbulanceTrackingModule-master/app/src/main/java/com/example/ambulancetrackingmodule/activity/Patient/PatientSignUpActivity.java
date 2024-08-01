package com.example.ambulancetrackingmodule.activity.Patient;

import static com.example.ambulancetrackingmodule.utils.Constants.LAT;
import static com.example.ambulancetrackingmodule.utils.Constants.LONG;
import static com.example.ambulancetrackingmodule.utils.Constants.P_CNIC;
import static com.example.ambulancetrackingmodule.utils.Constants.P_EMAIL;
import static com.example.ambulancetrackingmodule.utils.Constants.P_NAME;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.ambulancetrackingmodule.R;
import com.example.ambulancetrackingmodule.activity.WelcomeActivity;
import com.example.ambulancetrackingmodule.databinding.ActivityPatientSignUpBinding;
import com.example.ambulancetrackingmodule.model.Users;
import com.example.ambulancetrackingmodule.presenter.showLoading;
import com.example.ambulancetrackingmodule.utils.Constants;
import com.example.ambulancetrackingmodule.utils.DialogLoading;
import com.example.ambulancetrackingmodule.utils.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatientSignUpActivity extends AppCompatActivity implements showLoading.UpdateUI {
    ActivityPatientSignUpBinding binding;
    DialogLoading mDialog;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    //Context]
    private Context mCtx;


    //Working Variables
    private String mEmail;
    private String mPassword;
    private String mFullName;
    private String mCnic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_sign_up);
        setContentView(binding.getRoot());
        findViews();
        onClickListeners();
    }

    private void findViews() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mCtx = getApplicationContext();
        mDialog = new DialogLoading(PatientSignUpActivity.this);

    }

    private void onClickListeners() {
        binding.plogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatientSignUpActivity.this, PatientLoginActivity.class));

            }
        });
        binding.signup.setOnClickListener(v -> {
            downlod();
            showProgressBar();
            mEmail = binding.email.getText().toString();
            mFullName = binding.name.getText().toString();
            mPassword = binding.password.getText().toString();
            mCnic = binding.cnic.getText().toString();
            if (TextUtils.isEmpty(mEmail) && TextUtils.isEmpty(mPassword) && TextUtils.isEmpty(mFullName) && TextUtils.isEmpty(mCnic)) {
                hideProgressBar();
                Toast.makeText(getApplicationContext(), "Complete All Field", Toast.LENGTH_SHORT).show();
            } else if (!isEmailValid(mEmail)) {
                hideProgressBar();
                Toast.makeText(getApplicationContext(),
                        "Please enter a valid email address!",
                        Toast.LENGTH_LONG)
                        .show();
            } else if (mPassword.length() < 6) {
                hideProgressBar();
                Toast.makeText(mCtx, "Password Should be 6 Digit ", Toast.LENGTH_SHORT).show();
            } else {
                signUp();
            }

        });
    }

    private void signUp() {
        hideProgressBar();
        mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DatabaseReference reference = database.getReference().child("AllPatientsData").child(mAuth.getUid());
                    Users users = new Users(mAuth.getUid(),
                            mFullName,
                            mEmail,
                            mCnic
                    );
                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            hideProgressBar();
                            startActivity(new Intent(PatientSignUpActivity.this, PatientLoginActivity.class));
                            SharedPrefManager.setStringValue(PatientSignUpActivity.this, P_CNIC, mCnic, Constants.SHARED_PREF_FILE_KEY);
                            SharedPrefManager.setStringValue(PatientSignUpActivity.this, P_NAME, mFullName, Constants.SHARED_PREF_FILE_KEY);
                            SharedPrefManager.setStringValue(PatientSignUpActivity.this, P_EMAIL, mEmail, Constants.SHARED_PREF_FILE_KEY);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressBar();
                            Toast.makeText(mCtx, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    hideProgressBar();
                    Toast.makeText(mCtx, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean isEmailValid(String email) {
        // You can add more checking logic here.
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+()!=])"
                + "(?=\\S+$).{11,20}$";
        Pattern p = Pattern.compile(regex);   //Constants.PASSWORD_PATTERN_REGEX);
        Matcher m = p.matcher(password);
        return m.matches();
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
        startActivity(new Intent(getApplicationContext(), PatientLoginActivity.class));
    }

    private void downlod() {
        final Dialog progress = new Dialog(PatientSignUpActivity.this);

        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.loading);
        progress.setCancelable(false);
        Window mWindow;
        mWindow = progress.getWindow();
        mWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progress.show();

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                progress.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 4000);
    }
}