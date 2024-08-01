package com.example.ambulancetrackingmodule.activity.Patient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.ambulancetrackingmodule.R;
import com.example.ambulancetrackingmodule.activity.FragmentControllerActivity;
import com.example.ambulancetrackingmodule.activity.WelcomeActivity;
import com.example.ambulancetrackingmodule.model.RequestModel;
import com.example.ambulancetrackingmodule.utils.Constants;
import com.example.ambulancetrackingmodule.utils.HamburgerDrawable;
import com.example.ambulancetrackingmodule.utils.SharedPrefManager;
import com.example.ambulancetrackingmodule.utils.Util;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.ambulancetrackingmodule.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener, RoutingListener {

    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationClient;
    Location mLastLocation;
    private ActivityMapsBinding binding;
    ActionBarDrawerToggle mDrawerToggle;
    Context context;
    AutocompleteSupportFragment autocompleteFragment;
    Location location;
    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    double currentLatitude;
    double currentLongitude;

    private final static int LOCATION_REQUEST_CODE = 23;

    LatLng endLatLng = new LatLng(31.484844761348935, 74.29738116865052);
    TextView mLogout, callAmbulance, nearHospital, account, addNumber, tip_for_aid;

    LatLng startlatLng;

    Marker mMarker, mMarker2;

    Polyline currentPolyLine;
    private ProgressDialog progressDialog;

    private List<Polyline> polylines;

    TextView textView;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private DatabaseReference mReferenceValue;

    String name, email;

    private static final int[] COLORS = new int[]{R.color.green, R.color.red, R.color.orange, R.color.orange_red, R.color.primary_dark_material_light};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps);
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        findViews();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();
        if (isOnline()) {

            mReference = mDatabase.getReference().child("AllPatientsData").child(mAuth.getUid());
            getData();
            polylines = new ArrayList<>();
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            try {
                Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = gc.getFromLocation(31.484844761348935, 74.29738116865052, 1);
                String add = addresses.get(0).getAddressLine(0);
                binding.hosptalpatientAddressTv.setText(add);
                binding.patientAddressTv.setText(SharedPrefManager.getStringValue(MapsActivity.this, Constants.Address, "", Constants.SHARED_PREF_FILE_KEY));

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Create the LocationRequest object
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // 1 second, in milliseconds

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_REQUEST_CODE);
            } else {
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                assert mapFragment != null;
                mapFragment.getMapAsync(this);

            }


        } else {
            try {
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Error")
                        .setMessage("Internet not available, Cross check your internet connectivity and try again later...")
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();

                            }
                        }).show();
            } catch (Exception e) {
                Log.d("TAG", "Show Dialog: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }

    private void findViews() {
        context = getApplicationContext();
        mLogout = findViewById(R.id.nav_logout);
        callAmbulance = findViewById(R.id.callAmbulance);
        nearHospital = findViewById(R.id.nearHospital);
        account = findViewById(R.id.account);
        addNumber = findViewById(R.id.addNumber);
        tip_for_aid = findViewById(R.id.tip_for_aid);
        textView = findViewById(R.id.numberTxt);

        setupToolbar();
        setupDrawerToggle();

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MapsActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        callAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "+923313774424";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });
        nearHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constants.FRAG_VAL = "nearHospital";
                startActivity(new Intent(MapsActivity.this, FragmentControllerActivity.class));
            }
        });
        tip_for_aid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constants.FRAG_VAL = "tip_for_aid";
                startActivity(new Intent(MapsActivity.this, FragmentControllerActivity.class));
            }
        });
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constants.FRAG_VAL = "account";
                startActivity(new Intent(MapsActivity.this, FragmentControllerActivity.class));
            }
        });
        addNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constants.FRAG_VAL = "addNumber";
                startActivity(new Intent(MapsActivity.this, FragmentControllerActivity.class));
            }
        });

        binding.getAmulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMarker2 = mMap.addMarker(new MarkerOptions().position(endLatLng).title("Hospital Here").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
                binding.getAmulance.setVisibility(View.GONE);
                binding.getRoute.setVisibility(View.VISIBLE);
            }
        });
        binding.getRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.getAmulance.setVisibility(View.GONE);
                binding.getRoute.setVisibility(View.GONE);
                if (Util.Operations.isOnline(MapsActivity.this)) {
                    route();
//                    driverRequest();
                    String[] names = getResources().getStringArray(R.array.number);
                    Random rand = new Random();
                    int n = rand.nextInt(names.length - 1);
                    textView.setText(names[n]);
                } else {
                    Toast.makeText(getApplicationContext(), "No internet connectivity", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.cancleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.getReference().child("PatientRequests").child(mAuth.getUid()).removeValue();
                restartActivity();
            }
        });
    }

    public void restartActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void route() {

        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Fetching route information.", true);
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(startlatLng, endLatLng)
                .key(getString(R.string.google_maps_key))
                .build();
        routing.execute();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupToolbar() {
        setSupportActionBar(binding.toolbar1);
        binding.toolbar1.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    void setupDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar1, R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();

        mDrawerToggle.setDrawerArrowDrawable(new HamburgerDrawable(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mDrawerToggle.getDrawerArrowDrawable().setColor(getColor(R.color.white));
        } else {
            mDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location != null) {
            binding.getAmulance.setVisibility(View.VISIBLE);
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            try {
                Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = gc.getFromLocation(currentLatitude, currentLongitude, 1);
                String add = addresses.get(0).getAddressLine(0);
                SharedPrefManager.setStringValue(MapsActivity.this, Constants.Address
                        , add, Constants.SHARED_PREF_FILE_KEY);

                binding.patientAddressTv.setText(add);
                binding.currUserAdd.setText(add);

            } catch (IOException e) {
                e.printStackTrace();
            }

//            Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        } else {
            LocationstatusCheck();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        binding.getAmulance.setVisibility(View.VISIBLE);
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        try {
            Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = gc.getFromLocation(currentLatitude, currentLongitude, 1);
            String add = addresses.get(0).getAddressLine(0);
            SharedPrefManager.setStringValue(MapsActivity.this, Constants.Address
                    , add, Constants.SHARED_PREF_FILE_KEY);

            binding.patientAddressTv.setText(add);
            binding.currUserAdd.setText(add);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                checkLocationPermission();
            }
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {

                mLastLocation = location;

                startlatLng = new LatLng(location.getLatitude(), location.getLongitude());
//                mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_location_city_24)));
                mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Pickup Here").icon(BitmapDescriptorFactory.fromResource(R.drawable.patient)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(startlatLng));
//                mMap.animateCamera(CameraUpdateFactory.NzoomTo(15));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startlatLng, 13));
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);


            }
        }

    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Please give permission...")
                        .setMessage("Please give permission...")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please provide the permission...", Toast.LENGTH_LONG).show();
                }
                break;
            }


        }
    }


    @Override
    public void onRoutingFailure(RouteException e) {
        // The Routing request failed
        progressDialog.dismiss();
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        progressDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(startlatLng);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startlatLng, 16));

        mMap.moveCamera(center);
        binding.showDetailsCard.setVisibility(View.VISIBLE);
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

//            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " +
//                            route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(),
//                    Toast.LENGTH_SHORT).show();
            // Start marker
            MarkerOptions options = new MarkerOptions();
            options.position(startlatLng);
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.patient));
            mMap.addMarker(options);

            // End marker
            options = new MarkerOptions();
            options.position(endLatLng);
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance));
            mMap.addMarker(options);

        }
    }

    @Override
    public void onRoutingCancelled() {

        Log.i("LOG_TAG", "Routing was cancelled.");
    }


    private void driverRequest() {

//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
//        String formattedDate = mdformat.format(calendar.getTime());
//        DatabaseReference reference = mDatabase.getReference().child("PatientRequests").child(mAuth.getUid()).child("request");
//        RequestModel users = new RequestModel(name, email, formattedDate, String.valueOf(currentLatitude), String.valueOf(currentLongitude));
////        , String.valueOf(currentLatitude), String.valueOf(currentLongitude)
//        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Toast.makeText(getApplicationContext(), "Request Accepted", Toast.LENGTH_SHORT).show();
//
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }


    private void getData() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        name = map.get("name").toString();
                        binding.currUserName.setText(name);
                        binding.getAmulance.setVisibility(View.VISIBLE);
                    }
                    if (map.get("email") != null) {
                        email = map.get("email").toString();
                        binding.getAmulance.setVisibility(View.VISIBLE);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", databaseError.getMessage());
            }
        };
        mReference.addListenerForSingleValueEvent(valueEventListener);
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        System.exit(1);
    }

    public void LocationstatusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}