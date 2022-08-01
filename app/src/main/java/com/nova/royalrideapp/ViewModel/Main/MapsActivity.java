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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nova.royalrideapp.Data.Users.MyTripsModel;
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
import com.nova.royalrideapp.databinding.ActivityMapsBinding;

import com.nova.royalrideapp.RequestUserPermissions;
import com.nova.royalrideapp.SimpleService;
import com.nova.royalrideapp.ViewModel.Notifications.ui.NotificationsActivity;
import com.nova.royalrideapp.ViewModel.Users.Cars.MyCarsActivity;
import com.nova.royalrideapp.ViewModel.Users.UserViewModel;
import com.nova.royalrideapp.ViewModel.Users.ui.MyTripsActivity;
import com.nova.royalrideapp.ViewModel.Users.ui.ProfileActivity;
import com.nova.royalrideapp.ViewModel.Wallet.ui.WalletActivity;


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
    TextView txvAccountState, m, k ;

    boolean isConnected = false;
    boolean DrawPoly = false;
    public static boolean InTrip = false;
    Drawable circleDrawable ;
    Polyline polyline;

    EventListener<DocumentSnapshot> event, event2;
    Intent ser_int;
    Location loc;
    long StartedTripAt = 0;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(binding.getRoot());


        mp = new MediaPlayer();
        Uri mediaPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.carhorn);

        try {
            mp.setAudioStreamType(AudioManager.STREAM_RING);
            mp.setDataSource(MapsActivity.this, mediaPath);
            mp.setLooping(true);
            mp.prepare(); }
        catch (Exception e) {}

        progressDialog = new ProgressDialog(MapsActivity.this);
        progressDialog.setTitle("النظام...");
        progressDialog.setMessage("الرجاء الإنتظار...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseFirestore.getInstance().collection("BlockUsers")
                .whereEqualTo("idUser",UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (queryDocumentSnapshots.getDocuments().size()>0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setTitle("النظام...");
                    builder.setMessage("عزيزي المستخدم, لقد تم حظرك من إستخدام التطبيق يمكنك التواصل معنا");
                    builder.setPositiveButton("تواصل", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(getApplicationContext(),ContactUsActivity.class));
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

                                    try{
                                        FirebaseFirestore.getInstance()
                                                .collection("Users")
                                                .document(getSharedPreferences("User", Context.MODE_PRIVATE).getString("uid",""))
                                                .update("token", "");
                                    }
                                    catch (Exception ex){}

                                    try{
                                        FirebaseFirestore.getInstance()
                                                .collection("locations")
                                                .document(getSharedPreferences("User", Context.MODE_PRIVATE).getString("uid",""))
                                                .delete();
                                    }
                                    catch (Exception ex){}

                                    try{
                                        Toast.makeText(MapsActivity.this, "تم تسجيل الدخول من جهاز اخر", Toast.LENGTH_SHORT).show();
                                        UserInfo_sharedPreference.logout(MapsActivity.this);
                                    }
                                    catch (Exception ex){}
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

        event = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                Intent homeIntent = new Intent(MapsActivity.this, MapsActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(homeIntent);

                String val="";
                try{ val = value.getString("idCustomer"); }
                catch (Exception ex){}

                if(val!= null && isConnected){
                    if(dialog_count == 0 && !InTrip){
                        dialog_count = 1;
                        mp.start();
                        dialog.show();
                        Snap_data = value;
                        final TextView t1 = dialog.findViewById(R.id.txvType);
                        final TextView t3 = dialog.findViewById(R.id.txvNo);
                        final TextView t4 = dialog.findViewById(R.id.txvColor);
                        final TextView t5 = dialog.findViewById(R.id.textView11);
                        final TextView t6 = dialog.findViewById(R.id.textView7);
                        final ImageView close = dialog.findViewById(R.id.close);

                        cd2 = new CountDownTimer(17000, 1000) {
                            public void onTick(long millisUntilFinished) {

                                t6.setText("الرفض ("+(millisUntilFinished/1000)+")");

                            }
                            public void onFinish() {
                                isConnected = true;
                                dialog_count = 0;
                                try{ mp.pause(); mp.seekTo(0); } catch (Exception ex){}
                                FirebaseFirestore.getInstance()
                                        .collection("driverRequests")
                                        .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                        .delete();
                                dialog.dismiss(); }
                        };
                        cd2.start();

                        t4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Geocoder coder = new Geocoder(MapsActivity.this);
                                double lng = 0;
                                double lat = 0;
                                try {
                                    ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(value.getString("currentAddress"), 1);
                                    for(Address add : adresses){
                                        lng = add.getLongitude();
                                        lat = add.getLatitude();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                        Uri.parse("http://maps.google.com/maps?saddr="+
                                                loc.getLatitude()+","+loc.getLongitude()+
                                                "&daddr="+lat+","+lng));
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
                                findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);

                                FirebaseFirestore.getInstance()
                                        .collection("driverRequests")
                                        .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                        .delete();
                                try{
                                    mp.pause();
                                    mp.seekTo(0);
                                } catch (Exception ex){}
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
                                        try{
                                            mp.pause();
                                            mp.seekTo(0);
                                        } catch (Exception ex){}
                                        dialog_count = 0;
                                        InTrip = false;
                                        isConnected = true;
                                        findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                        findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);

                                        FirebaseFirestore.getInstance()
                                                .collection("driverRequests")
                                                .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                .delete();
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
                                try{
                                    mp.pause();
                                    mp.seekTo(0);
                                } catch (Exception ex){}
                                progressDialogLoad.show();
                                findViewById(R.id.textView7).setVisibility(View.VISIBLE);
                                k.setText("0.000 Km");
                                m.setText("00:00:00");
                                InTrip = true;

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
                                mini_map.put("addressTo", ((Map<String, Object>) value.get("accessPoint")).get("addressTo")+"");
                                map.put("accessPoint", mini_map);

                                Geocoder coder = new Geocoder(MapsActivity.this);
                                double lng = 0;
                                double lat = 0;
                                try {
                                    ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(value.getString("currentAddress"), 1);
                                    for(Address add : adresses){
                                        lng = add.getLongitude();
                                        lat = add.getLatitude();
                                    }
                                } catch (Exception e) {}

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
                                                .document(map.get("tripsid")+"")
                                                .addSnapshotListener(event2);

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
                                                intent.setData(Uri.parse("tel:"+value.getString("phoneCustomer").replace("+962", "0")));
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
                                                    for(Address add : adresses){
                                                        lng = add.getLongitude();
                                                        lat = add.getLatitude();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                        Uri.parse("http://maps.google.com/maps?saddr="+
                                                                loc.getLatitude()+","+loc.getLongitude()+
                                                                "&daddr="+lat+","+lng));
                                                startActivity(intent);
                                            }
                                        });

                                        arrive.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                if(arrive.getText().toString().equals("لقد وصلت")){
                                                    progressDialogLoad.show();
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
                                                else if(arrive.getText().toString().equals("بدء الرحلة")){
                                                    DrawPoly = false;
                                                    polyline.remove();
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
                                                                        .collection("Trips")
                                                                        .document(map.get("tripsid")+"")
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
                                                                                                TypedValue.COMPLEX_UNIT_DIP, 440, getResources().getDisplayMetrics()));
                                                                                        dialog2.getWindow().setAttributes(lp);
                                                                                        dialog2.show();

                                                                                        final TextView t1 = dialog2.findViewById(R.id.txvType);
                                                                                        final TextView t2 = dialog2.findViewById(R.id.txvColor);
                                                                                        final TextView t3 = dialog2.findViewById(R.id.txvNo);
                                                                                        final TextView t4 = dialog2.findViewById(R.id.textView11);
                                                                                        final TextView t5 = dialog2.findViewById(R.id.txvNo5);

                                                                                        BigDecimal TripDistance = new BigDecimal("0");
                                                                                        for(int i=0; i<TripDistanceCalc.size()-1; i++){

                                                                                            TripDistance = TripDistance.add(new BigDecimal(GetDistanceFromLatLonInKm(
                                                                                                    TripDistanceCalc.get(i).getLatitude(), TripDistanceCalc.get(i).getLongitude(),
                                                                                                    TripDistanceCalc.get(i+1).getLatitude(), TripDistanceCalc.get(i+1).getLongitude())));

                                                                                        }

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

                                                                                        BigDecimal disc = new BigDecimal(Snap_data.get("discount").toString()).divide(new BigDecimal("100"));

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

                                                                                        if(TotalTripPrice.doubleValue() < minimum_trip_cost)
                                                                                            TotalTripPrice = new BigDecimal(minimum_trip_cost);
                                                                                        if(TotalTripPrice.subtract(TotalTripPrice.multiply(disc)).doubleValue() >= minimum_trip_cost)
                                                                                            TotalTripPrice = TotalTripPrice.subtract(TotalTripPrice.multiply(disc));

                                                                                        BigDecimal fee = TotalTripPrice.multiply(new BigDecimal(driver_fee).divide(new BigDecimal("100")));
                                                                                        BigDecimal final_fee = new BigDecimal("0").subtract(fee);
                                                                                        FirebaseFirestore.getInstance()
                                                                                                .collection("Users")
                                                                                                .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                                                .update("balance", FieldValue.increment(final_fee.doubleValue()))
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        Toast.makeText(MapsActivity.this, "تم إقتطاع مبلغ "+String.format(Locale.ENGLISH,"%.3f", final_fee.doubleValue()), Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                });

                                                                                        double km = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f", TripDistance.doubleValue()));
                                                                                        double totalPrice =Double.parseDouble(String.format(Locale.ENGLISH,"%.3f", TotalTripPrice.doubleValue()));
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
                                                                                                .update(ups);

                                                                                        t1.setText(Snap_data.getString("nameCustomer"));
                                                                                        t2.setText(String.format("%.3f", TripDistance.doubleValue())+" Km | "+time+" min");
                                                                                        if(CustomerBalance.subtract(TotalTripPrice).doubleValue() >= 0){
                                                                                            t3.setText("قيمة الرحلة: "+String.format("%.3f", TotalTripPrice.subtract(TotalTripPrice.multiply(disc)).doubleValue())+" بخصم "+Snap_data.get("discount")+"%");
                                                                                            t5.setText("تم خصم الرحلة من محفظة الراكب والمطلوب 0.0 دينار");
                                                                                            Map<String, Object> dada = new HashMap<>();
                                                                                            dada.put("balance",Double.parseDouble(String.format(Locale.ENGLISH,"%.3f", CustomerBalance.subtract(TotalTripPrice))));
                                                                                            FirebaseFirestore.getInstance()
                                                                                                    .collection("Users")
                                                                                                    .document(Snap_data.getString("idCustomer"))
                                                                                                    .update(dada); }
                                                                                        else{
                                                                                            t3.setText("قيمة الرحلة: "+String.format("%.3f", TotalTripPrice.doubleValue())+" بخصم "+Snap_data.get("discount")+"%");
                                                                                            t5.setText("سعر الرحلةالنهائي: "+String.format(Locale.ENGLISH,"%.3f", TotalTripPrice));
                                                                                        }

                                                                                        progressDialogLoad.dismiss();

                                                                                        t4.setOnClickListener(new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View view) {

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

                                                                                                        SendNoti("عميلنا العزيز شكرا لاستخدامكم تطبيق رويال رايد, نتمنى لك يوما سعيدا", documentSnapshot.getString("token"));

                                                                                                        FirebaseFirestore.getInstance()
                                                                                                                .collection("Trips")
                                                                                                                .document(map.get("tripsid").toString())
                                                                                                                .update("state", "StateTrip.needRatingByCustomer")
                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                        dialog2.dismiss();
                                                                                                                    }
                                                                                                                });
                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                });

                                                                                arrive.setText("لقد وصلت");
                                                                                canc.setText("إلغاء");
                                                                                dialog_count = 0;
                                                                                InTrip = false;
                                                                                isConnected = true;
                                                                                findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                                                findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);
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

                                                                    FirebaseFirestore.getInstance()
                                                                            .collection("Trips")
                                                                            .document(map.get("tripsid")+"")
                                                                            .addSnapshotListener(event2).remove();

                                                                    findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                                    findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);
                                                                    progressDialogLoad.show();
                                                                    dialog_count = 0;
                                                                    InTrip = false;
                                                                    isConnected = true;
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

                                                            FirebaseFirestore.getInstance()
                                                                    .collection("Trips")
                                                                    .document(map.get("tripsid")+"")
                                                                    .addSnapshotListener(event2).remove();

                                                            findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                            findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);

                                                            dialog_count = 0;
                                                            InTrip = false;
                                                            isConnected = true;
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

                        try{
                            t1.setText("إسم الراكب : "+value.getString("nameCustomer"));
                            t3.setText("خصم الرحلة : "+value.get("discount").toString());
                            t4.setText("مرجع الخريطة : "+value.getString("currentAddress"));

                        } catch (Exception ex){}

                    }
                }
                else{
                    FirebaseFirestore.getInstance()
                            .collection("driverRequests")
                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                            .delete();
                    if (dialog_count == 1){
                        isConnected = true;
                        dialog_count = 0;
                        try{
                            mp.pause();
                            mp.seekTo(0);
                        } catch (Exception ex){}
                        dialog.dismiss();
                    }
                }
            }
        };

        event2 = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String tsta="";
                try{ tsta = value.getString("state"); }
                catch (Exception ex){}
                if(tsta.contains("StateTrip.cancel")){
                    InTrip = false;
                    isConnected = true;
                    findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                    findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);
                }
            }
        };

        ser_int = new Intent(getApplicationContext(), SimpleService.class);

        EventListener<DocumentSnapshot> docsn = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String AID = Settings.Secure.getString(
                        getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                String other = value.getString("AID");
                if(!other.equals(AID) && !other.equals("")){
                    startActivity(new Intent(MapsActivity.this, MainActivity.class)
                            .putExtra("exit", "1"));
                    finish();
                }
            }
        };

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                .addSnapshotListener(docsn);

    }

    private void DrawPolyLine() {

        Geocoder coder = new Geocoder(MapsActivity.this);
        double lng = 0;
        double lat = 0;
        try {
            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(Snap_data.getString("currentAddress"), 1);
            for(Address add : adresses){
                lng = add.getLongitude();
                lat = add.getLatitude();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{PolylineOptions polylineOptions = new PolylineOptions()
                .add(new LatLng(lat, lng))
                .add(new LatLng(loc.getLatitude(), loc.getLongitude()));
            polyline = mMap.addPolyline(polylineOptions);}
        catch (Exception ex){}

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
//        registerActivityLifecycleCallbacks(handler);
//        registerComponentCallbacks(handler);

        circleDrawable= getResources().getDrawable(R.drawable.cc);
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        try{
            mMap.setMyLocationEnabled(true);
        }
        catch (Exception ex){}
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);

//        mMap.clear();
//        markerLat = loc.getLatitude();
//        markerLng = loc.getLongitude();
//        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
//
//        LatLng l =new LatLng(loc.getLatitude(), loc.getLongitude());
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l,15.0f));
//        mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
//                .icon(markerIcon));

        toggleButton = findViewById(R.id.toggleButton);
        txvAccountState = findViewById(R.id.textView10);
        m = findViewById(R.id.textView511);
        k = findViewById(R.id.textView57);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isConnected = b;
                if (b){

                    if (UserInfo_sharedPreference.getUser(MapsActivity.this).balance<=0){
                        AlertDialog.Builder builder= new AlertDialog.Builder(MapsActivity.this);
                        builder.setTitle("رويال رايد");
                        builder.setMessage("لا تمتلك رصيد كافي لاستقبال طلبات, يرجى الشحن لفك حجز الحساب");
                        builder.setPositiveButton("شحن", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(getApplicationContext(),WalletActivity.class));
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
                        builder.show(); }
                    else {
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

//                    BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
//                    mMap.clear();
//                    LatLng l =new LatLng(loc.getLatitude(), loc.getLatitude());
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l,15.0f));
//                    mMap.addMarker(new MarkerOptions()
//                            .position(new LatLng(loc.getLatitude(), loc.getLatitude()))
//                            .icon(markerIcon));

            }
        });

        setNavView();
        getUserInfo();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try{progressDialog.dismiss();}catch (Exception ex){}
            }
        }, 5000);
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

            mMap.clear();
            markerLat = location.getLatitude();
            markerLng = location.getLongitude();
            BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);

            if(loc == null)
                loc = location;

            float bearing = loc.bearingTo(location) ;

            LatLng l =new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l,15.0f));
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .icon(markerIcon)
                    .anchor(0.5f, 0.5f)
                    .rotation(bearing));

            loc = location;

            if(StartedTripAt != 0 && InTrip){
                TripDistanceCalc.add(loc);
                BigDecimal TripDistance = new BigDecimal("0");
                for(int i=0; i<TripDistanceCalc.size()-1; i++)
                    TripDistance = TripDistance.add(new BigDecimal(GetDistanceFromLatLonInKm(
                            TripDistanceCalc.get(i).getLatitude(), TripDistanceCalc.get(i).getLongitude(),
                            TripDistanceCalc.get(i+1).getLatitude(), TripDistanceCalc.get(i+1).getLongitude())));
                k.setText(String.format(Locale.ENGLISH,"%.3f", TripDistance.doubleValue())+" Km");
            }

            if(isConnected){
                updateToken();
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
                                .document(TripObjTid)//findme
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
                    FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                            .update("token", "");
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

                            MyTripsModel obj = list.get(list.size()-1).toObject(MyTripsModel.class);

                            FirebaseFirestore.getInstance()
                                    .collection("Trips")
                                    .document(obj.tripsid+"")
                                    .addSnapshotListener(event2);

                            if(obj.state.equals("StateTrip.active")){
                                dialog_count = 1;
                                InTrip = true;

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

                                                arrive.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        if(arrive.getText().toString().equals("لقد وصلت")){
                                                            progressDialogLoad.show();
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
                                                        else if(arrive.getText().toString().equals("بدء الرحلة")){
                                                            DrawPoly = false;
                                                            try{
                                                                polyline.remove();
                                                            }
                                                            catch (Exception ex){}
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
                                                                                                        TypedValue.COMPLEX_UNIT_DIP, 440, getResources().getDisplayMetrics()));
                                                                                                dialog2.getWindow().setAttributes(lp);
                                                                                                dialog2.show();

                                                                                                final TextView t1 = dialog2.findViewById(R.id.txvType);
                                                                                                final TextView t2 = dialog2.findViewById(R.id.txvColor);
                                                                                                final TextView t3 = dialog2.findViewById(R.id.txvNo);
                                                                                                final TextView t4 = dialog2.findViewById(R.id.textView11);
                                                                                                final TextView t5 = dialog2.findViewById(R.id.txvNo5);

                                                                                                BigDecimal TripDistance = new BigDecimal("0");
                                                                                                TripDistance = TripDistance.add(new BigDecimal(GetDistanceFromLatLonInKm(
                                                                                                        Double.parseDouble(obj.locationCustomer.get("lng")+""), Double.parseDouble(obj.locationCustomer.get("lat")+""),
                                                                                                        loc.getLatitude(), loc.getLongitude())));

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

                                                                                                BigDecimal disc = new BigDecimal(obj.discount+"").divide(new BigDecimal("100"));

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

                                                                                                if(TotalTripPrice.doubleValue() < minimum_trip_cost)
                                                                                                    TotalTripPrice = new BigDecimal(minimum_trip_cost);
                                                                                                if(TotalTripPrice.subtract(TotalTripPrice.multiply(disc)).doubleValue() >= minimum_trip_cost)
                                                                                                    TotalTripPrice = TotalTripPrice.subtract(TotalTripPrice.multiply(disc));

                                                                                                BigDecimal fee = TotalTripPrice.multiply(new BigDecimal(driver_fee).divide(new BigDecimal("100")));
                                                                                                BigDecimal final_fee = new BigDecimal("0").subtract(fee);
                                                                                                FirebaseFirestore.getInstance()
                                                                                                        .collection("Users")
                                                                                                        .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                                                        .update("balance", FieldValue.increment(final_fee.doubleValue()))
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                Toast.makeText(MapsActivity.this, "تم إقتطاع مبلغ "+String.format(Locale.ENGLISH,"%.3f", final_fee.doubleValue()), Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        });

                                                                                                double km = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f", TripDistance.doubleValue()));
                                                                                                double totalPrice =Double.parseDouble(String.format(Locale.ENGLISH,"%.3f", TotalTripPrice.doubleValue()));
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
                                                                                                        .update(ups);

                                                                                                t1.setText(obj.nameCustomer);
                                                                                                t2.setText(String.format("%.3f", TripDistance.doubleValue())+" Km | "+time+" min");
                                                                                                if(CustomerBalance.subtract(TotalTripPrice).doubleValue() >= 0){
                                                                                                    t3.setText("قيمة الرحلة: "+String.format("%.3f", TotalTripPrice.subtract(TotalTripPrice.multiply(disc)).doubleValue())+" بخصم "+obj.discount+"%");
                                                                                                    t5.setText("تم خصم الرحلة من محفظة الراكب والمطلوب 0.0 دينار");
                                                                                                    Map<String, Object> dada = new HashMap<>();
                                                                                                    dada.put("balance",Double.parseDouble(String.format(Locale.ENGLISH,"%.3f", CustomerBalance.subtract(TotalTripPrice))));
                                                                                                    FirebaseFirestore.getInstance()
                                                                                                            .collection("Users")
                                                                                                            .document(obj.idCustomer)
                                                                                                            .update(dada); }
                                                                                                else{
                                                                                                    t3.setText("قيمة الرحلة: "+String.format("%.3f", TotalTripPrice.doubleValue())+" بخصم "+obj.discount+"%");
                                                                                                    t5.setText("سعر الرحلةالنهائي: "+String.format(Locale.ENGLISH,"%.3f", TotalTripPrice));
                                                                                                }

                                                                                                progressDialogLoad.dismiss();

                                                                                                t4.setOnClickListener(new View.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(View view) {

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
                                                                                                                        .collection("Trips")
                                                                                                                        .document(map.get("tripsid").toString())
                                                                                                                        .update("state", "StateTrip.needRatingByCustomer")
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                dialog2.dismiss();
                                                                                                                            }
                                                                                                                        });
                                                                                                            }
                                                                                                        });
                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        });

                                                                                        arrive.setText("لقد وصلت");
                                                                                        canc.setText("إلغاء");
                                                                                        dialog_count = 0;
                                                                                        InTrip = false;
                                                                                        isConnected = true;
                                                                                        findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                                                        findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);
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
                                findViewById(R.id.textView7).setVisibility(View.VISIBLE);
                                k.setText("0.000 Km");
                                m.setText("00:00:00");

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

                                arrive.setText("إنهاء الرحلة");
                                StartedTripAt = obj.tripsid;
                                TripDistanceCalc = new ArrayList<>();
                                k.setText("0.0 Km");
                                canc.setText("إلغاء");
                                canc.setVisibility(View.INVISIBLE);
                                try{cd.cancel();} catch (Exception ex){}

                                progressDialogLoad.dismiss();

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
                                                                                        TypedValue.COMPLEX_UNIT_DIP, 440, getResources().getDisplayMetrics()));
                                                                                dialog2.getWindow().setAttributes(lp);
                                                                                dialog2.show();

                                                                                final TextView t1 = dialog2.findViewById(R.id.txvType);
                                                                                final TextView t2 = dialog2.findViewById(R.id.txvColor);
                                                                                final TextView t3 = dialog2.findViewById(R.id.txvNo);
                                                                                final TextView t4 = dialog2.findViewById(R.id.textView11);
                                                                                final TextView t5 = dialog2.findViewById(R.id.txvNo5);

                                                                                BigDecimal TripDistance = new BigDecimal("0");
                                                                                TripDistance = TripDistance.add(new BigDecimal(GetDistanceFromLatLonInKm(
                                                                                        Double.parseDouble(obj.locationCustomer.get("lng")+""), Double.parseDouble(obj.locationCustomer.get("lat")+""),
                                                                                        loc.getLatitude(), loc.getLongitude())));

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

                                                                                BigDecimal disc = new BigDecimal(obj.discount+"").divide(new BigDecimal("100"));

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

                                                                                if(TotalTripPrice.doubleValue() < minimum_trip_cost)
                                                                                    TotalTripPrice = new BigDecimal(minimum_trip_cost);
                                                                                if(TotalTripPrice.subtract(TotalTripPrice.multiply(disc)).doubleValue() >= minimum_trip_cost)
                                                                                    TotalTripPrice = TotalTripPrice.subtract(TotalTripPrice.multiply(disc));

                                                                                BigDecimal fee = TotalTripPrice.multiply(new BigDecimal(driver_fee).divide(new BigDecimal("100")));
                                                                                BigDecimal final_fee = new BigDecimal("0").subtract(fee);
                                                                                FirebaseFirestore.getInstance()
                                                                                        .collection("Users")
                                                                                        .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                                                                                        .update("balance", FieldValue.increment(final_fee.doubleValue()))
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                Toast.makeText(MapsActivity.this, "تم إقتطاع مبلغ "+String.format(Locale.ENGLISH,"%.3f", final_fee.doubleValue()), Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });

                                                                                double km = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f", TripDistance.doubleValue()));
                                                                                double totalPrice =Double.parseDouble(String.format(Locale.ENGLISH,"%.3f", TotalTripPrice.doubleValue()));
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
                                                                                        .update(ups);

                                                                                t1.setText(obj.nameCustomer);
                                                                                t2.setText(String.format("%.3f", TripDistance.doubleValue())+" Km | "+time+" min");
                                                                                if(CustomerBalance.subtract(TotalTripPrice).doubleValue() >= 0){
                                                                                    t3.setText("قيمة الرحلة: "+String.format("%.3f", TotalTripPrice.subtract(TotalTripPrice.multiply(disc)).doubleValue())+" بخصم "+obj.discount+"%");
                                                                                    t5.setText("تم خصم الرحلة من محفظة الراكب والمطلوب 0.0 دينار");
                                                                                    Map<String, Object> dada = new HashMap<>();
                                                                                    dada.put("balance",Double.parseDouble(String.format(Locale.ENGLISH,"%.3f", CustomerBalance.subtract(TotalTripPrice))));
                                                                                    FirebaseFirestore.getInstance()
                                                                                            .collection("Users")
                                                                                            .document(obj.idCustomer)
                                                                                            .update(dada); }
                                                                                else{
                                                                                    t3.setText("قيمة الرحلة: "+String.format("%.3f", TotalTripPrice.doubleValue())+" بخصم "+obj.discount+"%");
                                                                                    t5.setText("سعر الرحلةالنهائي: "+String.format(Locale.ENGLISH,"%.3f", TotalTripPrice));
                                                                                }

                                                                                progressDialogLoad.dismiss();

                                                                                t4.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View view) {

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
                                                                                                        .collection("Trips")
                                                                                                        .document(map.get("tripsid").toString())
                                                                                                        .update("state", "StateTrip.needRatingByCustomer")
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                dialog2.dismiss();
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                });
                                                                            }
                                                                        });

                                                                        arrive.setText("لقد وصلت");
                                                                        canc.setText("إلغاء");
                                                                        dialog_count = 0;
                                                                        InTrip = false;
                                                                        isConnected = true;
                                                                        findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                                                        findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);
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
                                                TypedValue.COMPLEX_UNIT_DIP, 440, getResources().getDisplayMetrics()));
                                        dialog2.getWindow().setAttributes(lp);
                                        dialog2.show();

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

                                        BigDecimal disc = new BigDecimal(obj.discount+"").divide(new BigDecimal("100"));

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

                                        if(TotalTripPrice.doubleValue() < minimum_trip_cost)
                                            TotalTripPrice = new BigDecimal(minimum_trip_cost);
                                        if(TotalTripPrice.subtract(TotalTripPrice.multiply(disc)).doubleValue() >= minimum_trip_cost)
                                            TotalTripPrice = TotalTripPrice.subtract(TotalTripPrice.multiply(disc));

                                        BigDecimal fee = TotalTripPrice.multiply(new BigDecimal(driver_fee).divide(new BigDecimal("100")));
                                        BigDecimal final_fee = new BigDecimal("0").subtract(fee);

                                        double km = Double.parseDouble(String.format(Locale.ENGLISH,"%.3f", TripDistance.doubleValue()));
                                        double totalPrice =Double.parseDouble(String.format(Locale.ENGLISH,"%.3f", TotalTripPrice.doubleValue()));
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
                                                .update(ups);

                                        t1.setText(obj.nameCustomer);
                                        t2.setText(String.format("%.3f", TripDistance.doubleValue())+" Km | "+time+" min");
                                        if(CustomerBalance.subtract(TotalTripPrice).doubleValue() >= 0){
                                            t3.setText("قيمة الرحلة: "+String.format("%.3f", TotalTripPrice.subtract(TotalTripPrice.multiply(disc)).doubleValue())+" بخصم "+obj.discount+"%");
                                            t5.setText("تم خصم الرحلة من محفظة الراكب والمطلوب 0.0 دينار");
                                            Map<String, Object> dada = new HashMap<>();
                                            dada.put("balance",Double.parseDouble(String.format(Locale.ENGLISH,"%.3f", CustomerBalance.subtract(TotalTripPrice))));
                                            FirebaseFirestore.getInstance()
                                                    .collection("Users")
                                                    .document(obj.idCustomer)
                                                    .update(dada); }
                                        else{
                                            t3.setText("قيمة الرحلة: "+String.format("%.3f", TotalTripPrice.doubleValue())+" بخصم "+obj.discount+"%");
                                            t5.setText("سعر الرحلةالنهائي: "+String.format(Locale.ENGLISH,"%.3f", TotalTripPrice));
                                        }

                                        progressDialogLoad.dismiss();

                                        t4.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

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
                                                                .collection("Trips")
                                                                .document(map.get("tripsid").toString())
                                                                .update("state", "StateTrip.needRatingByCustomer")
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        dialog2.dismiss();
                                                                    }
                                                                });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });

                                arrive.setText("لقد وصلت");
                                canc.setText("إلغاء");
                                dialog_count = 0;
                                InTrip = false;
                                isConnected = true;
                                findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);
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
                                findViewById(R.id.card_default).setVisibility(View.VISIBLE);
                                findViewById(R.id.card_default2).setVisibility(View.INVISIBLE);
                            }
                        }
                        else{
                            InTrip = false;
                            isConnected = true;
                        }
                    }
                });

                FirebaseFirestore.getInstance()
                        .collection("locations")
                        .document(UserInfo_sharedPreference.getUser(MapsActivity.this).uid)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String iduser = "";
                        try{
                            iduser = documentSnapshot.getString("idUser");
                        } catch (Exception ex){}

                        if(iduser != null && !iduser.equals("")){
                            toggleButton.setChecked(true);
                            InTrip = false;
                            isConnected = true;
                        }

                    }
                });
            }

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
            json.put("notification", notificationObj);

            String URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,null, null
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
    protected void onResume() {
        super.onResume();
        if (UserInfo_sharedPreference.getUser(MapsActivity.this).balance<=0){
            AlertDialog.Builder builder= new AlertDialog.Builder(MapsActivity.this);
            builder.setTitle("رويال رايد");
            builder.setMessage("لا تمتلك رصيد كافي لاستقبال طلبات, يرجى الشحن لفك حجز الحساب");
            builder.setPositiveButton("شحن", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(getApplicationContext(),WalletActivity.class));
                }
            });
            builder.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.setCancelable(false);
            builder.show();
        }


    }

}