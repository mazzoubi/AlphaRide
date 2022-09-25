package com.nova.royalrideapp.ViewModel.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.collect.Maps;
import com.nova.royalrideapp.ConnectionChangeReceiver;
import com.nova.royalrideapp.Data.Users.MyTripsModel;
import com.nova.royalrideapp.FloatingService;
import com.nova.royalrideapp.MainActivity;
import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.nova.royalrideapp.ClassDate;
import com.nova.royalrideapp.Data.Users.UserInfo_sharedPreference;
import com.nova.royalrideapp.Data.Users.UserModel;
import com.nova.royalrideapp.R;
import com.nova.royalrideapp.ViewModel.SplashActivity;
import com.nova.royalrideapp.databinding.ActivityMapsBinding;

import com.nova.royalrideapp.RequestUserPermissions;
import com.nova.royalrideapp.ViewModel.Notifications.ui.NotificationsActivity;
import com.nova.royalrideapp.ViewModel.Users.Cars.MyCarsActivity;
import com.nova.royalrideapp.ViewModel.Users.UserViewModel;
import com.nova.royalrideapp.ViewModel.Users.ui.MyTripsActivity;
import com.nova.royalrideapp.ViewModel.Users.ui.ProfileActivity;
import com.nova.royalrideapp.ViewModel.Wallet.ui.WalletActivity;
import com.nova.royalrideapp.directionhelpers.FetchURL;
import com.nova.royalrideapp.directionhelpers.TaskLoadedCallback;
import com.siddharthks.bubbles.FloatingBubblePermissions;

import de.hdodenhof.circleimageview.CircleImageView;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {
    public DrawerLayout drawerLayout;
    NavigationView navigationView;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;

    int LOCATION_REFRESH_TIME = 5000; // 15 seconds to update
    int LOCATION_REFRESH_DISTANCE = 3; // 500 meters to update
    LocationManager mLocationManager;

    ToggleButton toggleButton;
    TextView txvAccountState, m, k;

    boolean isConnected = false;
    boolean DrawPoly = false;
    public static boolean InTrip = false;
    Drawable circleDrawable;
    Polyline polyline;

    EventListener<DocumentSnapshot> event, event2, event3, event4;
    Intent ser_int;
    Location loc;
    long StartedTripAt = 0;
    long StartedTripAt2 = 0;
    ArrayList<Location> TripDistanceCalc;

    int dialog_count = 0;
    CountDownTimer cd, cd2;
    Timer timer, timer2;
    int Tsec = 0, Tmin = 0, Thour = 0;
    DocumentSnapshot Snap_data;
    ProgressDialog progressDialogLoad;
    BigDecimal CustomerBalance;
    MediaPlayer mp;
    ProgressDialog progressDialog;
    String TripObjTid = "";
    boolean FirstOpen = true;
    Criteria locationCritera;
    String OldDistance = "0";
    String TripState = "";
    Intent BgServiceIntent;
    public static Activity main;
    double WallateBalance = 0;
    boolean location_buffer = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(binding.getRoot());

        if(isLocationEnabled(MapsActivity.this)){
            main = this;
            BgServiceIntent = new Intent(MapsActivity.this, FloatingService.class);
            FloatingBubblePermissions.startPermissionRequest(this);

            toggleButton = findViewById(R.id.toggleButton);

            locationCritera = new Criteria();
            locationCritera.setAccuracy(Criteria.ACCURACY_FINE);
            locationCritera.setAltitudeRequired(false);
            locationCritera.setCostAllowed(true);
            locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);

            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);

            CheckForAppVersion();
            updateToken();

            mp = new MediaPlayer();
            Uri mediaPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.carhorn);

            try {
                mp.setAudioStreamType(AudioManager.STREAM_RING);
                mp.setDataSource(MapsActivity.this, mediaPath);
                mp.setLooping(true);
                mp.prepare();
            } catch (Exception e) {}

            progressDialog = new ProgressDialog(MapsActivity.this);
            progressDialog.setTitle("النظام...");
            progressDialog.setMessage("الرجاء الإنتظار...");
//            progressDialog.setCancelable(false);
            try {
                if (!progressDialog.isShowing())
                    progressDialog.show();
            } catch (Exception ex) {
            }

            FirebaseFirestore.getInstance().collection("BlockUsers")
                    .whereEqualTo("idUser", UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    if (queryDocumentSnapshots.getDocuments().size() > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                        builder.setTitle("النظام...");
                        builder.setMessage("عزيزي المستخدم, لقد تم حظرك من إستخدام التطبيق يمكنك التواصل معنا");
                        builder.setPositiveButton("تواصل", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(getApplicationContext(), ContactUsActivity.class));
                            }
                        });
//                    builder.setNegativeButton("", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                        }
//                    });

                        builder.setCancelable(false);
                        builder.show();
                    }
                }
            });

            CustomerBalance = new BigDecimal("0");
            progressDialogLoad = new ProgressDialog(MapsActivity.this);
            progressDialogLoad.setCancelable(false);
            progressDialogLoad.setMessage("الرجاء الإنتظار...");

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }


            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            }

            final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MapsActivity.this);
            LayoutInflater inflater = MapsActivity.this.getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.dialog_trip_req, null));
            final androidx.appcompat.app.AlertDialog dialog = builder.create();
            ((FrameLayout) dialog.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = Math.round(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 500, getResources().getDisplayMetrics()));
            dialog.getWindow().setAttributes(lp);
            dialog.setCancelable(false);

            event = new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    String val = "";
                    try { val = value.getString("idCustomer"); } catch (Exception ex) {}

                    if (val != null && isConnected) {
                        Intent homeIntent = new Intent(MapsActivity.this, MapsActivity.class);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(homeIntent);
                        if (dialog_count == 0 && !InTrip) {
                            dialog_count = 1;
                            mp.start();
                            try {
                                dialog.show();
                            } catch (Exception ex) {
                            }
                            Snap_data = value;
                            final TextView t1 = dialog.findViewById(R.id.txvType);
                            final TextView t3 = dialog.findViewById(R.id.txvNo);
                            final TextView t4 = dialog.findViewById(R.id.txvColor);
                            final TextView t5 = dialog.findViewById(R.id.textView11);
                            final TextView t6 = dialog.findViewById(R.id.textView7);
                            final TextView t7 = dialog.findViewById(R.id.rarate);
                            final TextView t8 = dialog.findViewById(R.id.distatxt);
                            final ImageView close = dialog.findViewById(R.id.close);

                            cd2 = new CountDownTimer(17000, 1000) {
                                public void onTick(long millisUntilFinished) {

                                    t6.setText("الرفض (" + (millisUntilFinished / 1000) + ")");

                                }

                                public void onFinish() {
                                    isConnected = true;
                                    SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                    editor.putBoolean("isConnected", isConnected);
                                    editor.apply();
                                    editor.commit();

                                    dialog_count = 0;
                                    try {
                                        mp.pause();
                                        mp.seekTo(0);
                                    } catch (Exception ex) {
                                    }
                                    FirebaseFirestore.getInstance()
                                            .collection("driverRequests")
                                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                            .delete();
                                    dialog.dismiss();
                                }
                            };
                            cd2.start();

                            final double clng = Double.parseDouble(value.get("lng").toString());
                            final double clat = Double.parseDouble(value.get("lat").toString());

                            t4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

//                                Geocoder coder = new Geocoder(MapsActivity.this);
                                    double lng = Double.parseDouble(value.get("lng").toString());
                                    double lat = Double.parseDouble(value.get("lat").toString());
//                                try {
//                                    ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(value.getString("currentAddress"), 1);
//                                    for(Address add : adresses){
//                                        lng = add.getLongitude();
//                                        lat = add.getLatitude();
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }

                                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                            Uri.parse("http://maps.google.com/maps?saddr=" +
                                                    loc.getLatitude() + "," + loc.getLongitude() +
                                                    "&daddr=" + lat + "," + lng));
                                    startActivity(intent);
                                }
                            });

                            close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    dialog_count = 0;
                                    InTrip = false;
                                    isConnected = true;
                                    SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                    editor.putBoolean("isConnected", isConnected);
                                    editor.putBoolean("InTrip", InTrip);
                                    editor.apply();
                                    editor.commit();
                                    findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                    findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);

                                    FirebaseFirestore.getInstance()
                                            .collection("driverRequests")
                                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                            .delete();
                                    try {
                                        mp.pause();
                                        mp.seekTo(0);
                                    } catch (Exception ex) {
                                    }
                                    try {
                                        cd2.cancel();
                                    } catch (Exception ex) {
                                    }
                                }
                            });

                            t6.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    AlertDialog.Builder d = new AlertDialog.Builder(MapsActivity.this);
                                    d.setCancelable(false);
                                    d.setTitle("النظام...");
                                    d.setMessage("هل تريد تأكيد رفض الطلب؟");
                                    d.setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialog.dismiss();
                                            try {
                                                mp.pause();
                                                mp.seekTo(0);
                                            } catch (Exception ex) {
                                            }
                                            dialog_count = 0;
                                            InTrip = false;
                                            isConnected = true;
                                            SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                            editor.putBoolean("isConnected", isConnected);
                                            editor.putBoolean("InTrip", InTrip);
                                            editor.apply();
                                            editor.commit();
                                            findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                            findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);

                                            FirebaseFirestore.getInstance()
                                                    .collection("driverRequests")
                                                    .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                    .delete();
                                            try {
                                                cd2.cancel();
                                            } catch (Exception ex) {
                                            }
                                        }
                                    });
                                    d.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                                    d.show();
                                }
                            });

                            t5.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    InTrip = true;
                                    SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                    editor.putBoolean("InTrip", InTrip);
                                    editor.apply();
                                    editor.commit();

                                    try {
                                        mp.pause();
                                        mp.seekTo(0);
                                    } catch (Exception ex) {
                                    }
                                    progressDialogLoad.show();
                                    findViewById(R.id.textView7).setVisibility(View.VISIBLE);
                                    k.setText("0.000 Km");
                                    m.setText("00:00:00");
                                    TripState = "tracking";

                                    final Map<String, Object> map = new HashMap<>();
                                    final Map<String, Object> mini_map = new HashMap<>();
                                    final Map<String, Object> mini_map2 = new HashMap<>();
                                    final Map<String, Object> mini_map3 = new HashMap<>();

                                    map.put("tripsid", System.currentTimeMillis());
                                    TripObjTid = map.get("tripsid").toString();
                                    map.put("date", ClassDate.date());
                                    map.put("nameCustomer", value.getString("nameCustomer"));
                                    map.put("phoneCustomer", value.getString("phoneCustomer"));
                                    map.put("idCustomer", value.getString("idCustomer"));
                                    map.put("idDriver", UserInfo_sharedPreference.getUser(MapsActivity.this).uid);
                                    map.put("dateStart", FieldValue.serverTimestamp());
                                    map.put("dateAcceptRequest", FieldValue.serverTimestamp());
                                    map.put("state", "StateTrip.active");
                                    map.put("km", 0.0);
                                    map.put("totalPrice", 0.0);
                                    map.put("hours", value.get("hours"));
                                    map.put("typeTrip", value.getString("typeTrip"));
                                    map.put("discount", value.get("discount"));
                                    map.put("addressCurrent", value.getString("currentAddress"));

                                    mini_map.put("lat", ((Map<String, Object>) value.get("accessPoint")).get("lat"));
                                    mini_map.put("lng", ((Map<String, Object>) value.get("accessPoint")).get("lng"));
                                    mini_map.put("addressTo", ((Map<String, Object>) value.get("accessPoint")).get("addressTo") + "");
                                    map.put("accessPoint", mini_map);

                                    Geocoder coder = new Geocoder(MapsActivity.this);
                                    double lng = 0;
                                    double lat = 0;
                                    try {
                                        ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(value.getString("currentAddress"), 1);
                                        for (Address add : adresses) {
                                            lng = add.getLongitude();
                                            lat = add.getLatitude();
                                        }
                                    } catch (Exception e) {
                                    }

                                    mini_map2.put("lat", lng);
                                    mini_map2.put("lng", lat);
                                    map.put("locationCustomer", mini_map2);

                                    mini_map3.put("lat", loc.getLatitude());
                                    mini_map3.put("lng", loc.getLongitude());
                                    mini_map3.put("rotateDriver", 0);
                                    map.put("locationDriver", mini_map3);

                                    FirebaseFirestore.getInstance()
                                            .collection("Trips")
                                            .document(map.get("tripsid").toString())
                                            .set(map)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    FirebaseFirestore.getInstance()
                                                            .collection("Trips")
                                                            .document(map.get("tripsid") + "")
                                                            .addSnapshotListener(event2);

                                                    FirebaseFirestore.getInstance()
                                                            .collection("driverRequests")
                                                            .whereEqualTo("idCustomer", map.get("idCustomer").toString())
                                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                            for (int i = 0; i < list.size(); i++) {
                                                                FirebaseFirestore.getInstance()
                                                                        .collection("driverRequests")
                                                                        .document(list.get(i).getId())
                                                                        .delete();
                                                            }
                                                        }
                                                    });

                                                    FirebaseFirestore.getInstance()
                                                            .collection("location")
                                                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                            .update("available", false);

                                                    FirebaseFirestore.getInstance()
                                                            .collection("Users")
                                                            .document(map.get("idCustomer").toString())
                                                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            SendNoti("الكابتن في الطريق اليك", documentSnapshot.getString("token"));
                                                            progressDialogLoad.dismiss();
                                                        }
                                                    });

                                                    DrawPoly = true;
                                                    DrawPolyLine();

                                                    findViewById(R.id.card_default).setVisibility(View.INVISIBLE);
                                                    findViewById(R.id.card_default2).setVisibility(View.VISIBLE);

                                                    ImageView call = findViewById(R.id.call);
                                                    CardView loca = findViewById(R.id.card3);
                                                    TextView arrive = findViewById(R.id.textView11);
                                                    TextView canc = findViewById(R.id.textView7);
                                                    TextView name = findViewById(R.id.txvType);
                                                    TextView locat = findViewById(R.id.txvColor);

                                                    name.setText(value.getString("nameCustomer"));
                                                    locat.setText(value.getString("currentAddress"));

                                                    call.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent intent = new Intent(Intent.ACTION_DIAL);
                                                            intent.setData(Uri.parse("tel:" + value.getString("phoneCustomer").replace("+962", "0")));
                                                            startActivity(intent);
                                                        }
                                                    });

                                                    loca.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {

                                                            Geocoder coder = new Geocoder(MapsActivity.this);
                                                            double lng = 0;
                                                            double lat = 0;
                                                            try {
                                                                ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(value.getString("currentAddress"), 1);
                                                                for (Address add : adresses) {
                                                                    lng = add.getLongitude();
                                                                    lat = add.getLatitude();
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                                    Uri.parse("http://maps.google.com/maps?saddr=" +
                                                                            loc.getLatitude() + "," + loc.getLongitude() +
                                                                            "&daddr=" + lat + "," + lng));
                                                            startActivity(intent);
                                                        }
                                                    });

                                                    arrive.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {

                                                            if (arrive.getText().toString().equals("لقد وصلت")) {
                                                               // if (GetDistanceFromLatLonInKm(loc.getLatitude(), loc.getLongitude(), clat, clng) <= 0.10) {
                                                                if (true) {
                                                                    progressDialogLoad.show();
                                                                    TripState = "";

                                                                    FirebaseFirestore.getInstance()
                                                                            .collection("Users")
                                                                            .document(map.get("idCustomer").toString())
                                                                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                            arrive.setText("بدء الرحلة");
                                                                            SendNoti("لقد وصل الكابتن للموقع.", documentSnapshot.getString("token"));
                                                                            progressDialogLoad.dismiss();
                                                                            cd = new CountDownTimer(300000, 1000) {

                                                                                public void onTick(long millisUntilFinished) {

                                                                                    if (millisUntilFinished >= 240000) {
                                                                                        if (millisUntilFinished - 240000 < 10000)
                                                                                            canc.setText("04:0" + ((millisUntilFinished - 240000) / 1000));
                                                                                        else
                                                                                            canc.setText("04:" + ((millisUntilFinished - 240000) / 1000));
                                                                                    } else if (millisUntilFinished >= 180000) {
                                                                                        if (millisUntilFinished - 180000 < 10000)
                                                                                            canc.setText("03:0" + ((millisUntilFinished - 180000) / 1000));
                                                                                        else
                                                                                            canc.setText("03:" + ((millisUntilFinished - 180000) / 1000));
                                                                                    } else if (millisUntilFinished >= 120000) {
                                                                                        if (millisUntilFinished - 120000 < 10000)
                                                                                            canc.setText("02:0" + ((millisUntilFinished - 120000) / 1000));
                                                                                        else
                                                                                            canc.setText("02:" + ((millisUntilFinished - 180000) / 1000));
                                                                                    } else if (millisUntilFinished >= 60000) {
                                                                                        if (millisUntilFinished - 60000 < 10000)
                                                                                            canc.setText("01:0" + ((millisUntilFinished - 60000) / 1000));
                                                                                        else
                                                                                            canc.setText("01:" + ((millisUntilFinished - 180000) / 1000));
                                                                                    } else if (millisUntilFinished >= 0) {
                                                                                        if (millisUntilFinished < 10000)
                                                                                            canc.setText("01:0" + (millisUntilFinished / 1000));
                                                                                        else
                                                                                            canc.setText("01:" + (millisUntilFinished / 1000));
                                                                                    }

                                                                                }

                                                                                public void onFinish() {
                                                                                    canc.setText("إلغاء");
                                                                                }

                                                                            };
                                                                            cd.start();
                                                                        }
                                                                    });
                                                                } else
                                                                    Toast.makeText(MapsActivity.this, "لا يمكن الوصول على بعد أكبر من 100 مترا", Toast.LENGTH_LONG).show();
                                                            } else if (arrive.getText().toString().equals("بدء الرحلة")) {
                                                                FirebaseFirestore.getInstance()
                                                                        .collection("Users")
                                                                        .document(map.get("idCustomer").toString())
                                                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                        SendNoti("لقد بدأت الرحلة.", documentSnapshot.getString("token"));
                                                                    }
                                                                });

                                                                DrawPoly = false;
//                                                    polyline.remove();
                                                                progressDialogLoad.show();
                                                                FirebaseFirestore.getInstance()
                                                                        .collection("Trips")
                                                                        .document(map.get("tripsid").toString())
                                                                        .update("state", "StateTrip.started")
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                progressDialogLoad.dismiss();

                                                                                try {
                                                                                    timer = new Timer();
                                                                                    timer.schedule(new TimerTask() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            runOnUiThread(new Runnable() {
                                                                                                @Override
                                                                                                public void run() {
                                                                                                    Tsec += 1;
                                                                                                    String hour_msg = "0" + Thour + ":";
                                                                                                    String hour_msg2 = Thour + ":";
                                                                                                    String min_msg = "0" + Tmin + ":";
                                                                                                    String min_msg2 = Tmin + ":";
                                                                                                    String sec_msg = "0" + Tsec;
                                                                                                    String sec_msg2 = "" + Tsec;

                                                                                                    if (Tsec == 59) {
                                                                                                        Tsec = 0;
                                                                                                        Tmin += 1;
                                                                                                    }
                                                                                                    if (Tmin == 59) {
                                                                                                        Tmin = 0;
                                                                                                        Thour += 1;
                                                                                                    }

                                                                                                    String final_msg = "";
                                                                                                    if (Thour >= 10)
                                                                                                        final_msg += hour_msg2;
                                                                                                    else
                                                                                                        final_msg += hour_msg;
                                                                                                    if (Tmin >= 10)
                                                                                                        final_msg += min_msg2;
                                                                                                    else
                                                                                                        final_msg += min_msg;
                                                                                                    if (Tsec >= 10)
                                                                                                        final_msg += sec_msg2;
                                                                                                    else
                                                                                                        final_msg += sec_msg;

                                                                                                    m.setText(final_msg);
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    }, 1000, 1000);
                                                                                } catch (Exception ex) {
                                                                                }

                                                                                //here

                                                                                arrive.setText("إنهاء الرحلة");
                                                                                StartedTripAt = System.currentTimeMillis();
                                                                                TripDistanceCalc = new ArrayList<>();
                                                                                k.setText("0.0 Km");
                                                                                canc.setText("إلغاء");
                                                                                canc.setVisibility(View.INVISIBLE);
                                                                                cd.cancel();
                                                                            }
                                                                        });
                                                            } else if (arrive.getText().toString().equals("إنهاء الرحلة")) {
                                                                AlertDialog.Builder d = new AlertDialog.Builder(MapsActivity.this);
                                                                d.setMessage("هل تريد تأكيد انهاء الرحلة؟");
                                                                d.setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        {
                                                                            progressDialog.show();

                                                                            FirebaseFirestore.getInstance()
                                                                                    .collection("Trips")
                                                                                    .document(map.get("tripsid") + "")
                                                                                    .addSnapshotListener(event2).remove();

                                                                            FirebaseFirestore.getInstance()
                                                                                    .collection("Users")
                                                                                    .document(Snap_data.getString("idCustomer"))
                                                                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                    CustomerBalance = new BigDecimal(documentSnapshot.get("balance").toString());
                                                                                }
                                                                            });

                                                                            try {
                                                                                timer.cancel();
                                                                                timer.purge();
                                                                            } catch (Exception ex) {
                                                                            }
                                                                            Tsec = 0;
                                                                            Tmin = 0;
                                                                            Thour = 0;
                                                                            m.setText("00:00:00");

                                                                            FirebaseFirestore.getInstance()
                                                                                    .collection("Trips")
                                                                                    .document(map.get("tripsid").toString())
                                                                                    .update("state", "StateTrip.needRatingByDriver")
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                            FirebaseFirestore.getInstance()
                                                                                                    .collection("AdminDataConfig")
                                                                                                    .document("Data")
                                                                                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                                @Override
                                                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                                                                    final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MapsActivity.this);
                                                                                                    LayoutInflater inflater = MapsActivity.this.getLayoutInflater();
                                                                                                    builder.setView(inflater.inflate(R.layout.dialog_trip_req2, null));
                                                                                                    final androidx.appcompat.app.AlertDialog dialog2 = builder.create();
                                                                                                    ((FrameLayout) dialog2.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                                                                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                                                                    lp.copyFrom(dialog2.getWindow().getAttributes());
                                                                                                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                                                                                    lp.height = Math.round(TypedValue.applyDimension(
                                                                                                            TypedValue.COMPLEX_UNIT_DIP, 510, getResources().getDisplayMetrics()));
                                                                                                    dialog2.getWindow().setAttributes(lp);
                                                                                                    dialog2.show();
                                                                                                    dialog2.setCancelable(false);

                                                                                                    final TextView t1 = dialog2.findViewById(R.id.txvType);
                                                                                                    final TextView t2 = dialog2.findViewById(R.id.txvColor);
                                                                                                    final TextView t3 = dialog2.findViewById(R.id.txvNo);
                                                                                                    final TextView t4 = dialog2.findViewById(R.id.textView11);
                                                                                                    final TextView t5 = dialog2.findViewById(R.id.txvNo5);

                                                                                                    BigDecimal TripDistance = new BigDecimal(OldDistance);
                                                                                                    for (int i = 0; i < TripDistanceCalc.size() - 1; i++) {

                                                                                                        TripDistance = TripDistance.add(new BigDecimal(GetDistanceFromLatLonInKm(
                                                                                                                TripDistanceCalc.get(i).getLatitude(), TripDistanceCalc.get(i).getLongitude(),
                                                                                                                TripDistanceCalc.get(i + 1).getLatitude(), TripDistanceCalc.get(i + 1).getLongitude())));

                                                                                                    }

                                                                                                    final BigDecimal FinalTripDistance = TripDistance;

                                                                                                    double base_price = 0, below_4_km = 0, between_4n5_km = 0, between_5n8_km = 0,
                                                                                                            more_8_km = 0, minute_price = 0, minimum_trip_cost = 0, driver_fee = 0;

                                                                                                    try {
                                                                                                        base_price = Double.parseDouble(documentSnapshot.getString("base_price"));
                                                                                                        below_4_km = Double.parseDouble(documentSnapshot.getString("below_4_km"));
                                                                                                        between_4n5_km = Double.parseDouble(documentSnapshot.getString("between_4n5_km"));
                                                                                                        between_5n8_km = Double.parseDouble(documentSnapshot.getString("between_5n8_km"));
                                                                                                        more_8_km = Double.parseDouble(documentSnapshot.getString("more_8_km"));
                                                                                                        minute_price = Double.parseDouble(documentSnapshot.getString("minute_price"));
                                                                                                        minimum_trip_cost = Double.parseDouble(documentSnapshot.getString("minimum_trip_cost"));
                                                                                                        driver_fee = Double.parseDouble(documentSnapshot.getString("driver_fee"));
                                                                                                    } catch (Exception ex) {
                                                                                                    }

                                                                                                    long time = ((System.currentTimeMillis() / 1000) - (StartedTripAt / 1000)) / 60;
                                                                                                    StartedTripAt = 0;
                                                                                                    BigDecimal TotalTripPrice = new BigDecimal("-1");

                                                                                                    if (TripDistance.doubleValue() <= 4)
                                                                                                        TotalTripPrice = new BigDecimal(base_price)
                                                                                                                .add(new BigDecimal(below_4_km).multiply(TripDistance))
                                                                                                                .add(new BigDecimal(minute_price).multiply(new BigDecimal(time)));
                                                                                                    else if (TripDistance.doubleValue() > 4 && TripDistance.doubleValue() <= 5)
                                                                                                        TotalTripPrice = new BigDecimal(base_price)
                                                                                                                .add(new BigDecimal(between_4n5_km).multiply(TripDistance))
                                                                                                                .add(new BigDecimal(minute_price).multiply(new BigDecimal(time)));
                                                                                                    else if (TripDistance.doubleValue() > 5 && TripDistance.doubleValue() <= 8)
                                                                                                        TotalTripPrice = new BigDecimal(base_price)
                                                                                                                .add(new BigDecimal(between_5n8_km).multiply(TripDistance))
                                                                                                                .add(new BigDecimal(minute_price).multiply(new BigDecimal(time)));
                                                                                                    else if (TripDistance.doubleValue() > 8)
                                                                                                        TotalTripPrice = new BigDecimal(base_price)
                                                                                                                .add(new BigDecimal(more_8_km).multiply(TripDistance))
                                                                                                                .add(new BigDecimal(minute_price).multiply(new BigDecimal(time)));

                                                                                                    if (TotalTripPrice.doubleValue() < minimum_trip_cost)
                                                                                                        TotalTripPrice = new BigDecimal(minimum_trip_cost);

                                                                                                    final BigDecimal PriceWithoutDisc = TotalTripPrice;
                                                                                                    final BigDecimal disc = new BigDecimal(Snap_data.get("discount").toString()).divide(new BigDecimal("100"));
                                                                                                    final BigDecimal disc2 = new BigDecimal(driver_fee).divide(new BigDecimal("100"));
                                                                                                    final BigDecimal CompanyShare = TotalTripPrice.multiply(disc2);
                                                                                                    final BigDecimal DiscountablePrice = TotalTripPrice.subtract(new BigDecimal(minimum_trip_cost));
                                                                                                    final BigDecimal CustomerPrice = DiscountablePrice.subtract(DiscountablePrice.multiply(disc));
                                                                                                    final BigDecimal AddBalancePrice = CompanyShare.subtract(DiscountablePrice.multiply(disc));
                                                                                                    final BigDecimal AddSubBalance = new BigDecimal("0").subtract(AddBalancePrice);
                                                                                                    final BigDecimal FinalCustomerPrice = CustomerPrice.add(new BigDecimal(minimum_trip_cost));

                                                                                                    FirebaseFirestore.getInstance()
                                                                                                            .collection("Users")
                                                                                                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                                                            .update("balance", FieldValue.increment(AddSubBalance.doubleValue()))
                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task) {
//                                                                                                                Toast.makeText(MapsActivity.this, "تم إضافة مبلغ " + String.format(Locale.ENGLISH, "%.2f", AddSubBalance.doubleValue()), Toast.LENGTH_SHORT).show();

                                                                                                                    final double km = Double.parseDouble(String.format(Locale.ENGLISH, "%.3f", FinalTripDistance.doubleValue()));
                                                                                                                    final double totalPrice = Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", PriceWithoutDisc.doubleValue()));

                                                                                                                    Map<String, Object> ups = new HashMap<>();
                                                                                                                    Map<String, Object> mini_locs = new HashMap<>();
                                                                                                                    mini_locs.put("addressTo", "");
                                                                                                                    mini_locs.put("lat", loc.getLatitude());
                                                                                                                    mini_locs.put("lng", loc.getLongitude());
                                                                                                                    ups.put("accessPoint", mini_locs);
                                                                                                                    ups.put("km", km);
                                                                                                                    ups.put("hours", time);
                                                                                                                    ups.put("totalPrice", totalPrice);
                                                                                                                    FirebaseFirestore.getInstance()
                                                                                                                            .collection("Trips")
                                                                                                                            .document(map.get("tripsid").toString())
                                                                                                                            .update(ups).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                                                            BigDecimal Balance = new BigDecimal("0");
                                                                                                                            BigDecimal Change = new BigDecimal("0");
                                                                                                                            BigDecimal Recieve = new BigDecimal("0");

                                                                                                                            if (CustomerBalance.subtract(FinalCustomerPrice).doubleValue() >= 0) {
                                                                                                                                Balance = CustomerBalance.subtract(FinalCustomerPrice);
                                                                                                                                Change = new BigDecimal("0");
                                                                                                                                Recieve = FinalCustomerPrice;
                                                                                                                            } else {
                                                                                                                                Balance = new BigDecimal("0");
                                                                                                                                Change = FinalCustomerPrice.subtract(CustomerBalance);
                                                                                                                                Recieve = CustomerBalance;
                                                                                                                            }

                                                                                                                            t1.setText(Snap_data.getString("nameCustomer"));
                                                                                                                            t2.setText("المسافة المقطوعة: " + String.format("%.2f", FinalTripDistance.doubleValue()) + " Km \n" + "وقت الرحلة: " + time + " min");
                                                                                                                            t3.setText("عداد الرحلة: " + String.format("%.2f", PriceWithoutDisc) + " دينار بخصم " + Snap_data.get("discount") + "%");
                                                                                                                            t5.setText("القيمة المطلوبة كاش: " + String.format("%.2f", Change.doubleValue()) + " دينار");

                                                                                                                            CustomerBalance = Balance;
                                                                                                                            final BigDecimal AddBalance = Recieve;
                                                                                                                            progressDialog.dismiss();

                                                                                                                            t4.setOnClickListener(new View.OnClickListener() {
                                                                                                                                @Override
                                                                                                                                public void onClick(View view) {
                                                                                                                                    progressDialog.show();

                                                                                                                                    Map<String, Object> dada = new HashMap<>();
                                                                                                                                    dada.put("balance", Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", CustomerBalance)));
                                                                                                                                    FirebaseFirestore.getInstance()
                                                                                                                                            .collection("Users")
                                                                                                                                            .document(Snap_data.getString("idCustomer"))
                                                                                                                                            .update(dada).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                            RatingBar rate = dialog2.findViewById(R.id.rating);
                                                                                                                                            double rating = rate.getRating();

                                                                                                                                            Map<String, Object> ra = new HashMap<>();
                                                                                                                                            ra.put("rating", FieldValue.increment(rating));
                                                                                                                                            ra.put("countRating", FieldValue.increment(1));
                                                                                                                                            FirebaseFirestore.getInstance()
                                                                                                                                                    .collection("Users")
                                                                                                                                                    .document(Snap_data.getString("idCustomer"))
                                                                                                                                                    .update(ra).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                @Override
                                                                                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                                    FirebaseFirestore.getInstance()
                                                                                                                                                            .collection("Users")
                                                                                                                                                            .document(Snap_data.getString("idCustomer"))
                                                                                                                                                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                                                                                            SendNoti("عميلنا العزيز شكرا لاستخدامكم تطبيق رويال رايد, نتمنى لك يوما سعيدا", documentSnapshot.getString("token"));
                                                                                                                                                            toggleButton.setChecked(true);
                                                                                                                                                            OldDistance = "0";
                                                                                                                                                        }
                                                                                                                                                    });

                                                                                                                                                    FirebaseFirestore.getInstance()
                                                                                                                                                            .collection("Users")
                                                                                                                                                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                                                                                                            .update("balance", FieldValue.increment(AddBalance.doubleValue()));

                                                                                                                                                    FirebaseFirestore.getInstance()
                                                                                                                                                            .collection("Trips")
                                                                                                                                                            .document(map.get("tripsid").toString())
                                                                                                                                                            .update("state", "StateTrip.needRatingByCustomer")
                                                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                @Override
                                                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                    progressDialog.dismiss();
                                                                                                                                                                    arrive.setText("لقد وصلت");
                                                                                                                                                                    canc.setText("إلغاء");
                                                                                                                                                                    dialog_count = 0;
                                                                                                                                                                    InTrip = false;
                                                                                                                                                                    isConnected = true;
                                                                                                                                                                    SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                                                                                                                                                    editor.putBoolean("isConnected", isConnected);
                                                                                                                                                                    editor.putBoolean("InTrip", InTrip);
                                                                                                                                                                    editor.apply();
                                                                                                                                                                    editor.commit();
                                                                                                                                                                    findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                                                                                                                                    findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);
                                                                                                                                                                    dialog2.dismiss();
                                                                                                                                                                }
                                                                                                                                                            });
                                                                                                                                                }
                                                                                                                                            });
                                                                                                                                        }
                                                                                                                                    });

                                                                                                                                }
                                                                                                                            });

                                                                                                                        }
                                                                                                                    });
                                                                                                                }
                                                                                                            });
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                });
                                                                d.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                                    }
                                                                });
                                                                d.setCancelable(false);
                                                                d.show();
                                                            }
                                                        }
                                                    });

                                                    canc.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {

                                                            if (!canc.getText().toString().equals("إلغاء")) {
                                                                new AlertDialog.Builder(MapsActivity.this)
                                                                        .setMessage("سيتم خصم مبلغ بقيمة 0.5 دينار عند قيامك بإلغاء رحلة مقبولة هل ترغب بذلك ؟")
                                                                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                                                FirebaseFirestore.getInstance()
                                                                                        .collection("Trips")
                                                                                        .document(map.get("tripsid") + "")
                                                                                        .addSnapshotListener(event2).remove();

                                                                                findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                                                findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);
                                                                                progressDialogLoad.show();
                                                                                dialog_count = 0;
                                                                                InTrip = false;
                                                                                isConnected = true;
                                                                                SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                                                                editor.putBoolean("isConnected", isConnected);
                                                                                editor.putBoolean("InTrip", InTrip);
                                                                                editor.apply();
                                                                                editor.commit();
                                                                                findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                                                findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);

                                                                                FirebaseFirestore.getInstance()
                                                                                        .collection("driverRequests")
                                                                                        .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                                        .delete();

                                                                                FirebaseFirestore.getInstance()
                                                                                        .collection("Trips")
                                                                                        .document(map.get("tripsid").toString())
                                                                                        .update("state", "StateTrip.cancelByDriver");

                                                                                BigDecimal bal = new BigDecimal(UserInfo_sharedPreference.getUser(MapsActivity.this).balance)
                                                                                        .subtract(new BigDecimal("0.5"));
                                                                                FirebaseFirestore.getInstance()
                                                                                        .collection("Users")
                                                                                        .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                                        .update("balance", bal.doubleValue());
                                                                                progressDialogLoad.dismiss();
                                                                            }
                                                                        }).setNegativeButton("لا", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        dialog.dismiss();
                                                                    }
                                                                }).setCancelable(false).create().show();
                                                            } else {

                                                                AlertDialog.Builder d = new AlertDialog.Builder(MapsActivity.this);
                                                                d.setCancelable(false);
                                                                d.setTitle("النظام...");
                                                                d.setMessage("هل تريد تأكيد الإلغاء؟");
                                                                d.setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                                        FirebaseFirestore.getInstance()
                                                                                .collection("Trips")
                                                                                .document(map.get("tripsid") + "")
                                                                                .addSnapshotListener(event2).remove();

                                                                        findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                                        findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);

                                                                        dialog_count = 0;
                                                                        InTrip = false;
                                                                        isConnected = true;
                                                                        SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                                                        editor.putBoolean("isConnected", isConnected);
                                                                        editor.putBoolean("InTrip", InTrip);
                                                                        editor.apply();
                                                                        editor.commit();
                                                                        findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                                        findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);

                                                                        progressDialogLoad.show();

                                                                        FirebaseFirestore.getInstance()
                                                                                .collection("driverRequests")
                                                                                .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                progressDialogLoad.dismiss();
                                                                            }
                                                                        });

                                                                        FirebaseFirestore.getInstance()
                                                                                .collection("Trips")
                                                                                .document(map.get("tripsid").toString())
                                                                                .update("state", "StateTrip.rejected");
                                                                    }
                                                                });
                                                                d.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        dialogInterface.dismiss();
                                                                    }
                                                                });
                                                                d.show();

                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                }
                            });

                            try {
                                t1.setText("الراكب : " + value.getString("nameCustomer"));
                                t3.setText("خصم الرحلة : " + value.get("discount").toString() + "%");
//                            t4.setText("مرجع الخريطة : "+value.getString("currentAddress"));

                                FirebaseFirestore.getInstance()
                                        .collection("Users")
                                        .document(value.getString("idCustomer"))
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        double flng = Double.parseDouble(value.get("lng").toString());
                                        double flat = Double.parseDouble(value.get("lat").toString());

                                        try {
                                            double rate1 = Double.parseDouble(documentSnapshot.get("rating").toString());
                                            double rate2 = Double.parseDouble(documentSnapshot.get("countRating").toString());
                                            double fdista = GetDistanceFromLatLonInKm(loc.getLatitude(), loc.getLongitude(), flat, flng);
                                            t7.setText("تقييم الراكب: " + String.format("%.2f", (rate1 / rate2)));
                                            t8.setText("يبعد الراكب عنك: " + String.format("%.3f", fdista) + " Km");
                                        } catch (Exception ex) {
                                            Toast.makeText(MapsActivity.this, "", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } catch (Exception ex) {
                            }

                        }
                    }
                    else {
//                        FirebaseFirestore.getInstance()
//                                .collection("driverRequests")
//                                .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
//                                .delete();
                        if (dialog_count == 1) {
                            isConnected = true;
                            SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                            editor.putBoolean("isConnected", isConnected);
                            editor.apply();
                            editor.commit();
                            dialog_count = 0;
                            try {
                                mp.pause();
                                mp.seekTo(0);
                            } catch (Exception ex) {
                            }
                            try {
                                cd.cancel();
                            } catch (Exception ex) {
                            }
                            try {
                                cd2.cancel();
                            } catch (Exception ex) {
                            }
                            dialog.dismiss();
                        }
                    }
                }
            };

            event2 = new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    String tsta = "";
                    try { tsta = value.getString("state"); }
                    catch (Exception ex) {}
                    if (tsta != null && tsta.contains("StateTrip.cancel")) {
                        InTrip = false;
                        isConnected = true;
                        SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                        editor.putBoolean("isConnected", isConnected);
                        editor.putBoolean("InTrip", InTrip);
                        editor.apply();
                        editor.commit();
                        findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                        findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);
                    }
                }
            };

            event3 = new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    String AID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    if (!AID.equals(value.getString("AID")) && !value.getString("AID").equals("")) {

                        try {
                            FirebaseFirestore.getInstance()
                                    .collection("Users")
                                    .document(getSharedPreferences("User", Context.MODE_PRIVATE).getString("uid", ""))
                                    .update("token", "");
                        } catch (Exception ex) {
                        }

                        try {
                            FirebaseFirestore.getInstance()
                                    .collection("locations")
                                    .document(getSharedPreferences("User", Context.MODE_PRIVATE).getString("uid", ""))
                                    .delete();
                        } catch (Exception ex) {
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FirebaseFirestore.getInstance()
                                            .collection("Users")
                                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                            .addSnapshotListener(event3).remove();

                                    try {
                                        Toast.makeText(MapsActivity.this, "تم تسجيل الدخول من جهاز جديد..", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MapsActivity.this, MainActivity.class).putExtra("exit", "1"));
                                        finish();
                                    } catch (Exception ex) {
                                    }

                                } catch (Exception ex) {
                                }
                            }
                        }, 2000);
                    }
                }
            };

            if (!UserInfo_sharedPreference.getUser(MapsActivity.this).uid.equals("")
                    && UserInfo_sharedPreference.getUser(MapsActivity.this).uid != null) {
                try {
                    FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                            .addSnapshotListener(event3);
                } catch (Exception ex) {
                }
            }

            if (UserInfo_sharedPreference.getUser(MapsActivity.this).uid != null && !UserInfo_sharedPreference.getUser(MapsActivity.this).uid.equals("")) {
                FirebaseFirestore.getInstance()
                        .collection("locations")
                        .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String iduser = "";
                        try {
                            iduser = documentSnapshot.getString("idUser");
                        } catch (Exception ex) {
                        }

                        if (iduser != null && !iduser.equals("")) {
                            toggleButton.setChecked(true);
                            InTrip = false;
                            isConnected = true;
                            SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                            editor.putBoolean("isConnected", isConnected);
                            editor.putBoolean("InTrip", InTrip);
                            editor.apply();
                            editor.commit();
                        }

                    }
                });
            }

            event4 = new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    try{ WallateBalance = Double.parseDouble(value.get("balance").toString()); }
                    catch (Exception ex){ WallateBalance = 0;}
                }
            };

            try{
                if(UserInfo_sharedPreference.getUser(MapsActivity.this).uid != null && !UserInfo_sharedPreference.getUser(MapsActivity.this).uid.equals("")){
                    FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                            .addSnapshotListener(event4);
                }
            }
            catch (Exception ex){}

            String VID = "0";
            try{VID = getPackageManager().getPackageInfo(getPackageName(), 0).versionName; }
            catch (Exception ex){ VID = "-1"; }

            if(VID == null)
                VID = "-2";
            else
            if(VID.equals(""))
                VID = "-3";

            UpdateVID(MapsActivity.this, VID);

        }
        else
            new AlertDialog.Builder(MapsActivity.this)
                .setMessage("يرجى تفعيل الموقع GPS و المحاولة مجددا..")
                .setPositiveButton("حسنا", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MapsActivity.this.finishAffinity();
                    }
                }).setCancelable(false).create().show();

        startService(new Intent(MapsActivity.this, ConnectionChangeReceiver.class));

    }

    private boolean CheckBalance() {
        if (WallateBalance <= 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            builder.setTitle("رويال رايد");
            builder.setMessage("لا تمتلك رصيد كافي لاستقبال طلبات, يرجى الشحن لفك حجز الحساب");
            builder.setPositiveButton("شحن", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(getApplicationContext(), WalletActivity.class));
                }
            });
            builder.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    toggleButton.setChecked(false);
                    dialogInterface.dismiss();
                }
            });
            builder.setCancelable(false);
            builder.show();

            return false;
        }
        else return true;
    }

    private void DrawPolyLine() {
        try {
            double lng = Double.parseDouble(Snap_data.get("lng").toString());
            double lat = Double.parseDouble(Snap_data.get("lat").toString());

            PolylineOptions polylineOptions = new PolylineOptions()
                .add(new LatLng(lat, lng))
                .add(new LatLng(loc.getLatitude(), loc.getLongitude()));
            polyline = mMap.addPolyline(polylineOptions);

            Drawable circleDrawable = getResources().getDrawable(R.drawable.mark);
            BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
            LatLng l = new LatLng(lat, lng);

            mMap.addMarker(new MarkerOptions().position(l).icon(markerIcon));

//            new FetchURL(MapsActivity.this).execute(getUrl(new LatLng(loc.getLatitude(), loc.getLongitude()),
//                    new LatLng(lat, lng), "driving"), "driving");

        } catch (Exception ex) {
        }

    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
//        registerActivityLifecycleCallbacks(handler);
//        registerComponentCallbacks(handler);

        circleDrawable = getResources().getDrawable(R.drawable.cc);
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        try {
            mMap.setMyLocationEnabled(true);
        } catch (Exception ex) {
        }
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(false);

        String providerName = mLocationManager.getBestProvider(locationCritera, true);
        Location location = mLocationManager.getLastKnownLocation(providerName);

        if (loc == null && location != null) {
            loc = location;
            markerLat = loc.getLatitude();
            markerLng = loc.getLongitude();
            BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);

            LatLng l = new LatLng(loc.getLatitude(), loc.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 15.0f));
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                    .icon(markerIcon));
        }

        txvAccountState = findViewById(R.id.textView10);
        m = findViewById(R.id.textView511);
        k = findViewById(R.id.textView57);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b && CheckBalance()) {

                    isConnected = true;
                    SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("isConnected", isConnected);
                    editor.apply();
                    editor.commit();

                    String providerName = mLocationManager.getBestProvider(locationCritera, true);
                    Location location = mLocationManager.getLastKnownLocation(providerName);

                    if(location != null){
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
                        map.put("timems", System.currentTimeMillis());

                        try{
                            FirebaseFirestore.getInstance()
                                    .collection("locations")
                                    .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                    .update(map);
                        }
                        catch (Exception ex){}
                    }

                    circleDrawable = getResources().getDrawable(R.drawable.ccc);
                    toggleButton.setBackgroundDrawable(getDrawable(R.drawable.btn_back23));

                    FirebaseFirestore.getInstance()
                            .collection("driverRequests")
                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                            .addSnapshotListener(event);

                    if (!Settings.canDrawOverlays(MapsActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 1234); }

                    try{
                        mMap.clear();
                        markerLat = loc.getLatitude();
                        markerLng = loc.getLongitude();
                        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                        LatLng l =new LatLng(loc.getLatitude(), loc.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l,15.0f));
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                                .icon(markerIcon));
                    }catch (Exception ex){}
                }
                else {
                    isConnected = false;
                    SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("isConnected", isConnected);
                    editor.apply();
                    editor.commit();

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

                    try{
                        mMap.clear();
                        markerLat = loc.getLatitude();
                        markerLng = loc.getLongitude();
                        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                        LatLng l =new LatLng(loc.getLatitude(), loc.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l,15.0f));
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                                .icon(markerIcon));
                    }catch (Exception ex){}
                }

            }
        });

        setNavView();
        getUserInfo();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    if(loc != null || location != null){
                        progressDialog.dismiss();
                    }
                    else{
                        if(location_buffer)
                            onMapReady(mMap);
                        else{
                            location_buffer = false;
                            try{
                                progressDialog.dismiss();
                            }
                            catch (Exception ex){}
                        }

                    }

                }
                catch (Exception ex){}
            }
        }, 5000);
    }

    void setNavView(){
        drawerLayout = findViewById(R.id.my_drawer_layout);
        navigationView = findViewById(R.id.navView);

        TextView nav_myProfile , nav_wallet , nav_carMg , nav_myTrips
                , nav_notifications , nav_sittings , nav_contactUs , nav_logout;
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
                            Glide.with(getApplicationContext()).load(Event.split("!!!")[0]).into(nav_imageView);
                            nav_imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Event.split("!!!")[1])));
                                }
                            });
                        }catch (Exception e){}
                    }
                });

        final TextView prices = findViewById(R.id.textViews23);
        FirebaseFirestore.getInstance()
                .collection("AdminDataConfig")
                .document("Data")
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.getString("Prices").equals("1"))
                    prices.setVisibility(View.VISIBLE);

                prices.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MapsActivity.this);
                        LayoutInflater inflater = MapsActivity.this.getLayoutInflater();
                        builder.setView(inflater.inflate(R.layout.dialog_trip_req3, null));
                        final androidx.appcompat.app.AlertDialog dialog2 = builder.create();
                        ((FrameLayout) dialog2.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog2.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = Math.round(TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 460, getResources().getDisplayMetrics()));
                        dialog2.getWindow().setAttributes(lp);
                        dialog2.show();

                        TextView t1 = dialog2.findViewById(R.id.txvType);
                        TextView t2 = dialog2.findViewById(R.id.txvColor);
                        TextView t3 = dialog2.findViewById(R.id.txvColaor);
                        TextView t4 = dialog2.findViewById(R.id.txvColasor);
                        TextView t5 = dialog2.findViewById(R.id.txvColaasor);
                        TextView t6 = dialog2.findViewById(R.id.txvColaaso6r);
                        TextView t7 = dialog2.findViewById(R.id.txvColaasso6r);
                        TextView t8 = dialog2.findViewById(R.id.txColaasso6r);

                        t1.setText("سعر البداية : "+documentSnapshot.get("base_price")+" دينار.");
                        t2.setText("سعر الدقيقة : "+documentSnapshot.get("minute_price")+" دينار.");
                        t3.setText("سعر ك.م دون ال 4 ك.م : "+documentSnapshot.get("below_4_km")+" دينار.");
                        t4.setText("سعر ك.م ل 4-5 ك.م : "+documentSnapshot.get("between_4n5_km")+" دينار.");
                        t5.setText("سعر ك.م ل 5-8 ك.م : "+documentSnapshot.get("between_5n8_km")+" دينار.");
                        t6.setText("سعر ك.م لأكثر من 8 ك.م : "+documentSnapshot.get("more_8_km")+" دينار.");
                        t7.setText("سعر أقل رحلة : "+documentSnapshot.get("minimum_trip_cost")+" دينار.");
                        t8.setText("نسبة التطبيق : "+documentSnapshot.get("driver_fee")+" بلمئة.");

                    }
                });

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
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("النظام ...");
                builder.setMessage("هل تريد تسجيل الخروج؟");
                builder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(MapsActivity.this, MainActivity.class)
                        .putExtra("exit", "1"));
                        finish();
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
        });


        try{
            FirebaseFirestore.getInstance().collection("Users")
                    .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            nav_wallet.setText("المحفظة" + "   "+UserInfo_sharedPreference
                                    .round(new BigDecimal(value.get("balance").toString()).doubleValue(),2) + "   " + "دينار");
                            nav_wallet.setTextColor(Color.WHITE);
                            if(UserInfo_sharedPreference
                                    .round(new BigDecimal(value.get("balance").toString()).doubleValue(),2)>0){
                                nav_wallet.setTextColor(Color.GREEN);
                            }else if(UserInfo_sharedPreference
                                    .round(new BigDecimal(value.get("balance").toString()).doubleValue(),2)<=0){
                                nav_wallet.setTextColor(Color.RED);
                            }

                        }
                    });
        }
        catch (Exception ex){}
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

            if(mMap != null){
                mMap.clear();
                markerLat = location.getLatitude();
                markerLng = location.getLongitude();
                BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);

                if(loc == null)
                    loc = location;

                float bearing = loc.bearingTo(location) ;

                LatLng l =new LatLng(location.getLatitude(), location.getLongitude());
                if(!TripState.equals("tracking"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l,16.0f));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .icon(markerIcon)
                        .anchor(0.5f, 0.5f)
                        .rotation(bearing));
            }

            loc = location;

            if(StartedTripAt != 0 && InTrip){
                TripDistanceCalc.add(loc);
                BigDecimal TripDistance = new BigDecimal(OldDistance);
                for(int i=0; i<TripDistanceCalc.size()-1; i++)
                    TripDistance = TripDistance.add(new BigDecimal(GetDistanceFromLatLonInKm(
                            TripDistanceCalc.get(i).getLatitude(), TripDistanceCalc.get(i).getLongitude(),
                            TripDistanceCalc.get(i+1).getLatitude(), TripDistanceCalc.get(i+1).getLongitude())));
                k.setText(String.format(Locale.ENGLISH,"%.3f", TripDistance.doubleValue())+" Km");

                Map<String, Object> calc_map = new HashMap<>();
                calc_map.put("km", TripDistance.doubleValue());

                try{
                    if(!(TripObjTid+"").equals(""))
                        FirebaseFirestore.getInstance()
                                .collection("Trips")
                                .document(TripObjTid+"")
                                .update(calc_map);
                }
                catch (Exception ex){}

            }

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
                map.put("timems", System.currentTimeMillis());

                try{
                    FirebaseFirestore.getInstance()
                            .collection("locations")
                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                            .set(map);
                }
                catch (Exception ex){}

                if(InTrip && Snap_data != null && DrawPoly)
                    DrawPolyLine();
                if(InTrip && Snap_data != null){
                    HashMap<String, Object> mini_map3 = new HashMap<>();
                    mini_map3.put("lat", loc.getLatitude());
                    mini_map3.put("lng", loc.getLongitude());
                    mini_map3.put("rotateDriver", 0);

                    try{
                        FirebaseFirestore.getInstance()
                                .collection("Trips")
                                .document(TripObjTid)
                                .update("locationDriver", mini_map3);
                    }
                    catch (Exception ex){}
                }
            }
            else{
                if(!UserInfo_sharedPreference.getUser(MapsActivity.this).uid.equals("")){
                    FirebaseFirestore.getInstance()
                            .collection("locations")
                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                            .delete();
//                    FirebaseFirestore.getInstance()
//                            .collection("Users")
//                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
//                            .update("token", "");
                } }

            if(FirstOpen){
                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MapsActivity.this);
                LayoutInflater inflater = MapsActivity.this.getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.dialog_trip_req, null));
                final androidx.appcompat.app.AlertDialog dialog = builder.create();
                ((FrameLayout) dialog.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = Math.round(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics()));
                dialog.getWindow().setAttributes(lp);
                FirstOpen = false;

                FirebaseFirestore.getInstance()
                        .collection("Trips")
                        .whereEqualTo("idDriver", UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                        .whereNotEqualTo("state", "StateTrip.done")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        List<DocumentSnapshot> list2 = queryDocumentSnapshots.getDocuments();
                        list.clear();

                        for(int i=0; i<list2.size(); i++)
                            if(list2.get(i).toObject(MyTripsModel.class).state.contains("StateTrip.active") ||
                                    list2.get(i).toObject(MyTripsModel.class).state.contains("StateTrip.started") ||
                                    list2.get(i).toObject(MyTripsModel.class).state.contains("StateTrip.needRatingByDriver"))
                                list.add(list2.get(i));

                        if(list.size() > 0){

                            circleDrawable = getResources().getDrawable(R.drawable.ccc);

                            MyTripsModel obj = list.get(list.size()-1).toObject(MyTripsModel.class);

                            FirebaseFirestore.getInstance()
                                    .collection("Trips")
                                    .document(obj.tripsid+"")
                                    .addSnapshotListener(event2);

                            if(obj.state.equals("StateTrip.active")){
                                dialog_count = 1;
                                InTrip = true;
                                SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                editor.putBoolean("InTrip", InTrip);
                                editor.apply();
                                editor.commit();

                                progressDialogLoad.show();

                                findViewById(R.id.textView7).setVisibility(View.VISIBLE);
                                k.setText("0.000 Km");
                                m.setText("00:00:00");

                                final Map<String, Object> map = new HashMap<>();
                                final Map<String, Object> mini_map = new HashMap<>();
                                final Map<String, Object> mini_map2 = new HashMap<>();
                                final Map<String, Object> mini_map3 = new HashMap<>();

                                map.put("tripsid", obj.tripsid);
                                map.put("date", obj.date);
                                map.put("idCustomer", obj.idCustomer);
                                map.put("idDriver", obj.idDriver);
                                map.put("dateStart", obj.dateStart);
                                map.put("dateAcceptRequest", obj.dateAcceptRequest);
                                map.put("state", obj.state);
                                map.put("km", obj.km);
                                map.put("totalPrice", obj.totalPrice);
                                map.put("hours", obj.hours);
                                map.put("typeTrip", obj.typeTrip);
                                map.put("discount", obj.discount);
                                map.put("addressCurrent", obj.addressCurrent);

                                mini_map.put("lat", (obj.accessPoint.get("lat")));
                                mini_map.put("lng", (obj.accessPoint.get("lng")));
                                mini_map.put("addressTo", (obj.accessPoint.get("addressTo")));
                                map.put("accessPoint", mini_map);

                                mini_map2.put("lat", obj.locationCustomer.get("lat"));
                                mini_map2.put("lng", obj.locationCustomer.get("lng"));
                                map.put("locationCustomer", mini_map2);

                                mini_map3.put("lat", obj.locationDriver.get("lat"));
                                mini_map3.put("lng", obj.locationDriver.get("lng"));
                                mini_map3.put("rotateDriver", obj.locationDriver.get("rotateDriver"));
                                map.put("locationDriver", mini_map3);

                                FirebaseFirestore.getInstance()
                                        .collection("Trips")
                                        .document(map.get("tripsid").toString())
                                        .update(map)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                FirebaseFirestore.getInstance()
                                                        .collection("driverRequests")
                                                        .whereEqualTo("idCustomer", map.get("idCustomer").toString())
                                                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                        for(int i=0; i<list.size(); i++){
                                                            FirebaseFirestore.getInstance()
                                                                    .collection("driverRequests")
                                                                    .document(list.get(i).getId())
                                                                    .delete();
                                                        }
                                                    }
                                                });

                                                FirebaseFirestore.getInstance()
                                                        .collection("location")
                                                        .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                        .update("available", false);

                                                progressDialogLoad.dismiss();

                                                DrawPoly = true;
                                                DrawPolyLine();

                                                findViewById(R.id.card_default).setVisibility(View.INVISIBLE);
                                                findViewById(R.id.card_default2).setVisibility(View.VISIBLE);

                                                ImageView call = findViewById(R.id.call);
                                                CardView loca = findViewById(R.id.card3);
                                                TextView arrive = findViewById(R.id.textView11);
                                                TextView canc = findViewById(R.id.textView7);
                                                TextView name = findViewById(R.id.txvType);
                                                TextView locat = findViewById(R.id.txvColor);

                                                name.setText(obj.nameCustomer);
                                                locat.setText(obj.addressCurrent);

                                                call.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                                        intent.setData(Uri.parse("tel:"+obj.phoneCustomer.replace("+962", "0")));
                                                        startActivity(intent);
                                                    }
                                                });

                                                loca.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                                Uri.parse("http://maps.google.com/maps?saddr="+
                                                                        loc.getLatitude()+","+loc.getLongitude()+
                                                                        "&daddr="+obj.locationCustomer.get("lat")+","+obj.locationCustomer.get("lng")));
                                                        startActivity(intent);
                                                    }
                                                });

                                                final double clng = Double.parseDouble(obj.locationCustomer.get("lng").toString());
                                                final double clat = Double.parseDouble(obj.locationCustomer.get("lat").toString());

                                                arrive.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        if(arrive.getText().toString().equals("لقد وصلت")){
//                                                            if(GetDistanceFromLatLonInKm(loc.getLatitude(),loc.getLongitude(), clat, clng) <= 0.10){
                                                            if(true){
                                                                progressDialogLoad.show();
                                                                FirebaseFirestore.getInstance()
                                                                        .collection("Users")
                                                                        .document(map.get("idCustomer").toString())
                                                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                        arrive.setText("بدء الرحلة");
                                                                        FirebaseFirestore.getInstance()
                                                                                .collection("Users")
                                                                                .document(map.get("idCustomer").toString())
                                                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                SendNoti("لقد وصل الكابتن للموقع.", documentSnapshot.getString("token"));
                                                                                progressDialogLoad.dismiss();
                                                                            }
                                                                        });

                                                                        progressDialogLoad.dismiss();
                                                                        cd = new CountDownTimer(300000, 1000) {

                                                                            public void onTick(long millisUntilFinished) {

                                                                                if(millisUntilFinished >= 240000){
                                                                                    if(millisUntilFinished-240000 < 10000) canc.setText("04:0" + ((millisUntilFinished-240000)/1000));
                                                                                    else canc.setText("04:" + ((millisUntilFinished-240000)/1000)); }

                                                                                else if(millisUntilFinished >= 180000){
                                                                                    if(millisUntilFinished-180000 < 10000) canc.setText("03:0" + ((millisUntilFinished-180000)/1000));
                                                                                    else canc.setText("03:" + ((millisUntilFinished-180000)/1000)); }

                                                                                else if(millisUntilFinished >= 120000){
                                                                                    if(millisUntilFinished-120000 < 10000) canc.setText("02:0" + ((millisUntilFinished-120000)/1000));
                                                                                    else canc.setText("02:" + ((millisUntilFinished-180000)/1000)); }

                                                                                else if(millisUntilFinished >= 60000){
                                                                                    if(millisUntilFinished-60000 < 10000) canc.setText("01:0" + ((millisUntilFinished-60000)/1000));
                                                                                    else canc.setText("01:" + ((millisUntilFinished-180000)/1000)); }

                                                                                else if(millisUntilFinished >= 0){
                                                                                    if(millisUntilFinished < 10000) canc.setText("01:0" + (millisUntilFinished/1000));
                                                                                    else canc.setText("01:" + (millisUntilFinished/1000)); }

                                                                            }

                                                                            public void onFinish() {
                                                                                canc.setText("إلغاء");
                                                                            }

                                                                        };
                                                                        cd.start();
                                                                    }
                                                                });
                                                            }
                                                            else
                                                                Toast.makeText(MapsActivity.this, "لا يمكن الوصول على بعد أكبر من 100 مترا", Toast.LENGTH_LONG).show();
                                                        }
                                                        else if(arrive.getText().toString().equals("بدء الرحلة")){
                                                            FirebaseFirestore.getInstance()
                                                                    .collection("Users")
                                                                    .document(map.get("idCustomer").toString())
                                                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    SendNoti("لقد بدأت الرحلة.", documentSnapshot.getString("token"));
                                                                }
                                                            });
                                                            DrawPoly = false;
//                                                            try{
//                                                                polyline.remove();
//                                                            }
//                                                            catch (Exception ex){}
                                                            progressDialogLoad.show();
                                                            FirebaseFirestore.getInstance()
                                                                    .collection("Trips")
                                                                    .document(map.get("tripsid").toString())
                                                                    .update("state", "StateTrip.started")
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            progressDialogLoad.dismiss();

                                                                            try{
                                                                                timer = new Timer();
                                                                                timer.schedule(new TimerTask() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        runOnUiThread(new Runnable()
                                                                                        {
                                                                                            @Override
                                                                                            public void run()
                                                                                            {
                                                                                                Tsec += 1;
                                                                                                String hour_msg = "0"+Thour+":";
                                                                                                String hour_msg2 = Thour+":";
                                                                                                String min_msg = "0"+Tmin+":";
                                                                                                String min_msg2 = Tmin+":";
                                                                                                String sec_msg = "0"+Tsec;
                                                                                                String sec_msg2 = ""+Tsec;

                                                                                                if(Tsec == 59){ Tsec = 0; Tmin += 1; }
                                                                                                if(Tmin == 59){ Tmin = 0; Thour += 1; }

                                                                                                String final_msg = "";
                                                                                                if(Thour >= 10) final_msg += hour_msg2;
                                                                                                else final_msg += hour_msg;
                                                                                                if(Tmin >= 10) final_msg += min_msg2;
                                                                                                else final_msg += min_msg;
                                                                                                if(Tsec >= 10) final_msg += sec_msg2;
                                                                                                else final_msg += sec_msg;

                                                                                                m.setText(final_msg);
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }, 1000, 1000);
                                                                            }
                                                                            catch (Exception ex){}

                                                                            arrive.setText("إنهاء الرحلة");
                                                                            StartedTripAt = System.currentTimeMillis();
                                                                            TripDistanceCalc = new ArrayList<>();
                                                                            k.setText("0.0 Km");
                                                                            canc.setText("إلغاء");
                                                                            canc.setVisibility(View.INVISIBLE);
                                                                            cd.cancel();
                                                                        }
                                                                    });
                                                        }
                                                        else if(arrive.getText().toString().equals("إنهاء الرحلة")){
                                                            AlertDialog.Builder d = new AlertDialog.Builder(MapsActivity.this);
                                                            d.setMessage("هل تريد تأكيد انهاء الرحلة؟");
                                                            d.setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    {
                                                                        FirebaseFirestore.getInstance()
                                                                                .collection("Users")
                                                                                .document(obj.idCustomer)
                                                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                CustomerBalance = new BigDecimal(documentSnapshot.get("balance").toString());
                                                                            }
                                                                        });

                                                                        try{
                                                                            timer.cancel();
                                                                            timer.purge();
                                                                        }
                                                                        catch (Exception ex){}
                                                                        Tsec = 0;
                                                                        Tmin = 0;
                                                                        Thour = 0;
                                                                        m.setText("00:00:00");

                                                                        progressDialogLoad.show();

                                                                        FirebaseFirestore.getInstance()
                                                                                .collection("Trips")
                                                                                .document(map.get("tripsid").toString())
                                                                                .update("state", "StateTrip.needRatingByDriver")
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        FirebaseFirestore.getInstance()
                                                                                                .collection("AdminDataConfig")
                                                                                                .document("Data")
                                                                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                            @Override
                                                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                                                                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MapsActivity.this);
                                                                                                LayoutInflater inflater = MapsActivity.this.getLayoutInflater();
                                                                                                builder.setView(inflater.inflate(R.layout.dialog_trip_req2, null));
                                                                                                final androidx.appcompat.app.AlertDialog dialog2 = builder.create();
                                                                                                ((FrameLayout) dialog2.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                                                                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                                                                lp.copyFrom(dialog2.getWindow().getAttributes());
                                                                                                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                                                                                lp.height = Math.round(TypedValue.applyDimension(
                                                                                                        TypedValue.COMPLEX_UNIT_DIP, 510, getResources().getDisplayMetrics()));
                                                                                                dialog2.getWindow().setAttributes(lp);
                                                                                                dialog2.show();
                                                                                                dialog2.setCancelable(false);

                                                                                                final TextView t1 = dialog2.findViewById(R.id.txvType);
                                                                                                final TextView t2 = dialog2.findViewById(R.id.txvColor);
                                                                                                final TextView t3 = dialog2.findViewById(R.id.txvNo);
                                                                                                final TextView t4 = dialog2.findViewById(R.id.textView11);
                                                                                                final TextView t5 = dialog2.findViewById(R.id.txvNo5);

                                                                                                BigDecimal TripDistance = new BigDecimal(OldDistance);
                                                                                                for(int i=0; i<TripDistanceCalc.size()-1; i++){

                                                                                                    TripDistance = TripDistance.add(new BigDecimal(GetDistanceFromLatLonInKm(
                                                                                                            TripDistanceCalc.get(i).getLatitude(), TripDistanceCalc.get(i).getLongitude(),
                                                                                                            TripDistanceCalc.get(i+1).getLatitude(), TripDistanceCalc.get(i+1).getLongitude())));

                                                                                                }

                                                                                                final BigDecimal FinalTripDistance = TripDistance;

                                                                                                double base_price = 0, below_4_km = 0, between_4n5_km = 0, between_5n8_km = 0,
                                                                                                        more_8_km = 0, minute_price = 0, minimum_trip_cost = 0, driver_fee = 0;

                                                                                                try{
                                                                                                    base_price = Double.parseDouble(documentSnapshot.getString("base_price"));
                                                                                                    below_4_km = Double.parseDouble(documentSnapshot.getString("below_4_km"));
                                                                                                    between_4n5_km = Double.parseDouble(documentSnapshot.getString("between_4n5_km"));
                                                                                                    between_5n8_km = Double.parseDouble(documentSnapshot.getString("between_5n8_km"));
                                                                                                    more_8_km = Double.parseDouble(documentSnapshot.getString("more_8_km"));
                                                                                                    minute_price = Double.parseDouble(documentSnapshot.getString("minute_price"));
                                                                                                    minimum_trip_cost = Double.parseDouble(documentSnapshot.getString("minimum_trip_cost"));
                                                                                                    driver_fee = Double.parseDouble(documentSnapshot.getString("driver_fee")); }
                                                                                                catch (Exception ex){}

                                                                                                long time = ((System.currentTimeMillis()/1000) - (StartedTripAt/1000))/60;
                                                                                                StartedTripAt = 0;
                                                                                                BigDecimal TotalTripPrice = new BigDecimal("-1");

                                                                                                if(TripDistance.doubleValue() <= 4)
                                                                                                    TotalTripPrice = new BigDecimal(base_price)
                                                                                                            .add(new BigDecimal(below_4_km).multiply(TripDistance))
                                                                                                            .add(new BigDecimal(minute_price).multiply(new BigDecimal(time)));
                                                                                                else if(TripDistance.doubleValue() > 4 && TripDistance.doubleValue() <= 5)
                                                                                                    TotalTripPrice = new BigDecimal(base_price)
                                                                                                            .add(new BigDecimal(between_4n5_km).multiply(TripDistance))
                                                                                                            .add(new BigDecimal(minute_price).multiply(new BigDecimal(time)));
                                                                                                else if(TripDistance.doubleValue() > 5 && TripDistance.doubleValue() <= 8)
                                                                                                    TotalTripPrice = new BigDecimal(base_price)
                                                                                                            .add(new BigDecimal(between_5n8_km).multiply(TripDistance))
                                                                                                            .add(new BigDecimal(minute_price).multiply(new BigDecimal(time)));
                                                                                                else if(TripDistance.doubleValue() > 8)
                                                                                                    TotalTripPrice = new BigDecimal(base_price)
                                                                                                            .add(new BigDecimal(more_8_km).multiply(TripDistance))
                                                                                                            .add(new BigDecimal(minute_price).multiply(new BigDecimal(time)));

                                                                                                if (TotalTripPrice.doubleValue() < minimum_trip_cost)
                                                                                                    TotalTripPrice = new BigDecimal(minimum_trip_cost);

                                                                                                final BigDecimal PriceWithoutDisc = TotalTripPrice;
                                                                                                final BigDecimal disc = new BigDecimal(obj.discount+"").divide(new BigDecimal("100"));
                                                                                                final BigDecimal disc2 = new BigDecimal(driver_fee).divide(new BigDecimal("100"));
                                                                                                final BigDecimal CompanyShare = TotalTripPrice.multiply(disc2);
                                                                                                final BigDecimal DiscountablePrice = TotalTripPrice.subtract(new BigDecimal(minimum_trip_cost));
                                                                                                final BigDecimal CustomerPrice = DiscountablePrice.subtract(DiscountablePrice.multiply(disc));
                                                                                                final BigDecimal AddBalancePrice = CompanyShare.subtract(DiscountablePrice.multiply(disc));
                                                                                                final BigDecimal AddSubBalance = new BigDecimal("0").subtract(AddBalancePrice);
                                                                                                final BigDecimal FinalCustomerPrice = CustomerPrice.add(new BigDecimal(minimum_trip_cost));

                                                                                                FirebaseFirestore.getInstance()
                                                                                                        .collection("Users")
                                                                                                        .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                                                        .update("balance", FieldValue.increment(AddSubBalance.doubleValue()))
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                                                                Toast.makeText(MapsActivity.this, "تم إضافة مبلغ " + String.format(Locale.ENGLISH, "%.2f", AddSubBalance.doubleValue()), Toast.LENGTH_SHORT).show();

                                                                                                                double km = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f", FinalTripDistance.doubleValue()));
                                                                                                                double totalPrice =Double.parseDouble(String.format(Locale.ENGLISH,"%.2f", PriceWithoutDisc.doubleValue()));

                                                                                                                Map<String, Object> ups = new HashMap<>();
                                                                                                                Map<String, Object> mini_locs = new HashMap<>();
                                                                                                                mini_locs.put("addressTo","");
                                                                                                                mini_locs.put("lat",loc.getLatitude());
                                                                                                                mini_locs.put("lng",loc.getLongitude());
                                                                                                                ups.put("accessPoint",mini_locs);
                                                                                                                ups.put("km",km);
                                                                                                                ups.put("hours", time);
                                                                                                                ups.put("totalPrice",totalPrice);
                                                                                                                FirebaseFirestore.getInstance()
                                                                                                                        .collection("Trips")
                                                                                                                        .document(map.get("tripsid").toString())
                                                                                                                        .update(ups).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                                        BigDecimal Balance = new BigDecimal("0");
                                                                                                                        BigDecimal Change = new BigDecimal("0");
                                                                                                                        BigDecimal Recieve = new BigDecimal("0");

                                                                                                                        if(CustomerBalance.subtract(FinalCustomerPrice).doubleValue() >= 0){
                                                                                                                            Balance = CustomerBalance.subtract(FinalCustomerPrice);
                                                                                                                            Change = new BigDecimal("0");
                                                                                                                            Recieve = FinalCustomerPrice;
                                                                                                                        }
                                                                                                                        else{
                                                                                                                            Balance = new BigDecimal("0");
                                                                                                                            Change = FinalCustomerPrice.subtract(CustomerBalance);
                                                                                                                            Recieve = CustomerBalance;
                                                                                                                        }

                                                                                                                        t1.setText(obj.nameCustomer);
                                                                                                                        t2.setText("المسافة المقطوعة: "+String.format("%.2f", FinalTripDistance.doubleValue())+" Km \n"+"وقت الرحلة: "+time+" min");
                                                                                                                        t3.setText("عداد الرحلة: "+String.format("%.2f", PriceWithoutDisc)+" دينار بخصم "+obj.discount+"%");
                                                                                                                        t5.setText("القيمة المطلوبة كاش: "+String.format("%.2f", Change.doubleValue())+" دينار");

                                                                                                                        CustomerBalance = Balance;
                                                                                                                        final BigDecimal AddBalance = Recieve;
                                                                                                                        progressDialog.dismiss();

                                                                                                                        t4.setOnClickListener(new View.OnClickListener() {
                                                                                                                            @Override
                                                                                                                            public void onClick(View view) {
                                                                                                                                progressDialog.show();

                                                                                                                                Map<String, Object> dada = new HashMap<>();
                                                                                                                                dada.put("balance",Double.parseDouble(String.format(Locale.ENGLISH,"%.2f", CustomerBalance)));
                                                                                                                                FirebaseFirestore.getInstance()
                                                                                                                                        .collection("Users")
                                                                                                                                        .document(obj.idCustomer)
                                                                                                                                        .update(dada).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                    @Override
                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                        RatingBar rate = dialog2.findViewById(R.id.rating);
                                                                                                                                        double rating = rate.getRating();

                                                                                                                                        Map<String, Object> ra = new HashMap<>();
                                                                                                                                        ra.put("rating", FieldValue.increment(rating));
                                                                                                                                        ra.put("countRating", FieldValue.increment(1));
                                                                                                                                        FirebaseFirestore.getInstance()
                                                                                                                                                .collection("Users")
                                                                                                                                                .document(obj.idCustomer)
                                                                                                                                                .update(ra).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                            @Override
                                                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                                FirebaseFirestore.getInstance()
                                                                                                                                                        .collection("Users")
                                                                                                                                                        .document(map.get("idCustomer").toString())
                                                                                                                                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                                                                                        SendNoti("عميلنا العزيز شكرا لاستخدامكم تطبيق رويال رايد, نتمنى لك يوما سعيدا", documentSnapshot.getString("token"));
                                                                                                                                                        toggleButton.setChecked(true);
                                                                                                                                                        OldDistance = "0";
                                                                                                                                                    }
                                                                                                                                                });

                                                                                                                                                FirebaseFirestore.getInstance()
                                                                                                                                                        .collection("Users")
                                                                                                                                                        .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                                                                                                        .update("balance", FieldValue.increment(AddBalance.doubleValue()));

                                                                                                                                                FirebaseFirestore.getInstance()
                                                                                                                                                        .collection("Trips")
                                                                                                                                                        .document(map.get("tripsid").toString())
                                                                                                                                                        .update("state", "StateTrip.needRatingByCustomer")
                                                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                            @Override
                                                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                progressDialog.dismiss();
                                                                                                                                                                arrive.setText("لقد وصلت");
                                                                                                                                                                canc.setText("إلغاء");
                                                                                                                                                                dialog_count = 0;
                                                                                                                                                                InTrip = false;
                                                                                                                                                                isConnected = true;
                                                                                                                                                                SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                                                                                                                                                editor.putBoolean("isConnected", isConnected);
                                                                                                                                                                editor.putBoolean("InTrip", InTrip);
                                                                                                                                                                editor.apply();
                                                                                                                                                                editor.commit();
                                                                                                                                                                findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                                                                                                                                findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);
                                                                                                                                                                dialog2.dismiss();
                                                                                                                                                            }
                                                                                                                                                        });
                                                                                                                                            }
                                                                                                                                        });

                                                                                                                                    }
                                                                                                                                });


                                                                                                                            }
                                                                                                                        });
                                                                                                                    }
                                                                                                                });
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                            d.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                                }
                                                            });
                                                            d.setCancelable(false);
                                                            d.show();
                                                        }
                                                    }
                                                });

                                                canc.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        if(!canc.getText().toString().equals("إلغاء")){
                                                            new AlertDialog.Builder(MapsActivity.this)
                                                                    .setMessage("سيتم خصم مبلغ بقيمة 0.5 دينار عند قيامك بإلغاء رحلة مقبولة هل ترغب بذلك ؟")
                                                                    .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                            findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                                            findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);
                                                                            progressDialogLoad.show();
                                                                            dialog_count = 0;
                                                                            InTrip = false;
                                                                            isConnected = true;
                                                                            SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                                                            editor.putBoolean("isConnected", isConnected);
                                                                            editor.putBoolean("InTrip", InTrip);
                                                                            editor.apply();
                                                                            editor.commit();
                                                                            findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                                            findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);

                                                                            FirebaseFirestore.getInstance()
                                                                                    .collection("driverRequests")
                                                                                    .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                                    .delete();

                                                                            FirebaseFirestore.getInstance()
                                                                                    .collection("Trips")
                                                                                    .document(map.get("tripsid").toString())
                                                                                    .update("state", "StateTrip.cancelByDriver");

                                                                            BigDecimal bal = new BigDecimal(UserInfo_sharedPreference.getUser(MapsActivity.this).balance)
                                                                                    .subtract(new BigDecimal("0.5"));
                                                                            FirebaseFirestore.getInstance()
                                                                                    .collection("Users")
                                                                                    .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                                    .update("balance", bal.doubleValue());
                                                                            progressDialogLoad.dismiss();
                                                                        }
                                                                    }).setNegativeButton("لا", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    dialog.dismiss();
                                                                }
                                                            }).setCancelable(false).create().show();
                                                        }
                                                        else{

                                                            AlertDialog.Builder d = new AlertDialog.Builder(MapsActivity.this);
                                                            d.setCancelable(false);
                                                            d.setTitle("النظام...");
                                                            d.setMessage("هل تريد تأكيد الإلغاء؟");
                                                            d.setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                                    findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);

                                                                    dialog_count = 0;
                                                                    InTrip = false;
                                                                    isConnected = true;
                                                                    SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                                                    editor.putBoolean("isConnected", isConnected);
                                                                    editor.putBoolean("InTrip", InTrip);
                                                                    editor.apply();
                                                                    editor.commit();
                                                                    findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                                    findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);

                                                                    progressDialogLoad.show();

                                                                    FirebaseFirestore.getInstance()
                                                                            .collection("driverRequests")
                                                                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            progressDialogLoad.dismiss();
                                                                        }
                                                                    });

                                                                    FirebaseFirestore.getInstance()
                                                                            .collection("Trips")
                                                                            .document(map.get("tripsid").toString())
                                                                            .update("state", "StateTrip.rejected");
                                                                }
                                                            });
                                                            d.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    dialogInterface.dismiss();
                                                                }
                                                            });
                                                            d.show();

                                                        }
                                                    }
                                                });
                                            }
                                        });

                            }
                            else if(obj.state.equals("StateTrip.started")){
                                dialog_count = 1;
                                InTrip = true;
                                SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                editor.putBoolean("InTrip", InTrip);
                                editor.apply();
                                editor.commit();
                                findViewById(R.id.textView7).setVisibility(View.VISIBLE);
                                k.setText("0.000 Km");
                                m.setText("00:00:00");

                                StartedTripAt = obj.tripsid;
                                OldDistance = obj.km+"";
                                long spent_time = (System.currentTimeMillis() - StartedTripAt)/1000;

                                try{
                                    Thour =  Integer.parseInt((spent_time/3600)+"");
                                    spent_time -= Integer.parseInt((Thour*3600)+"");
                                } catch (Exception ex){Thour = 0;}

                                try{
                                    Tmin = Integer.parseInt((spent_time/60)+"");
                                    spent_time -= Integer.parseInt((Tmin*60)+"");
                                } catch (Exception ex){Tmin = 0;}

                                try{ Tsec =  Integer.parseInt(spent_time+""); } catch (Exception ex){Tmin = 0;}

                                try{
                                    timer = new Timer();
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                    Tsec += 1;//here1
                                                    String hour_msg = "0"+Thour+":";
                                                    String hour_msg2 = Thour+":";
                                                    String min_msg = "0"+Tmin+":";
                                                    String min_msg2 = Tmin+":";
                                                    String sec_msg = "0"+Tsec;
                                                    String sec_msg2 = ""+Tsec;

                                                    if(Tsec == 59){ Tsec = 0; Tmin += 1; }
                                                    if(Tmin == 59){ Tmin = 0; Thour += 1; }

                                                    String final_msg = "";
                                                    if(Thour >= 10) final_msg += hour_msg2;
                                                    else final_msg += hour_msg;
                                                    if(Tmin >= 10) final_msg += min_msg2;
                                                    else final_msg += min_msg;
                                                    if(Tsec >= 10) final_msg += sec_msg2;
                                                    else final_msg += sec_msg;

                                                    m.setText(final_msg);
                                                }
                                            });
                                        }
                                    }, 1000, 1000);
                                }
                                catch (Exception ex){}

                                progressDialog.show();

                                final Map<String, Object> map = new HashMap<>();
                                final Map<String, Object> mini_map = new HashMap<>();
                                final Map<String, Object> mini_map2 = new HashMap<>();
                                final Map<String, Object> mini_map3 = new HashMap<>();

                                map.put("tripsid", obj.tripsid);
                                TripObjTid = obj.tripsid+"";
                                map.put("date", obj.date);
                                map.put("idCustomer", obj.idCustomer);
                                map.put("idDriver", obj.idDriver);
                                map.put("dateStart", obj.dateStart);
                                map.put("dateAcceptRequest", obj.dateAcceptRequest);
                                map.put("state", obj.state);
                                map.put("km", obj.km);
                                map.put("totalPrice", obj.totalPrice);
                                map.put("hours", obj.hours);
                                map.put("typeTrip", obj.typeTrip);
                                map.put("discount", obj.discount);
                                map.put("addressCurrent", obj.addressCurrent);

                                mini_map.put("lat", (obj.accessPoint.get("lat")));
                                mini_map.put("lng", (obj.accessPoint.get("lng")));
                                mini_map.put("addressTo", (obj.accessPoint.get("addressTo")));
                                map.put("accessPoint", mini_map);

                                mini_map2.put("lat", obj.locationCustomer.get("lat"));
                                mini_map2.put("lng", obj.locationCustomer.get("lng"));
                                map.put("locationCustomer", mini_map2);

                                mini_map3.put("lat", obj.locationDriver.get("lat"));
                                mini_map3.put("lng", obj.locationDriver.get("lng"));
                                mini_map3.put("rotateDriver", obj.locationDriver.get("rotateDriver"));
                                map.put("locationDriver", mini_map3);

                                findViewById(R.id.card_default).setVisibility(View.INVISIBLE);
                                findViewById(R.id.card_default2).setVisibility(View.VISIBLE);

                                ImageView call = findViewById(R.id.call);
                                CardView loca = findViewById(R.id.card3);
                                TextView arrive = findViewById(R.id.textView11);
                                TextView canc = findViewById(R.id.textView7);
                                TextView name = findViewById(R.id.txvType);
                                TextView locat = findViewById(R.id.txvColor);

                                name.setText(obj.nameCustomer);
                                locat.setText(obj.addressCurrent);

                                call.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:"+obj.phoneCustomer.replace("+962", "0")));
                                        startActivity(intent);
                                    }
                                });

                                loca.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                Uri.parse("http://maps.google.com/maps?saddr="+
                                                        loc.getLatitude()+","+loc.getLongitude()+
                                                        "&daddr="+obj.locationCustomer.get("lat")+","+obj.locationCustomer.get("lng")));
                                        startActivity(intent);
                                    }
                                });

                                arrive.setText("إنهاء الرحلة");
                                TripDistanceCalc = new ArrayList<>();
                                k.setText("0.0 Km");
                                canc.setText("إلغاء");
                                canc.setVisibility(View.INVISIBLE);
                                try{cd.cancel();} catch (Exception ex){}

                                progressDialog.dismiss();

                                arrive.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if(arrive.getText().toString().equals("إنهاء الرحلة")){
                                            AlertDialog.Builder d = new AlertDialog.Builder(MapsActivity.this);
                                            d.setMessage("هل تريد تأكيد انهاء الرحلة؟");
                                            d.setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    {
                                                        FirebaseFirestore.getInstance()
                                                                .collection("Users")
                                                                .document(obj.idCustomer)
                                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                CustomerBalance = new BigDecimal(documentSnapshot.get("balance").toString());
                                                            }
                                                        });

                                                        try{
                                                            timer.cancel();
                                                            timer.purge();
                                                        }
                                                        catch (Exception ex){}
                                                        Tsec = 0;
                                                        Tmin = 0;
                                                        Thour = 0;
                                                        m.setText("00:00:00");
                                                        progressDialog.show();

                                                        FirebaseFirestore.getInstance()
                                                                .collection("Trips")
                                                                .document(map.get("tripsid").toString())
                                                                .update("state", "StateTrip.needRatingByDriver")
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        FirebaseFirestore.getInstance()
                                                                                .collection("AdminDataConfig")
                                                                                .document("Data")
                                                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                                                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MapsActivity.this);
                                                                                LayoutInflater inflater = MapsActivity.this.getLayoutInflater();
                                                                                builder.setView(inflater.inflate(R.layout.dialog_trip_req2, null));
                                                                                final androidx.appcompat.app.AlertDialog dialog2 = builder.create();
                                                                                ((FrameLayout) dialog2.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                                                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                                                lp.copyFrom(dialog2.getWindow().getAttributes());
                                                                                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                                                                lp.height = Math.round(TypedValue.applyDimension(
                                                                                        TypedValue.COMPLEX_UNIT_DIP, 510, getResources().getDisplayMetrics()));
                                                                                dialog2.getWindow().setAttributes(lp);
                                                                                dialog2.show();
                                                                                dialog2.setCancelable(false);

                                                                                final TextView t1 = dialog2.findViewById(R.id.txvType);
                                                                                final TextView t2 = dialog2.findViewById(R.id.txvColor);
                                                                                final TextView t3 = dialog2.findViewById(R.id.txvNo);
                                                                                final TextView t4 = dialog2.findViewById(R.id.textView11);
                                                                                final TextView t5 = dialog2.findViewById(R.id.txvNo5);

                                                                                BigDecimal TripDistance = new BigDecimal(OldDistance);
                                                                                for(int i=0; i<TripDistanceCalc.size()-1; i++){

                                                                                    TripDistance = TripDistance.add(new BigDecimal(GetDistanceFromLatLonInKm(
                                                                                            TripDistanceCalc.get(i).getLatitude(), TripDistanceCalc.get(i).getLongitude(),
                                                                                            TripDistanceCalc.get(i+1).getLatitude(), TripDistanceCalc.get(i+1).getLongitude())));

                                                                                }

                                                                                final BigDecimal FinalTripDistance = TripDistance;

                                                                                double base_price = 0, below_4_km = 0, between_4n5_km = 0, between_5n8_km = 0,
                                                                                        more_8_km = 0, minute_price = 0, minimum_trip_cost = 0, driver_fee = 0;

                                                                                try{
                                                                                    base_price = Double.parseDouble(documentSnapshot.getString("base_price"));
                                                                                    below_4_km = Double.parseDouble(documentSnapshot.getString("below_4_km"));
                                                                                    between_4n5_km = Double.parseDouble(documentSnapshot.getString("between_4n5_km"));
                                                                                    between_5n8_km = Double.parseDouble(documentSnapshot.getString("between_5n8_km"));
                                                                                    more_8_km = Double.parseDouble(documentSnapshot.getString("more_8_km"));
                                                                                    minute_price = Double.parseDouble(documentSnapshot.getString("minute_price"));
                                                                                    minimum_trip_cost = Double.parseDouble(documentSnapshot.getString("minimum_trip_cost"));
                                                                                    driver_fee = Double.parseDouble(documentSnapshot.getString("driver_fee")); }
                                                                                catch (Exception ex){}

                                                                                long time = ((System.currentTimeMillis()/1000) - (StartedTripAt/1000))/60;
                                                                                StartedTripAt = 0;
                                                                                BigDecimal TotalTripPrice = new BigDecimal("-1");

                                                                                if(TripDistance.doubleValue() <= 4)
                                                                                    TotalTripPrice = new BigDecimal(base_price)
                                                                                            .add(new BigDecimal(below_4_km).multiply(TripDistance))
                                                                                            .add(new BigDecimal(minute_price).multiply(new BigDecimal(time)));
                                                                                else if(TripDistance.doubleValue() > 4 && TripDistance.doubleValue() <= 5)
                                                                                    TotalTripPrice = new BigDecimal(base_price)
                                                                                            .add(new BigDecimal(between_4n5_km).multiply(TripDistance))
                                                                                            .add(new BigDecimal(minute_price).multiply(new BigDecimal(time)));
                                                                                else if(TripDistance.doubleValue() > 5 && TripDistance.doubleValue() <= 8)
                                                                                    TotalTripPrice = new BigDecimal(base_price)
                                                                                            .add(new BigDecimal(between_5n8_km).multiply(TripDistance))
                                                                                            .add(new BigDecimal(minute_price).multiply(new BigDecimal(time)));
                                                                                else if(TripDistance.doubleValue() > 8)
                                                                                    TotalTripPrice = new BigDecimal(base_price)
                                                                                            .add(new BigDecimal(more_8_km).multiply(TripDistance))
                                                                                            .add(new BigDecimal(minute_price).multiply(new BigDecimal(time)));

                                                                                if (TotalTripPrice.doubleValue() < minimum_trip_cost)
                                                                                    TotalTripPrice = new BigDecimal(minimum_trip_cost);

                                                                                final BigDecimal PriceWithoutDisc = TotalTripPrice;
                                                                                final BigDecimal disc = new BigDecimal(obj.discount+"").divide(new BigDecimal("100"));
                                                                                final BigDecimal disc2 = new BigDecimal(driver_fee).divide(new BigDecimal("100"));
                                                                                final BigDecimal CompanyShare = TotalTripPrice.multiply(disc2);
                                                                                final BigDecimal DiscountablePrice = TotalTripPrice.subtract(new BigDecimal(minimum_trip_cost));
                                                                                final BigDecimal CustomerPrice = DiscountablePrice.subtract(DiscountablePrice.multiply(disc));
                                                                                final BigDecimal AddBalancePrice = CompanyShare.subtract(DiscountablePrice.multiply(disc));
                                                                                final BigDecimal AddSubBalance = new BigDecimal("0").subtract(AddBalancePrice);
                                                                                final BigDecimal FinalCustomerPrice = CustomerPrice.add(new BigDecimal(minimum_trip_cost));

                                                                                FirebaseFirestore.getInstance()
                                                                                        .collection("Users")
                                                                                        .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                                        .update("balance", FieldValue.increment(AddSubBalance.doubleValue()))
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                Toast.makeText(MapsActivity.this, "تم إقتطاع مبلغ "+String.format(Locale.ENGLISH,"%.2f", AddSubBalance.doubleValue()), Toast.LENGTH_SHORT).show();

                                                                                                double km = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f", FinalTripDistance.doubleValue()));
                                                                                                double totalPrice =Double.parseDouble(String.format(Locale.ENGLISH,"%.2f", PriceWithoutDisc.doubleValue()));

                                                                                                Map<String, Object> ups = new HashMap<>();
                                                                                                Map<String, Object> mini_locs = new HashMap<>();
                                                                                                mini_locs.put("addressTo","");
                                                                                                mini_locs.put("lat",loc.getLatitude());
                                                                                                mini_locs.put("lng",loc.getLongitude());
                                                                                                ups.put("accessPoint",mini_locs);
                                                                                                ups.put("km",km);
                                                                                                ups.put("hours", time);
                                                                                                ups.put("totalPrice",totalPrice);
                                                                                                FirebaseFirestore.getInstance()
                                                                                                        .collection("Trips")
                                                                                                        .document(map.get("tripsid").toString())
                                                                                                        .update(ups).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                        BigDecimal Balance = new BigDecimal("0");
                                                                                                        BigDecimal Change = new BigDecimal("0");
                                                                                                        BigDecimal Recieve = new BigDecimal("0");

                                                                                                        if(CustomerBalance.subtract(FinalCustomerPrice).doubleValue() >= 0){
                                                                                                            Balance = CustomerBalance.subtract(FinalCustomerPrice);
                                                                                                            Change = new BigDecimal("0");
                                                                                                            Recieve = FinalCustomerPrice;
                                                                                                        }
                                                                                                        else{
                                                                                                            Balance = new BigDecimal("0");
                                                                                                            Change = FinalCustomerPrice.subtract(CustomerBalance);
                                                                                                            Recieve = CustomerBalance;
                                                                                                        }

                                                                                                        t1.setText(obj.nameCustomer);
                                                                                                        t2.setText("المسافة المقطوعة: "+String.format("%.2f", FinalTripDistance.doubleValue())+" Km \n"+"وقت الرحلة: "+time+" min");
                                                                                                        t3.setText("عداد الرحلة: "+String.format("%.2f", PriceWithoutDisc)+" دينار بخصم "+obj.discount+"%");
                                                                                                        t5.setText("القيمة المطلوبة كاش: "+String.format("%.2f", Change.doubleValue())+" دينار");

                                                                                                        CustomerBalance = Balance;
                                                                                                        final BigDecimal AddBalance = Recieve;
                                                                                                        progressDialog.dismiss();

                                                                                                        t4.setOnClickListener(new View.OnClickListener() {
                                                                                                            @Override
                                                                                                            public void onClick(View view) {

                                                                                                                progressDialog.show();

                                                                                                                Map<String, Object> dada = new HashMap<>();
                                                                                                                dada.put("balance",Double.parseDouble(String.format(Locale.ENGLISH,"%.3f", CustomerBalance)));
                                                                                                                FirebaseFirestore.getInstance()
                                                                                                                        .collection("Users")
                                                                                                                        .document(obj.idCustomer)
                                                                                                                        .update(dada).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                                        RatingBar rate = dialog2.findViewById(R.id.rating);
                                                                                                                        double rating = rate.getRating();

                                                                                                                        Map<String, Object> ra = new HashMap<>();
                                                                                                                        ra.put("rating", FieldValue.increment(rating));
                                                                                                                        ra.put("countRating", FieldValue.increment(1));
                                                                                                                        FirebaseFirestore.getInstance()
                                                                                                                                .collection("Users")
                                                                                                                                .document(obj.idCustomer)
                                                                                                                                .update(ra).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                FirebaseFirestore.getInstance()
                                                                                                                                        .collection("Users")
                                                                                                                                        .document(obj.idCustomer)
                                                                                                                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                                                                    @Override
                                                                                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                                                                        SendNoti("عميلنا العزيز شكرا لاستخدامكم تطبيق رويال رايد, نتمنى لك يوما سعيدا", documentSnapshot.getString("token"));
                                                                                                                                        toggleButton.setChecked(true);
                                                                                                                                        OldDistance = "0";
                                                                                                                                    }
                                                                                                                                });

                                                                                                                                FirebaseFirestore.getInstance()
                                                                                                                                        .collection("Users")
                                                                                                                                        .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                                                                                        .update("balance", FieldValue.increment(AddBalance.doubleValue()));

                                                                                                                                FirebaseFirestore.getInstance()
                                                                                                                                        .collection("Trips")
                                                                                                                                        .document(map.get("tripsid").toString())
                                                                                                                                        .update("state", "StateTrip.needRatingByCustomer")
                                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                            @Override
                                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                progressDialog.dismiss();
                                                                                                                                                arrive.setText("لقد وصلت");
                                                                                                                                                canc.setText("إلغاء");
                                                                                                                                                dialog_count = 0;
                                                                                                                                                InTrip = false;
                                                                                                                                                isConnected = true;
                                                                                                                                                SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                                                                                                                                editor.putBoolean("isConnected", isConnected);
                                                                                                                                                editor.putBoolean("InTrip", InTrip);
                                                                                                                                                editor.apply();
                                                                                                                                                editor.commit();
                                                                                                                                                findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                                                                                                                findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);
                                                                                                                                                dialog2.dismiss();
                                                                                                                                            }
                                                                                                                                        });
                                                                                                                            }
                                                                                                                        });

                                                                                                                    }
                                                                                                                });
                                                                                                            }
                                                                                                        });
                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        });
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                            d.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            });
                                            d.setCancelable(false);
                                            d.show();
                                        }
                                    }
                                });

                                canc.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if(!canc.getText().toString().equals("إلغاء")){
                                            new AlertDialog.Builder(MapsActivity.this)
                                                    .setMessage("سيتم خصم مبلغ بقيمة 0.5 دينار عند قيامك بإلغاء رحلة مقبولة هل ترغب بذلك ؟")
                                                    .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                            findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);
                                                            progressDialogLoad.show();
                                                            dialog_count = 0;
                                                            InTrip = false;
                                                            isConnected = true;
                                                            SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                                            editor.putBoolean("isConnected", isConnected);
                                                            editor.putBoolean("InTrip", InTrip);
                                                            editor.apply();
                                                            editor.commit();
                                                            findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                            findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);

                                                            FirebaseFirestore.getInstance()
                                                                    .collection("driverRequests")
                                                                    .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                    .delete();

                                                            FirebaseFirestore.getInstance()
                                                                    .collection("Trips")
                                                                    .document(map.get("tripsid").toString())
                                                                    .update("state", "StateTrip.cancelByDriver");

                                                            BigDecimal bal = new BigDecimal(UserInfo_sharedPreference.getUser(MapsActivity.this).balance)
                                                                    .subtract(new BigDecimal("0.5"));
                                                            FirebaseFirestore.getInstance()
                                                                    .collection("Users")
                                                                    .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                    .update("balance", bal.doubleValue());
                                                            progressDialogLoad.dismiss();
                                                        }
                                                    }).setNegativeButton("لا", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialog.dismiss();
                                                }
                                            }).setCancelable(false).create().show();
                                        }
                                        else{

                                            AlertDialog.Builder d = new AlertDialog.Builder(MapsActivity.this);
                                            d.setCancelable(false);
                                            d.setTitle("النظام...");
                                            d.setMessage("هل تريد تأكيد الإلغاء؟");
                                            d.setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                    findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);

                                                    dialog_count = 0;
                                                    InTrip = false;
                                                    isConnected = true;
                                                    SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                                    editor.putBoolean("isConnected", isConnected);
                                                    editor.putBoolean("InTrip", InTrip);
                                                    editor.apply();
                                                    editor.commit();
                                                    findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                    findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);

                                                    progressDialogLoad.show();

                                                    FirebaseFirestore.getInstance()
                                                            .collection("driverRequests")
                                                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            progressDialogLoad.dismiss();
                                                        }
                                                    });

                                                    FirebaseFirestore.getInstance()
                                                            .collection("Trips")
                                                            .document(map.get("tripsid").toString())
                                                            .update("state", "StateTrip.rejected");
                                                }
                                            });
                                            d.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            d.show();

                                        }
                                    }
                                });
                            }
                            else if(obj.state.equals("StateTrip.needRatingByDriver")){

                                dialog_count = 1;
                                InTrip = true;
                                SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                editor.putBoolean("InTrip", InTrip);
                                editor.apply();
                                editor.commit();
                                findViewById(R.id.textView7).setVisibility(View.VISIBLE);
                                k.setText("0.000 Km");
                                m.setText("00:00:00");

                                progressDialogLoad.show();

                                final Map<String, Object> map = new HashMap<>();
                                final Map<String, Object> mini_map = new HashMap<>();
                                final Map<String, Object> mini_map2 = new HashMap<>();
                                final Map<String, Object> mini_map3 = new HashMap<>();

                                map.put("tripsid", obj.tripsid);
                                TripObjTid = obj.tripsid+"";
                                map.put("date", obj.date);
                                map.put("idCustomer", obj.idCustomer);
                                map.put("idDriver", obj.idDriver);
                                map.put("dateStart", obj.dateStart);
                                map.put("dateAcceptRequest", obj.dateAcceptRequest);
                                map.put("state", obj.state);
                                map.put("km", obj.km);
                                map.put("totalPrice", obj.totalPrice);
                                map.put("hours", obj.hours);
                                map.put("typeTrip", obj.typeTrip);
                                map.put("discount", obj.discount);
                                map.put("addressCurrent", obj.addressCurrent);

                                mini_map.put("lat", (obj.accessPoint.get("lat")));
                                mini_map.put("lng", (obj.accessPoint.get("lng")));
                                mini_map.put("addressTo", (obj.accessPoint.get("addressTo")));
                                map.put("accessPoint", mini_map);

                                mini_map2.put("lat", obj.locationCustomer.get("lat"));
                                mini_map2.put("lng", obj.locationCustomer.get("lng"));
                                map.put("locationCustomer", mini_map2);

                                mini_map3.put("lat", obj.locationDriver.get("lat"));
                                mini_map3.put("lng", obj.locationDriver.get("lng"));
                                mini_map3.put("rotateDriver", obj.locationDriver.get("rotateDriver"));
                                map.put("locationDriver", mini_map3);

                                findViewById(R.id.card_default).setVisibility(View.INVISIBLE);
                                findViewById(R.id.card_default2).setVisibility(View.VISIBLE);

                                ImageView call = findViewById(R.id.call);
                                CardView loca = findViewById(R.id.card3);
                                TextView arrive = findViewById(R.id.textView11);
                                TextView canc = findViewById(R.id.textView7);
                                TextView name = findViewById(R.id.txvType);
                                TextView locat = findViewById(R.id.txvColor);

                                name.setText(obj.nameCustomer);
                                locat.setText(obj.addressCurrent);

                                call.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:"+obj.phoneCustomer.replace("+962", "0")));
                                        startActivity(intent);
                                    }
                                });

                                loca.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                Uri.parse("http://maps.google.com/maps?saddr="+
                                                        loc.getLatitude()+","+loc.getLongitude()+
                                                        "&daddr="+obj.locationCustomer.get("lat")+","+obj.locationCustomer.get("lng")));
                                        startActivity(intent);
                                    }
                                });

                                FirebaseFirestore.getInstance()
                                        .collection("AdminDataConfig")
                                        .document("Data")
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MapsActivity.this);
                                        LayoutInflater inflater = MapsActivity.this.getLayoutInflater();
                                        builder.setView(inflater.inflate(R.layout.dialog_trip_req2, null));
                                        final androidx.appcompat.app.AlertDialog dialog2 = builder.create();
                                        ((FrameLayout) dialog2.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                        lp.copyFrom(dialog2.getWindow().getAttributes());
                                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                        lp.height = Math.round(TypedValue.applyDimension(
                                                TypedValue.COMPLEX_UNIT_DIP, 510, getResources().getDisplayMetrics()));
                                        dialog2.getWindow().setAttributes(lp);
                                        dialog2.show();
                                        dialog2.setCancelable(false);

                                        final TextView t1 = dialog2.findViewById(R.id.txvType);
                                        final TextView t2 = dialog2.findViewById(R.id.txvColor);
                                        final TextView t3 = dialog2.findViewById(R.id.txvNo);
                                        final TextView t4 = dialog2.findViewById(R.id.textView11);
                                        final TextView t5 = dialog2.findViewById(R.id.txvNo5);

                                        StartedTripAt= obj.tripsid;

                                        BigDecimal TripDistance = new BigDecimal("0");
                                        TripDistance = TripDistance.add(new BigDecimal(GetDistanceFromLatLonInKm(
                                                Double.parseDouble(obj.locationCustomer.get("lng")+""),
                                                Double.parseDouble(obj.locationCustomer.get("lat")+""),
                                                loc.getLatitude(), loc.getLongitude())));

                                        final BigDecimal FinalTripDistance = TripDistance;

                                        double base_price = 0, below_4_km = 0, between_4n5_km = 0, between_5n8_km = 0,
                                                more_8_km = 0, minute_price = 0, minimum_trip_cost = 0, driver_fee = 0;

                                        try{
                                            base_price = Double.parseDouble(documentSnapshot.getString("base_price"));
                                            below_4_km = Double.parseDouble(documentSnapshot.getString("below_4_km"));
                                            between_4n5_km = Double.parseDouble(documentSnapshot.getString("between_4n5_km"));
                                            between_5n8_km = Double.parseDouble(documentSnapshot.getString("between_5n8_km"));
                                            more_8_km = Double.parseDouble(documentSnapshot.getString("more_8_km"));
                                            minute_price = Double.parseDouble(documentSnapshot.getString("minute_price"));
                                            minimum_trip_cost = Double.parseDouble(documentSnapshot.getString("minimum_trip_cost"));
                                            driver_fee = Double.parseDouble(documentSnapshot.getString("driver_fee")); }
                                        catch (Exception ex){}

                                        long time = ((System.currentTimeMillis()/1000) - (StartedTripAt/1000))/60;
                                        StartedTripAt = 0;
                                        BigDecimal TotalTripPrice = new BigDecimal("-1");

                                        if(TripDistance.doubleValue() <= 4)
                                            TotalTripPrice = new BigDecimal(base_price)
                                                    .add(new BigDecimal(below_4_km).multiply(TripDistance))
                                                    .add(new BigDecimal(minute_price).multiply(new BigDecimal(time)));
                                        else if(TripDistance.doubleValue() > 4 && TripDistance.doubleValue() <= 5)
                                            TotalTripPrice = new BigDecimal(base_price)
                                                    .add(new BigDecimal(between_4n5_km).multiply(TripDistance))
                                                    .add(new BigDecimal(minute_price).multiply(new BigDecimal(time)));
                                        else if(TripDistance.doubleValue() > 5 && TripDistance.doubleValue() <= 8)
                                            TotalTripPrice = new BigDecimal(base_price)
                                                    .add(new BigDecimal(between_5n8_km).multiply(TripDistance))
                                                    .add(new BigDecimal(minute_price).multiply(new BigDecimal(time)));
                                        else if(TripDistance.doubleValue() > 8)
                                            TotalTripPrice = new BigDecimal(base_price)
                                                    .add(new BigDecimal(more_8_km).multiply(TripDistance))
                                                    .add(new BigDecimal(minute_price).multiply(new BigDecimal(time)));

                                        if (TotalTripPrice.doubleValue() < minimum_trip_cost)
                                            TotalTripPrice = new BigDecimal(minimum_trip_cost);

                                        final BigDecimal PriceWithoutDisc = TotalTripPrice;
                                        final BigDecimal disc = new BigDecimal(obj.discount+"").divide(new BigDecimal("100"));
                                        final BigDecimal disc2 = new BigDecimal(driver_fee).divide(new BigDecimal("100"));
                                        final BigDecimal CompanyShare = TotalTripPrice.multiply(disc2);
                                        final BigDecimal DiscountablePrice = TotalTripPrice.subtract(new BigDecimal(minimum_trip_cost));
                                        final BigDecimal CustomerPrice = DiscountablePrice.subtract(DiscountablePrice.multiply(disc));
                                        final BigDecimal AddBalancePrice = CompanyShare.subtract(DiscountablePrice.multiply(disc));
                                        final BigDecimal AddSubBalance = new BigDecimal("0").subtract(AddBalancePrice);
                                        final BigDecimal FinalCustomerPrice = CustomerPrice.add(new BigDecimal(minimum_trip_cost));

                                        double km = Double.parseDouble(String.format(Locale.ENGLISH,"%.2f", FinalTripDistance.doubleValue()));
                                        double totalPrice =Double.parseDouble(String.format(Locale.ENGLISH,"%.2f", PriceWithoutDisc.doubleValue()));

                                        Map<String, Object> ups = new HashMap<>();
                                        Map<String, Object> mini_locs = new HashMap<>();
                                        mini_locs.put("addressTo","");
                                        mini_locs.put("lat",loc.getLatitude());
                                        mini_locs.put("lng",loc.getLongitude());
                                        ups.put("accessPoint",mini_locs);
                                        ups.put("km",km);
                                        ups.put("hours", time);
                                        ups.put("totalPrice",totalPrice);
                                        FirebaseFirestore.getInstance()
                                                .collection("Trips")
                                                .document(map.get("tripsid").toString())
                                                .update(ups).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                BigDecimal Balance = new BigDecimal("0");
                                                BigDecimal Change = new BigDecimal("0");
                                                BigDecimal Recieve = new BigDecimal("0");

                                                if(CustomerBalance.subtract(FinalCustomerPrice).doubleValue() >= 0){
                                                    Balance = CustomerBalance.subtract(FinalCustomerPrice);
                                                    Change = new BigDecimal("0");
                                                    Recieve = FinalCustomerPrice;
                                                }
                                                else{
                                                    Balance = new BigDecimal("0");
                                                    Change = FinalCustomerPrice.subtract(CustomerBalance);
                                                    Recieve = CustomerBalance;
                                                }

                                                t1.setText(obj.nameCustomer);
                                                t2.setText("المسافة المقطوعة: "+String.format("%.2f", FinalTripDistance.doubleValue())+" Km \n"+"وقت الرحلة: "+time+" min");
                                                t3.setText("عداد الرحلة: "+String.format("%.2f", PriceWithoutDisc)+" دينار بخصم "+obj.discount+"%");
                                                t5.setText("القيمة المطلوبة كاش: "+String.format("%.2f", Change.doubleValue())+" دينار");

                                                CustomerBalance = Balance;
                                                final BigDecimal AddBalance = Recieve;
                                                progressDialog.dismiss();

                                                t4.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        progressDialog.show();

                                                        Map<String, Object> dada = new HashMap<>();
                                                        dada.put("balance",Double.parseDouble(String.format(Locale.ENGLISH,"%.2f", CustomerBalance)));
                                                        FirebaseFirestore.getInstance()
                                                                .collection("Users")
                                                                .document(obj.idCustomer)
                                                                .update(dada).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                RatingBar rate = dialog2.findViewById(R.id.rating);
                                                                double rating = rate.getRating();

                                                                Map<String, Object> ra = new HashMap<>();
                                                                ra.put("rating", FieldValue.increment(rating));
                                                                ra.put("countRating", FieldValue.increment(1));
                                                                FirebaseFirestore.getInstance()
                                                                        .collection("Users")
                                                                        .document(obj.idCustomer)
                                                                        .update(ra).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        FirebaseFirestore.getInstance()
                                                                                .collection("Users")
                                                                                .document(map.get("idCustomer").toString())
                                                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                SendNoti("عميلنا العزيز شكرا لاستخدامكم تطبيق رويال رايد, نتمنى لك يوما سعيدا", documentSnapshot.getString("token"));
                                                                                toggleButton.setChecked(true);
                                                                                OldDistance = "0";
                                                                            }
                                                                        });

                                                                        FirebaseFirestore.getInstance()
                                                                                .collection("Users")
                                                                                .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                                .update("balance", FieldValue.increment(AddSubBalance.doubleValue()));


                                                                        FirebaseFirestore.getInstance()
                                                                                .collection("Trips")
                                                                                .document(map.get("tripsid").toString())
                                                                                .update("state", "StateTrip.needRatingByCustomer")
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        progressDialog.dismiss();
                                                                                        arrive.setText("لقد وصلت");
                                                                                        canc.setText("إلغاء");
                                                                                        dialog_count = 0;
                                                                                        InTrip = false;
                                                                                        isConnected = true;
                                                                                        SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                                                                        editor.putBoolean("isConnected", isConnected);
                                                                                        editor.putBoolean("InTrip", InTrip);
                                                                                        editor.apply();
                                                                                        editor.commit();
                                                                                        findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                                                        findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);
                                                                                        dialog2.dismiss();
                                                                                    }
                                                                                });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                            else{
                                FirebaseFirestore.getInstance()
                                        .collection("Trips")
                                        .document(obj.tripsid+"")
                                        .addSnapshotListener(event2).remove();
                                TextView arrive = findViewById(R.id.textView11);
                                TextView canc = findViewById(R.id.textView7);
                                arrive.setText("لقد وصلت");
                                canc.setText("إلغاء");
                                dialog_count = 0;
                                InTrip = false;
                                isConnected = true;
                                SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                                editor.putBoolean("isConnected", isConnected);
                                editor.putBoolean("InTrip", InTrip);
                                editor.apply();
                                editor.commit();
                                findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);
                            }
                        }
                        else{
                            InTrip = false;
                            SharedPreferences.Editor editor = MapsActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                            editor.putBoolean("InTrip", InTrip);
                            editor.apply();
                            editor.commit();
//                            isConnected = true;
                        }
                    }
                });

            }

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {}

        @Override
        public void onProviderDisabled(@NonNull String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
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

    public void SendNoti(String note, String token){

        RequestQueue mRequestQue = Volley.newRequestQueue(MapsActivity.this);
        JSONObject json = new JSONObject();
        try {
            json.put("to", token);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", "تطبيق رويال رايد");
            notificationObj.put("body", note);
            json.put("data", notificationObj);

            String URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("Sender", "id=867383388531");
                    header.put("authorization", "key=AAAAyfQUKXM:APA91bFD2UtTOIvRiiyp56z3XDkPww6df6PR5jLk6n-bzpy17Q5DhAd4uMwFgR_QyjTSDCYBjizDFqlrGGIobtXRkySFaGMadRzTY_JM4ltOexLfPVe5Xq6VLqh4YT2ec41ehoXOCSMD");
                    return header;
                }
            };
            mRequestQue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public double GetDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2)
    {
        final int R = 6371;
        // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);
        // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        // Distance in km
        return d;
    }
    private double deg2rad(double deg)
    {
        return deg * (Math.PI / 180);
    }


    @Override
    protected void onDestroy() {
        try{
            progressDialog.dismiss();
        }
        catch (Exception ex){}

        super.onDestroy();
    }

    private void CheckForAppVersion() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("AdminDataConfig")
                .document("Data")
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                try {
                    final String url = documentSnapshot.get("DownloadLink").toString();
                    String ver = documentSnapshot.get("Version").toString();
                    String ver2 = MapsActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

                    if(!ver.equals(ver2)){
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                        LayoutInflater inflater = MapsActivity.this.getLayoutInflater();
                        builder.setView(inflater.inflate(R.layout.dialog_info15, null));
                        final AlertDialog dialog = builder.create();
                        ((FrameLayout) dialog.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                        dialog.show();
                        dialog.getWindow().setAttributes(lp);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);

                        CircleImageView im = dialog.findViewById(R.id.im);
                        im.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MapsActivity.this.finish();
                                finishAffinity();
                                dialog.dismiss();
                            }
                        });
                        Button btn = dialog.findViewById(R.id.btn2);
                        btn.setText("تحميل اخر إصدار");
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                            }
                        });
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onTaskDone(Object... values) {
        if (polyline != null)
            polyline.remove();
        polyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTrimMemory(int level) {

        if(isConnected){
            BgServiceIntent.putExtra("condition","start");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                MapsActivity.this.startForegroundService(BgServiceIntent);
             else
                startService(BgServiceIntent);

        }
        super.onTrimMemory(level);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isMyServiceRunning(FloatingService.class)){
            BgServiceIntent.putExtra("condition","close");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                MapsActivity.this.startForegroundService(BgServiceIntent);
            else
                startService(BgServiceIntent);
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public void UpdateVID(Activity c , String VID){

        Map<String,Object> map = new HashMap<>();
        map.put("VID", VID );

        if(!(UserInfo_sharedPreference.getUser(c).uid+"").equals("")
                && UserInfo_sharedPreference.getUser(c).uid != null){

            FirebaseFirestore.getInstance().collection("Users")
                    .document(UserInfo_sharedPreference.getUser(c).uid)
                    .update(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            SharedPreferences.Editor editor = c.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                            editor.putString("VID" , VID);
                            editor.apply();
                            editor.commit();
                        }
                    });
        }
    }

}