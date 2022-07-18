package com.app.alpha_ride.alpharide.ViewModel.Users.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import com.app.alpha_ride.alpharide.ApplicationLifecycleHandler;
import com.app.alpha_ride.alpharide.ClassDate;
import com.app.alpha_ride.alpharide.Data.Users.MyTripsModel;
import com.app.alpha_ride.alpharide.R;
import com.app.alpha_ride.alpharide.ViewModel.Users.Adapters.MyTripsAdapter;
import com.app.alpha_ride.alpharide.ViewModel.Users.TripsViewModel;

public class MyTripsActivity extends AppCompatActivity {

    ArrayList<MyTripsModel> myTrips ;
    public static MyTripsModel tripsObject ;
    ListView listView ;

    TextView txvDateTo,txvDateFrom;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_trips);
        init();

        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
        registerActivityLifecycleCallbacks(handler);
        registerComponentCallbacks(handler);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tripsObject = myTrips.get(i);
                startActivity(new Intent(getApplicationContext(),TripInfoWithMapActivity.class));
            }
        });
    }

    void init(){
        listView = findViewById(R.id.listView);
//        txvDateTo = findViewById(R.id.txvDateTo);
//        txvDateFrom = findViewById(R.id.txvDateFrom);
        getMyTrips();
    }

    void getMyTrips(){
        ProgressDialog p = new ProgressDialog(MyTripsActivity.this);
        p.setTitle("الرجاء الإنتظار....");
        p.setCancelable(false);
        p.show();

        TripsViewModel vm = ViewModelProviders.of(this).get(TripsViewModel.class);
        vm.getMyTrips(MyTripsActivity.this);
        vm.listOfMyTrips.observe(this, new Observer<ArrayList<MyTripsModel>>() {
            @Override
            public void onChanged(ArrayList<MyTripsModel> myTripsModels) {
                myTrips = myTripsModels;

                ArrayAdapter<MyTripsModel> adapter = new MyTripsAdapter(MyTripsActivity.this,
                        R.layout.activity_my_trips,myTrips);
                listView.setAdapter(adapter);
                p.dismiss();
            }
        });
    }

    public void onClickDateFrom(View view) {
        ClassDate d = ViewModelProviders.of(this).get(ClassDate.class);
        d.showDatePicker(MyTripsActivity.this);
        d.datePicker.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                txvDateFrom.setText(s);
            }
        });
    }

    public void onClickDateTo(View view) {
        ClassDate d = ViewModelProviders.of(this).get(ClassDate.class);
        d.showDatePicker(MyTripsActivity.this);
        d.datePicker.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                txvDateTo.setText(s);
            }
        });
    }
}