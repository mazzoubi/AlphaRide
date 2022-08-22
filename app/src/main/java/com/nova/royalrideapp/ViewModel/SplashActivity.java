package com.nova.royalrideapp.ViewModel;

import static java.lang.Thread.sleep;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import com.nova.royalrideapp.Data.Users.UserInfo_sharedPreference;
import com.nova.royalrideapp.MainActivity;
import com.nova.royalrideapp.ViewModel.Users.UserViewModel;

import com.nova.royalrideapp.R;


public class SplashActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        if(isLocationEnabled(SplashActivity.this))
            logTo();
        else
            new AlertDialog.Builder(SplashActivity.this)
                    .setMessage("يرجى تفعيل الموقع GPS و المحاولة مجددا..")
            .setPositiveButton("حسنا", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SplashActivity.this.finishAffinity();
                }
            }).setCancelable(false).create().show();

    }

    private void logTo() {

        try {
            if (UserInfo_sharedPreference.getUser(SplashActivity.this).uid==null||
                    UserInfo_sharedPreference.getUser(SplashActivity.this).uid.equals("")){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                }, 3000);
            }else {
                UserViewModel vm = ViewModelProviders.of(this).get(UserViewModel.class);
                vm.login2(SplashActivity.this,UserInfo_sharedPreference.getUser(SplashActivity.this).phoneNumber,
                        UserInfo_sharedPreference.getUser(SplashActivity.this).password);
            }
        }catch (Exception e){}

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

}