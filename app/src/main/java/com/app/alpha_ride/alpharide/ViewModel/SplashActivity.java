package com.app.alpha_ride.alpharide.ViewModel;

import static java.lang.Thread.sleep;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.alpha_ride.alpharide.ApplicationLifecycleHandler;
import com.app.alpha_ride.alpharide.Data.Users.UserInfo_sharedPreference;
import com.app.alpha_ride.alpharide.MainActivity;
import com.app.alpha_ride.alpharide.R;
import com.app.alpha_ride.alpharide.ViewModel.Users.UserViewModel;

public class SplashActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

//        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
//        registerActivityLifecycleCallbacks(handler);
//        registerComponentCallbacks(handler);


//        String msg = "Whenever you want. Wherever you are";

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
                logTo();
//            }
//        }, 3000);

//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            new Thread(new Runnable() {
//                public void run() {
//                    try {
//                        TextView tv = findViewById(R.id.intro);
//                        tv.setText("");
//                        tv.setTextColor(Color.parseColor("#AE994C"));
//                        for (int i = 0; i < msg.length(); i++) {
//                            sleep(80);
//                            tv.setText(tv.getText().toString() + msg.charAt(i));
//                        }
//                        sleep(3500);
//                        logTo();
//                    }
//                    catch (Exception e) {}
//                }
//            }).start();
//        } else {
//            new Thread(new Runnable() {
//                public void run() {
//                    try {
//                        sleep(3000);
//                        logTo();
//                    } catch (InterruptedException e) {
//                    }
//                }
//            }).start();
//        }
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
}