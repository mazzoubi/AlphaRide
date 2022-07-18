package com.app.alpha_ride.alpharide;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.app.alpha_ride.alpharide.ViewModel.Main.MapsActivity;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        showNotification(remoteMessage.getNotification().getTitle(),
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

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, new Intent(getApplicationContext(), MapsActivity.class),
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

    public void createChannel(NotificationManager notificationManager){
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationChannel channel = new NotificationChannel("channelID","name", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Description");
        notificationManager.createNotificationChannel(channel);
    }
}