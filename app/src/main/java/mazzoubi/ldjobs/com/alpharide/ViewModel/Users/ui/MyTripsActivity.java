package mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import mazzoubi.ldjobs.com.alpharide.Data.Users.MyTripsModel;
import mazzoubi.ldjobs.com.alpharide.R;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.Adapters.MyTripsAdapter;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.TripsViewModel;

public class MyTripsActivity extends AppCompatActivity {

    ArrayList<MyTripsModel> myTrips ;
    ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);
        init();
    }

    void init(){
        listView = findViewById(R.id.listView);
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
}