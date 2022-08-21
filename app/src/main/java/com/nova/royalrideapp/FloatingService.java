package com.nova.royalrideapp;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.nova.royalrideapp.Data.Users.UserInfo_sharedPreference;
import com.nova.royalrideapp.ViewModel.Main.MapsActivity;
import com.siddharthks.bubbles.FloatingBubbleConfig;
import com.siddharthks.bubbles.FloatingBubbleService;
import com.siddharthks.bubbles.FloatingBubbleTouchListener;

import java.util.HashMap;
import java.util.Map;

public class FloatingService extends FloatingBubbleService {

    LocationManager mLocationManager;
    Criteria locationCritera;
    LocationListener lsn;
    String flag;
    String providerName;
    Intent act;
    int counter = 0;
    int WithIntent = 0;

    @Override
    protected FloatingBubbleConfig getConfig() {
        View convertView = LayoutInflater.from(MapsActivity.main).inflate(R.layout.buub, null, false);
        ImageView key = convertView.findViewById(R.id.key);


        ViewTreeObserver vto = convertView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(counter++ == 1){
                    Intent homeIntent = new Intent(MapsActivity.main, MapsActivity.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(homeIntent);
                }
            }
        });

        return new FloatingBubbleConfig.Builder()
                .bubbleIcon(getDrawable(R.drawable.logo7))
                .removeBubbleIcon(getDrawable(R.drawable.close2))
                .bubbleIconDp(64)
                .removeBubbleIconDp(64)
                .paddingDp(4)
                .borderRadiusDp(4)
                .physicsEnabled(false)
                .expandableColor(Color.WHITE)
                .triangleColor(Color.WHITE)
                .expandableView(convertView)
                .removeBubbleAlpha(0.75f)
                .build();
    }

    @Override
    protected boolean onGetIntent(@NonNull Intent intent) {

        act = intent;
        try{ flag = act.getStringExtra("condition"); }
        catch (Exception ex) { flag = ""; }

        WithIntent = 1;
        if (flag.equals("close")) StopWorkingMF();
        else StartWorkingMF();

        return super.onGetIntent(intent);
    }

    private void StartWorkingMF() {
        WithIntent = 0;
        InitLsnr();
        showNotification2("رويال رايد", "لا يزال التطبيق يعمل في الخلف");

        locationCritera = new Criteria();
        locationCritera.setAccuracy(Criteria.ACCURACY_FINE);
        locationCritera.setAltitudeRequired(false);
        locationCritera.setCostAllowed(true);
        locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        providerName = mLocationManager.getBestProvider(locationCritera, true);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lsn);

        //GetData();

    }

    private void InitLsnr() {

        lsn = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                if(MapsActivity.main.getSharedPreferences("User", Context.MODE_PRIVATE).getBoolean("isConnected", false)){
                    UploadLocationData(location);
                }

            }
        };

    }

    private void UploadLocationData(Location location) {

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> mini_map = new HashMap<>();
        String geohash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(location.getLatitude(), location.getLongitude()));
        GeoPoint geopoint = new GeoPoint(location.getLatitude(), location.getLongitude());

        mini_map.put("geohash", geohash);
        mini_map.put("geopoint", geopoint);

        if(!MapsActivity.main.getSharedPreferences("User", Context.MODE_PRIVATE).getBoolean("InTrip", false)) map.put("available",true);
        else map.put("available",false);
        map.put("idUser", UserInfo_sharedPreference.getUser(MapsActivity.main).uid);
        map.put("position", mini_map);
        map.put("timems", System.currentTimeMillis());

        try{
            FirebaseFirestore.getInstance()
                    .collection("locations")
                    .document(map.get("idUser").toString())
                    .update(map);
        }
        catch (Exception ex){}

    }

    public void showNotification2(String heading, String description){

        String CHANNEL_ID="1234";

        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.carhorn);
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1,
                new Intent(getApplicationContext(), MapsActivity.class), PendingIntent.FLAG_IMMUTABLE);
        NotificationChannel mChannel;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setLightColor(Color.GRAY);
            mChannel.enableLights(true);
            mChannel.setDescription(CHANNEL_ID);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
//            mChannel.setSound(soundUri, audioAttributes);

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel( mChannel );
            }
        }

        NotificationCompat.Builder status = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
        status.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logo7)
                .setContentTitle(heading)
                .setContentText(description)
                .setTicker(null)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                .setVibrate(new long[]{0, 500, 1000})
                .setDefaults(Notification.DEFAULT_LIGHTS)
//                .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +getApplicationContext().getPackageName()+"/"+R.raw.carhorn))
                .setContentIntent(pendingIntent);

        startForeground(1234, status.build());

    }

    private void StopWorkingMF() {

        try{mLocationManager.removeUpdates(lsn);}
        catch (Exception ex){}

        stopSelf();

    }

    @Override
    public void onDestroy() {

        if(WithIntent == 0){
            try{
                FirebaseFirestore.getInstance()
                        .collection("locations")
                        .document(UserInfo_sharedPreference.getUser(MapsActivity.main).uid)
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FloatingService.super.onDestroy();
                        try{MapsActivity.main.finishAffinity();}
                        catch (Exception ex){}
                    }
                });
            }
            catch (Exception ex){}
        }
        else
            FloatingService.super.onDestroy();
    }
}
