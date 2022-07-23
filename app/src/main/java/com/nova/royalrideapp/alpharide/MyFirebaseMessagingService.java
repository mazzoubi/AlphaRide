package com.nova.royalrideapp.alpharide;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.nova.alpha_ride.alpharide.R;
import com.nova.royalrideapp.alpharide.ViewModel.Main.MapsActivity;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        showNotification2(remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody());

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        UploadToken(s); }

    private void UploadToken(String token) {

        SharedPreferences.Editor editor = getSharedPreferences("emp",MODE_PRIVATE).edit();
        editor.putString("token", token);
        editor.putString("new_token", "yes");
        editor.apply(); }

    public void showNotification(String heading, String description){

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1,
                new Intent(getApplicationContext(), MapsActivity.class),
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.carhorn);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),"channelID")
                .setSmallIcon(R.drawable.logo5)
                .setContentTitle(heading)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                .setSound(sound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        createChannel(notificationManager);

        notificationManager.notify(2, notificationBuilder.build());


    }

    public void showNotification2(String heading, String description){

        String CHANNEL_ID="1234";

        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.carhorn);
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1,
                new Intent(getApplicationContext(), MapsActivity.class), PendingIntent.FLAG_ONE_SHOT);
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
                //.setOnlyAlertOnce(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(description)
                .setVibrate(new long[]{0, 500, 1000})
                .setDefaults(Notification.DEFAULT_LIGHTS )
                .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +getApplicationContext().getPackageName()+"/"+R.raw.carhorn))
                .setContentIntent(pendingIntent);

        mNotificationManager.notify(123, status.build());

    }

    public void createChannel(NotificationManager notificationManager){
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationChannel channel = new NotificationChannel("channelID","name", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Description");
        notificationManager.createNotificationChannel(channel);
    }
}