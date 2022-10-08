package com.nova.royalrideapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.nova.royalrideapp.ViewModel.Main.MapsActivity;

public class ConnectionChangeReceiver extends Service
{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {return null; }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                Toast.makeText(ConnectionChangeReceiver.this, "تم الإتصال بخوادم التطبيق", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLost(Network network) {
                Toast.makeText(ConnectionChangeReceiver.this, "الشبكة ضعيفة, تم فقدان الإتصال بالإنترنت... يرجى التأكد من شبكة الإنترنت و إعادة تشغيل البرنامج.. شكرا لتفهمك", Toast.LENGTH_LONG).show();
            }
        };

        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }

    }
}