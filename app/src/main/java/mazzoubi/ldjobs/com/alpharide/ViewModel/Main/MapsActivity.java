package mazzoubi.ldjobs.com.alpharide.ViewModel.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import mazzoubi.ldjobs.com.alpharide.Data.Users.UserInfo_sharedPreference;
import mazzoubi.ldjobs.com.alpharide.Data.Users.UserModel;
import mazzoubi.ldjobs.com.alpharide.MainActivity;
import mazzoubi.ldjobs.com.alpharide.R;
import mazzoubi.ldjobs.com.alpharide.RequestUserPermissions;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Notifications.ui.NotificationsActivity;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.Cars.MyCarsActivity;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.UserViewModel;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui.MyTripsActivity;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui.ProfileActivity;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Wallet.ui.WalletActivity;
import mazzoubi.ldjobs.com.alpharide.databinding.ActivityMapsBinding;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public DrawerLayout drawerLayout;
    NavigationView navigationView;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;

    int LOCATION_REFRESH_TIME = 0; // 15 seconds to update
    int LOCATION_REFRESH_DISTANCE = 0; // 500 meters to update
    LocationManager mLocationManager;

    ToggleButton toggleButton ;
    TextView txvAccountState ;

    boolean isConnected = false;
    public static boolean InTrip = false;
    Drawable circleDrawable ;

    EventListener<DocumentSnapshot> event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

        if(!UserInfo_sharedPreference.getUser(MapsActivity.this).uid.equals("")
        && UserInfo_sharedPreference.getUser(MapsActivity.this).uid != null){
            try{
                FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                String AID = Settings.Secure
                                        .getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                                if(!AID.equals(value.getString("AID"))
                                && !value.getString("AID").equals("")){
                                    Toast.makeText(MapsActivity.this, "تم تسجيل الدخول من جهاز اخر", Toast.LENGTH_SHORT).show();
                                    UserInfo_sharedPreference.logout(MapsActivity.this);
                                }
                            }
                        });
            }
            catch (Exception ex){}
        }

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(MapsActivity.this, new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    1); }

        event = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                Toast.makeText(MapsActivity.this, value.getString("idDriver"), Toast.LENGTH_SHORT).show();

            }
        };

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        updateToken();

        circleDrawable= getResources().getDrawable(R.drawable.cc);
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);

        Toast.makeText(getApplicationContext(), "الرجاء انتظار تحميل الخريطة....", Toast.LENGTH_LONG).show();
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
//            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_account_circle_24);
        mMap.clear();
        LatLng l =new LatLng(markerLat, markerLng);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l,15.0f));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(markerLat, markerLng))
                .icon(markerIcon));

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

//        drawerLayout.openDrawer(GravityCompat.START);

        toggleButton = findViewById(R.id.toggleButton);
        txvAccountState = findViewById(R.id.textView10);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isConnected = b;
                if (b){
                    circleDrawable = getResources().getDrawable(R.drawable.ccc);
                    toggleButton.setBackgroundDrawable(getDrawable(R.drawable.btn_back23));

                    FirebaseFirestore.getInstance()
                            .collection("driverRequests")
                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                            .addSnapshotListener(event);

                }
                else {
                    circleDrawable = getResources().getDrawable(R.drawable.cc);
                    toggleButton.setBackgroundDrawable(getDrawable(R.drawable.btn_back22));
                    FirebaseFirestore.getInstance()
                            .collection("locations")
                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                            .delete();

                    FirebaseFirestore.getInstance()
                            .collection("driverRequests")
                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                            .addSnapshotListener(event).remove();

                }




                    BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
//            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_account_circle_24);
                    mMap.clear();
                    LatLng l =new LatLng(markerLat, markerLng);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l,15.0f));
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(markerLat, markerLng))
                            .icon(markerIcon));

            }
        });

        setNavView();
        getUserInfo();
    }

    void setNavView(){
        drawerLayout = findViewById(R.id.my_drawer_layout);
        navigationView = findViewById(R.id.navView);

        TextView nav_myProfile , nav_wallet , nav_carMg , nav_myTrips
                , nav_notifications , nav_sittings , nav_contactUs , nav_logout ;
        ImageView nav_imageView;
//        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
//        View view= inflater.inflate(R.layout.menu_nav,navigationView);
        nav_myProfile = findViewById(R.id.textView3);
        nav_wallet = findViewById(R.id.textView9);
        nav_carMg = findViewById(R.id.textView20);
        nav_myTrips = findViewById(R.id.textView21);
        nav_notifications = findViewById(R.id.textView22);
        nav_sittings = findViewById(R.id.textView23);
        nav_contactUs = findViewById(R.id.textView24);
        nav_logout = findViewById(R.id.textView25);
        nav_imageView = findViewById(R.id.imageView);

        FirebaseFirestore.getInstance().collection("AdminDataConfig")
                .document("Data")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        String Event = value.getString("Event");
                        try {
                            Glide.with(getApplicationContext()).load(Event).into(nav_imageView);
                        }catch (Exception e){}
                    }
                });


        nav_myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });
        nav_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), WalletActivity.class));
            }
        });
        nav_carMg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyCarsActivity.class));
            }
        });
        nav_myTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyTripsActivity.class));
            }
        });
        nav_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
            }
        });
        nav_sittings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RequestUserPermissions.class));
            }
        });
        nav_contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ContactUsActivity.class));
            }
        });
        nav_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInfo_sharedPreference.logout(MapsActivity.this);
            }
        });


        FirebaseFirestore.getInstance().collection("Users")
                .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        nav_wallet.setText("المحفظة" + "   "+UserInfo_sharedPreference
                                .round(value.getDouble("balance"),2) + "   " + "دينار");
                        nav_wallet.setTextColor(Color.WHITE);
                        if(UserInfo_sharedPreference
                                .round(value.getDouble("balance"),2)>0){
                            nav_wallet.setTextColor(Color.GREEN);
                        }else if(UserInfo_sharedPreference
                                .round(value.getDouble("balance"),2)<0){
                            nav_wallet.setTextColor(Color.RED);
                        }

                    }
                });
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
    double markerLat =0 ,markerLng=0;
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            markerLat = location.getLatitude();
            markerLng = location.getLongitude();
            BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
//            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_account_circle_24);
            mMap.clear();
            LatLng l =new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l,15.0f));
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .icon(markerIcon));

            if(isConnected){
                Map<String, Object> map = new HashMap<>();
                Map<String, Object> mini_map = new HashMap<>();
                String geohash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(location.getLatitude(), location.getLongitude()));
                GeoPoint geopoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                mini_map.put("geohash", geohash);
                mini_map.put("geopoint", geopoint);

                if(!InTrip) map.put("available",true);
                else map.put("available",false);
                map.put("idUser", UserInfo_sharedPreference.getUser(MapsActivity.this).uid);
                map.put("position", mini_map);

                FirebaseFirestore.getInstance()
                        .collection("locations")
                        .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                        .set(map);
            }
            else{
                FirebaseFirestore.getInstance()
                        .collection("locations")
                        .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                        .delete(); }

        }
    };

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void men(View view) {

        drawerLayout.open();

    }

    void getUserInfo(){
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
                }else {
                    toggleButton.setVisibility(View.VISIBLE);
                    txvAccountState.setVisibility(View.GONE);

                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("النظام ...");
        builder.setMessage("هل تريد الخروج من التطبيق؟");
        builder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
            }
        });
        builder.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }


    void updateToken(){
        UserViewModel vm = ViewModelProviders.of(this).get(UserViewModel.class);
        vm.updateToken(MapsActivity.this);
    }
}