package com.nova.royalrideapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.nova.royalrideapp.Data.Users.UserInfo_sharedPreference;
import com.nova.royalrideapp.ViewModel.Main.ContactUsActivity;
import com.nova.royalrideapp.ViewModel.Users.UserViewModel;
import com.nova.royalrideapp.ViewModel.Users.ui.LoginActivity;
import com.nova.royalrideapp.ViewModel.Users.ui.RegisterActivity;

import com.nova.royalrideapp.R;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
//        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
//        registerActivityLifecycleCallbacks(handler);
//        registerComponentCallbacks(handler);

        try{
            if(getIntent().getStringExtra("exit").equals("1")){
                SharedPreferences.Editor editor = MainActivity.this.getSharedPreferences("User", Context.MODE_PRIVATE).edit();

                editor.putBoolean("emailVerified" , false );
                editor.putBoolean("usePassword" , false );
                editor.putFloat("rating" , 0);

                editor.putString("uid", "" );
                editor.putString("carColor", "" );
                editor.putString("carModel", "" );
                editor.putString("carType", "" );
                editor.putString("email", "" );
                editor.putString("fullName", "" );
                editor.putString("imageProfile", "" );
                editor.putString("emailFacebook", "" );
                editor.putString("numberCar", "" );
                editor.putString("phoneNumber", "" );
                editor.putString("stateAccount", "" );
                editor.putString("typeUser", "" );
                editor.putString("password", "" );

                editor.putInt("balance",0 );
                editor.putInt("countRating",0 );
                editor.putInt("countTrips",0 );
                editor.putInt("points",0 );


                editor.putString("driverLicense" , "");
                editor.putString("drivingLicense" , "");
                editor.putString("yourPhoto" , "");
                editor.putString("endCar" , "");
                editor.putString("insideCar" , "");
                editor.putString("frontCar" , "");
                editor.putString("token" , "");
                editor.putString("AID" , "");

                editor.clear();
                editor.commit();
                editor.apply();
            }
        } catch (Exception ex){}

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    1); }

        try {
            if (UserInfo_sharedPreference.getUser(MainActivity.this).uid==null||
                    UserInfo_sharedPreference.getUser(MainActivity.this).uid.equals("")){

            }else {
                UserViewModel vm = ViewModelProviders.of(this).get(UserViewModel.class);
                vm.login(MainActivity.this,UserInfo_sharedPreference.getUser(MainActivity.this).phoneNumber,
                        UserInfo_sharedPreference.getUser(MainActivity.this).password);
            }
        }catch (Exception e){}
    }

    public void Login(View view) {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
    public void Register(View view) {
        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
    }

    @Override
    public void onBackPressed() {
        MainActivity.this.finishAffinity();
    }

    public void support(View view) {
        startActivity(new Intent(MainActivity.this, ContactUsActivity.class));
    }

    public void face(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://web.facebook.com/RoyalRideJo")));
    }

    public void insta(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/royalridejo")));
    }

    public void twit(View view) {
//        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://web.facebook.com/RoyalRideJo")));
    }
}