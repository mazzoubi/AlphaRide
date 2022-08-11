package com.nova.royalrideapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.nova.royalrideapp.ViewModel.Main.MapsActivity;

public class MyBackgroundService extends Service {

    LocationManager mLocationManager;
    Criteria locationCritera;
    LocationListener mLocationListener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        showNotification2("رويال رايد", "التطبيق لايزال يعمل في الخلفية");

//        locationCritera = new Criteria();
//        locationCritera.setAccuracy(Criteria.ACCURACY_FINE);
//        locationCritera.setAltitudeRequired(false);
//        locationCritera.setCostAllowed(true);
//        locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);
//
//        mLocationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(@NonNull Location location) {
////                Toast.makeText(MyBackgroundService.this, location.getLatitude()+","+location.getLatitude(), Toast.LENGTH_SHORT).show();
//            }
//        };
//
//        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);



        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Call();
    }

    public void Call(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyBackgroundService.this, ""+System.currentTimeMillis(), Toast.LENGTH_SHORT).show();
                Call();
            }
        }, 3000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void showNotification2(String heading, String description){

        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.carhorn);
        mp.start();

        String CHANNEL_ID="1234";

        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.carhorn);
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1,
                new Intent(getApplicationContext(), MapsActivity.class), PendingIntent.FLAG_IMMUTABLE);
        NotificationChannel mChannel;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setLightColor(Color.GRAY);
            mChannel.enableLights(true);
            mChannel.setDescription(CHANNEL_ID);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.setSound(soundUri, audioAttributes);

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel( mChannel );
            }
        }

        NotificationCompat.Builder status = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
        status.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(heading)
                .setContentText(description)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                .setVibrate(new long[]{0, 500, 1000})
                .setDefaults(Notification.DEFAULT_LIGHTS )
                .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +getApplicationContext().getPackageName()+"/"+R.raw.carhorn))
                .setContentIntent(pendingIntent);

        startForeground(1234, status.build());
//        mNotificationManager.notify(1234, status.build());

    }

}
