package mazzoubi.ldjobs.com.alpharide.ViewModel.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import mazzoubi.ldjobs.com.alpharide.Data.Users.UserInfo_sharedPreference;
import mazzoubi.ldjobs.com.alpharide.Data.Users.UserModel;
import mazzoubi.ldjobs.com.alpharide.R;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.Cars.MyCarsActivity;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.UserViewModel;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui.ProfileActivity;
import mazzoubi.ldjobs.com.alpharide.databinding.ActivityMapsBinding;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public DrawerLayout drawerLayout;
    NavigationView navigationView;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;

    int LOCATION_REFRESH_TIME = 15000; // 15 seconds to update
    int LOCATION_REFRESH_DISTANCE = 1; // 500 meters to update
    LocationManager mLocationManager;

    ConstraintLayout constraintLayout ;
    ToggleButton toggleButton ;
    TextView txvAccountState ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMapType(mMap.MAP_TYPE_NORMAL);


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        drawerLayout = findViewById(R.id.my_drawer_layout);
        navigationView = findViewById(R.id.navView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_account:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        break;

                    case R.id.nav_settings:
                        startActivity(new Intent(getApplicationContext(), MyCarsActivity.class));
                        break;

                    case R.id.nav_logout:
                        MapsActivity.this.finishAffinity();
                        break;
                }
                return false;
            }
        });

//        drawerLayout.openDrawer(GravityCompat.START);

        constraintLayout = findViewById(R.id.constraintLayout);
        toggleButton = findViewById(R.id.toggleButton);
        txvAccountState = findViewById(R.id.textView10);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    constraintLayout.setVisibility(View.VISIBLE);
                }else {
                    constraintLayout.setVisibility(View.GONE);
                }
            }
        });

        getUserInfo();
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
//                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            LatLng l =new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l,20.0f));
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title("Hello world"));

        }
    };


    public void men(View view) {

        drawerLayout.open();

    }

//    void getUserInfo(){
//        toggleButton.setClickable(false);
//        toggleButton.setTextOff("الرجاء الانتظار...");
//        toggleButton.setTextOn("الرجاء الانتظار...");
//        UserViewModel vm = ViewModelProviders.of(this).get(UserViewModel.class);
//        vm.getUserInfo(MapsActivity.this, UserInfo_sharedPreference.getUser(MapsActivity.this).uid);
//        vm.userObject.observe(this, new Observer<UserModel>() {
//            @Override
//            public void onChanged(UserModel userModel) {
//                UserInfo_sharedPreference.setInfo(MapsActivity.this,userModel);
//                if (!userModel.stateAccount.equals("StateAccount.active")){
//                    toggleButton.setClickable(false);
//                    toggleButton.setTextOff("يرجى انتظار قبول الادمن لطلبك");
//                    toggleButton.setTextOn("يرجى انتظار قبول الادمن لطلبك");
//                }else {
//                    toggleButton.setClickable(true);
//                    toggleButton.setTextOff("غير متصل");
//                    toggleButton.setTextOn("متصل");
//                }
//
//            }
//        });
//    }


    void getUserInfo(){
        txvAccountState.setText("الرجاء الانتظار...");
        txvAccountState.setText("الرجاء الانتظار...");
        UserViewModel vm = ViewModelProviders.of(this).get(UserViewModel.class);
        vm.getUserInfo(MapsActivity.this, UserInfo_sharedPreference.getUser(MapsActivity.this).uid);
        vm.userObject.observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {
                UserInfo_sharedPreference.setInfo(MapsActivity.this,userModel);
                if (!userModel.stateAccount.equals("StateAccount.active")){
                    toggleButton.setVisibility(View.INVISIBLE);
                    txvAccountState.setText("يرجى انتظار قبول الادمن لطلبك");
                    txvAccountState.setText("يرجى انتظار قبول الادمن لطلبك");
                }else {
                    toggleButton.setVisibility(View.VISIBLE);
                    txvAccountState.setVisibility(View.GONE);

                }

            }
        });
    }
}