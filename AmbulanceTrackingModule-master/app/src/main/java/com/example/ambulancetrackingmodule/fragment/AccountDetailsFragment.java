package com.example.ambulancetrackingmodule.fragment;

import static android.app.Activity.RESULT_OK;

import static com.example.ambulancetrackingmodule.utils.Constants.Address;
import static com.example.ambulancetrackingmodule.utils.Constants.P_CNIC;
import static com.example.ambulancetrackingmodule.utils.Constants.P_EMAIL;
import static com.example.ambulancetrackingmodule.utils.Constants.P_NAME;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.ambulancetrackingmodule.R;
import com.example.ambulancetrackingmodule.activity.WelcomeActivity;
import com.example.ambulancetrackingmodule.model.Users;
import com.example.ambulancetrackingmodule.utils.Constants;
import com.example.ambulancetrackingmodule.utils.DialogLoading;
import com.example.ambulancetrackingmodule.utils.SharedPrefManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class AccountDetailsFragment extends Fragment {

    View view;
    ImageView account_back_button, edit_email, edit_phone, change_profile1;
    CircleImageView user_profile_pic;
    TextView user_name, first_name, last_name, mEmail, phone_no, address;
    DialogLoading mDialog;
    TextView profile_screen_email;
    AppCompatButton logout;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private DatabaseReference mReference1;

    String userNmae,email,cnic;

    public AccountDetailsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.fragment_account_details, container, false);
        mDialog = new DialogLoading(requireActivity());
        findView();
//        setText();
        onClickListeners();

        return view;
    }

    private void findView() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference().child("AllPatientsData").child(mAuth.getUid());
        mReference1 = mDatabase.getReference().child("AllPatientsData").child(mAuth.getUid());
        account_back_button = view.findViewById(R.id.account_back_button);
        user_name = view.findViewById(R.id.user_name);
        user_profile_pic = view.findViewById(R.id.user_profile_picture);
        mEmail = view.findViewById(R.id.profile_screen_email_text);
        phone_no = view.findViewById(R.id.profile_screen_lifesavers_received_text);
        profile_screen_email = view.findViewById(R.id.profile_screen_email);
        address = view.findViewById(R.id.profile_screen_muted_text);
        edit_email = view.findViewById(R.id.edit_email);
        edit_phone = view.findViewById(R.id.edit_phone);
        logout = view.findViewById(R.id.logOut);
        change_profile1 = view.findViewById(R.id.change_profile1);
        getData();
    }

    private void getData() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        userNmae = map.get("name").toString();
                        user_name.setText(userNmae);
                        Log.i("customer_name", String.valueOf(userNmae));

                    }
                    if (map.get("email") != null) {
                        email = map.get("email").toString();
                        mEmail.setText(email);
                        Log.i("customer_name", String.valueOf(email));

                    }
                    if (map.get("cnic") != null) {
                        cnic = map.get("cnic").toString();
                        phone_no.setText(cnic);
                        Log.i("customer_name", String.valueOf(cnic));

                    }
                    address.setText(SharedPrefManager.getStringValue(requireContext(), Address, "", Constants.SHARED_PREF_FILE_KEY));


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", databaseError.getMessage());
            }
        };
        mReference.addListenerForSingleValueEvent(valueEventListener);
    }

    private void setText() {
        String[] fullName = SharedPrefManager.getStringValue(requireContext(), P_NAME, "", Constants.SHARED_PREF_FILE_KEY).split(" ");
        user_name.setText(SharedPrefManager.getStringValue(requireContext(), P_NAME, "", Constants.SHARED_PREF_FILE_KEY));
//        first_name.setText(fullName[0]);
//        last_name.setText(fullName[1]);

        mEmail.setText(SharedPrefManager.getStringValue(requireContext(), P_EMAIL, "", Constants.SHARED_PREF_FILE_KEY));
        address.setText(SharedPrefManager.getStringValue(requireContext(), Address, "", Constants.SHARED_PREF_FILE_KEY));
        phone_no.setText(SharedPrefManager.getStringValue(requireContext(), P_CNIC, "", Constants.SHARED_PREF_FILE_KEY));
//        Glide.with(requireContext())
//                .load(SharedPrefManager.getStringValue(requireContext(), USER_PROFILE_IMAGE, "", Constants.SHARED_PREF_FILE_KEY))
//                .placeholder(R.drawable.profile_avatar)
//                .error(R.drawable.profile_avatar)
//                .into(user_profile_pic);
    }

    private void onClickListeners() {

        account_back_button.setOnClickListener(v -> {

            requireActivity().onBackPressed();
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), WelcomeActivity.class));
                getActivity().finish();
            }
        });

    }

//    private void showDeactivateDialog() {
//
//        mInitialDialog = new Dialog(requireContext());
//        mInitialDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        mInitialDialog.setContentView(R.layout.deactivate_alert_dialouge);
//        mInitialDialog.setCancelable(false);
//        Window mWindow;
//        mWindow = mInitialDialog.getWindow();
//        mWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        mWindow.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
//
//
//        Button dialog_dismiss = mInitialDialog.findViewById(R.id.cancel_deactivate_button);
//        Button deactivateAccount = mInitialDialog.findViewById(R.id.deactivate_button);
//
//        dialog_dismiss.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mInitialDialog.dismiss();
//            }
//        });
//        deactivateAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showProgressBar();
//                mPresenter.callDeactivateAPI(SharedPrefManager.getStringValue(requireContext(), ACCESS_TOKEN, "", Constants.SHARED_PREF_FILE_KEY),
//                        Constants.HEADER_TOKEN_TYPE,
//                        SharedPrefManager.getStringValue(requireContext(), CLIENT_FOR_HEADER, "", Constants.SHARED_PREF_FILE_KEY),
//                        SharedPrefManager.getStringValue(requireContext(), U_ID, "", Constants.SHARED_PREF_FILE_KEY));
//            }
//        });
//
//    }
//
//    private void showUpdateEmailDialog() {
//
//        mUpdateEmail = new Dialog(requireContext());
//        mUpdateEmail.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        mUpdateEmail.setContentView(R.layout.update_user_dialouge);
//        mUpdateEmail.setCancelable(false);
//        mUpdateEmail.getWindow().setGravity(Gravity.CENTER);
//        Window mWindow;
//        mWindow = mUpdateEmail.getWindow();
//        mWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        mWindow.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
//
//        EditText email = mUpdateEmail.findViewById(R.id.ed_update_email);
//        ImageView dialog_dismiss = mUpdateEmail.findViewById(R.id.dismiss_user_dialog);
//        Button updateEmail = mUpdateEmail.findViewById(R.id.update_button);
//
//        dialog_dismiss.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mUpdateEmail.dismiss();
//            }
//        });
//        updateEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(email.getText().toString().trim())) {
//                    email.setError("Enter email");
//                    Toast.makeText(requireContext(), "Please enter Your email", Toast.LENGTH_SHORT).show();
//                } else if (!isEmailValid(email.getText().toString().trim())) {
//                    Toast.makeText(requireContext(),
//                            "Please enter a valid email address!",
//                            Toast.LENGTH_LONG)
//                            .show();
//                    return;
//                } else {
//                    UpdateEmailRequest mRequest = new UpdateEmailRequest(email.getText().toString().trim());
//                    showProgressBar();
//                    mPresenter.callUpdateEmailAPI(SharedPrefManager.getStringValue(requireContext(), ACCESS_TOKEN, "", Constants.SHARED_PREF_FILE_KEY),
//                            Constants.HEADER_TOKEN_TYPE,
//                            SharedPrefManager.getStringValue(requireContext(), CLIENT_FOR_HEADER, "", Constants.SHARED_PREF_FILE_KEY),
//                            SharedPrefManager.getStringValue(requireContext(), U_ID, "", Constants.SHARED_PREF_FILE_KEY), mRequest);
//                    mUpdateEmail.dismiss();
//                    email.setText("");
//                }
//            }
//        });
//
//    }
//
//    private void showUpdatePhoneDialog() {
//
//        mUpdatePhone = new Dialog(requireContext());
//        mUpdatePhone.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        mUpdatePhone.setContentView(R.layout.update_user_number_dialouge);
//        mUpdatePhone.setCancelable(false);
//        mUpdatePhone.getWindow().setGravity(Gravity.CENTER);
//        Window mWindow;
//        mWindow = mUpdatePhone.getWindow();
//        mWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        mWindow.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
//
//        CCP_updated = mUpdatePhone.findViewById(R.id.updated_country_code_picker);
//        update_phone_no = mUpdatePhone.findViewById(R.id.update_phone_number);
//        ImageView dialog_dismiss = mUpdatePhone.findViewById(R.id.dismiss_phone_dialog);
//        Button verifyNumber = mUpdatePhone.findViewById(R.id.verify_button);
//
//        dialog_dismiss.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mUpdatePhone.dismiss();
//            }
//        });
//        verifyNumber.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(update_phone_no.getText().toString().trim())) {
//                    update_phone_no.setError("Enter phone number");
//                    Toast.makeText(requireContext(), "Please enter your phone number", Toast.LENGTH_SHORT).show();
//                } else {
//                    showProgressBar();
//                    phone_number = update_phone_no.getText().toString().trim();
//                    UpdateNumberRequest request = new UpdateNumberRequest(CCP_updated.getSelectedCountryCode(), phone_number);
//                    mPresenter.callUpdateNumberAPI(SharedPrefManager.getStringValue(requireContext(), ACCESS_TOKEN, "", Constants.SHARED_PREF_FILE_KEY),
//                            Constants.HEADER_TOKEN_TYPE,
//                            SharedPrefManager.getStringValue(requireContext(), CLIENT_FOR_HEADER, "", Constants.SHARED_PREF_FILE_KEY),
//                            SharedPrefManager.getStringValue(requireContext(), U_ID, "", Constants.SHARED_PREF_FILE_KEY), request);
//
//                    mUpdatePhone.dismiss();
//                    update_phone_no.setText("");
//                }
//            }
//        });
//
//    }
//
//    private void navigateToLoginActivity() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(requireContext(), LoginActivity.class);
//                startActivity(intent);
//                requireActivity().finish();
//            }
//        }, 2000);
//    }
//
//    private boolean isEmailValid(String email) {
//        // You can add more checking logic here.
//        return email.contains("@");
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//
//        assert data != null;
//        if (resultCode == RESULT_OK) {
//            profileUri = data.getData();
//            user_profile_pic.setImageURI(profileUri);
//            updateProfileImage();
//        }
//
//    }
//
//    private void updateProfileImage() {
//        showProgressBar();
//        File file = new File(profileUri.getPath());
//        // Create a request body with file and image media type
//        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
//        // Create MultipartBody.Part using file request-body,file name and part name
//        MultipartBody.Part part = MultipartBody.Part.createFormData("profile_image_url", file.getName(), fileReqBody);
//        //Create request body with text description and text media type
//        mPresenter.callUpdateProfileImageAPI(
//                SharedPrefManager.getStringValue(requireContext(), ACCESS_TOKEN, "", Constants.SHARED_PREF_FILE_KEY),
//                Constants.HEADER_TOKEN_TYPE,
//                SharedPrefManager.getStringValue(requireContext(), CLIENT_FOR_HEADER, "", Constants.SHARED_PREF_FILE_KEY),
//                SharedPrefManager.getStringValue(requireContext(), U_ID, "", Constants.SHARED_PREF_FILE_KEY),
//                part
//        );
//    }
}