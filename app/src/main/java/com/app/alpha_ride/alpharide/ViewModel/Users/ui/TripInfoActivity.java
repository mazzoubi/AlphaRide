package com.app.alpha_ride.alpharide.ViewModel.Users.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import com.app.alpha_ride.alpharide.ApplicationLifecycleHandler;
import com.app.alpha_ride.alpharide.Data.Users.UserInfo_sharedPreference;
import com.app.alpha_ride.alpharide.Data.Users.UserModel;
import com.app.alpha_ride.alpharide.R;

public class TripInfoActivity extends AppCompatActivity {

    TextView txvPrice, txvDriverName , txvCarDetail , txvRate ;
    CircleImageView imgDriver ;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_trip_info);
        init();

//        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
//        registerActivityLifecycleCallbacks(handler);
//        registerComponentCallbacks(handler);

    }

    void init(){
        txvPrice = findViewById(R.id.txvPrice);
        txvDriverName = findViewById(R.id.txvDriverName);
        txvCarDetail = findViewById(R.id.txvCarDetail);
        txvRate = findViewById(R.id.txvRate);
        imgDriver = findViewById(R.id.imgDriver);
        UserModel a = UserInfo_sharedPreference.getUser(TripInfoActivity.this);
        txvPrice.setText(MyTripsActivity.tripsObject.totalPrice+" JD");
        txvDriverName.setText(a.fullName);
        txvRate.setText(a.rating+"");
        txvCarDetail.setText(a.carType+" "+a.carColor +" "+a.carModel + " " + a.numberCar);

        try {
            Picasso.get().load(a.imageProfile).into(imgDriver);
        }catch (Exception e){}
    }
}