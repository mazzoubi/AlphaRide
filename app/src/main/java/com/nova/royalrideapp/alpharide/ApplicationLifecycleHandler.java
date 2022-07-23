package com.nova.royalrideapp.alpharide;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.nova.royalrideapp.alpharide.Data.Users.UserInfo_sharedPreference;


//com.app.alpha_ride
//com.nova.royalrideapp

public class ApplicationLifecycleHandler implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    private static final String TAG = ApplicationLifecycleHandler.class.getSimpleName();
    private static boolean isInBackground = false;

    Intent ser_int;

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {

        if (UserInfo_sharedPreference.getUser(activity).uid==null||
                UserInfo_sharedPreference.getUser(activity).uid.equals("")){

        }else {
            ser_int = new Intent(activity, SimpleService.class);
            activity.stopService(ser_int);
        }

        if(isInBackground){
            Log.d(TAG, "app went to foreground");
            isInBackground = false;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ser_int = new Intent(activity, SimpleService.class);
//        activity.startService(ser_int);
    }

    @Override
    public void onActivityStopped(Activity activity) {
//        ser_int = new Intent(activity, SimpleService.class);
//        activity.stopService(ser_int);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ser_int = new Intent(activity, SimpleService.class);
        activity.stopService(ser_int);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {

    }

    @Override
    public void onLowMemory() {
    }

    @Override
    public void onTrimMemory(int i) {
        if(i == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN){
            Log.d(TAG, "app went to background");
            isInBackground = true;
        }
    }
}
