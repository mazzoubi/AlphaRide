package mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import mazzoubi.ldjobs.com.alpharide.ApplicationLifecycleHandler;
import mazzoubi.ldjobs.com.alpharide.Data.Users.UserInfo_sharedPreference;
import mazzoubi.ldjobs.com.alpharide.Data.Users.UserModel;
import mazzoubi.ldjobs.com.alpharide.R;
import mazzoubi.ldjobs.com.alpharide.databinding.ActivityTripInfoWithMapBinding;
import mazzoubi.ldjobs.com.alpharide.directionhelpers.FetchURL;
import mazzoubi.ldjobs.com.alpharide.directionhelpers.TaskLoadedCallback;


public class TripInfoWithMapActivity extends FragmentActivity implements OnMapReadyCallback , TaskLoadedCallback {

    private GoogleMap mMap;
    private ActivityTripInfoWithMapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        binding = ActivityTripInfoWithMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
        registerActivityLifecycleCallbacks(handler);
        registerComponentCallbacks(handler);

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng customerLatlng = new LatLng(Double.parseDouble(MyTripsActivity.tripsObject.locationCustomer.get("lat")+""),
                Double.parseDouble(MyTripsActivity.tripsObject.locationCustomer.get("lng")+""));
        MarkerOptions m = new MarkerOptions().position(customerLatlng).title("موقع الإنطلاق");
        mMap.addMarker(m);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(customerLatlng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(customerLatlng,17.0f));

        if (Double.parseDouble(MyTripsActivity.tripsObject.accessPoint.get("lat")+"")==0){
            LatLng driverLatlng = new LatLng(Double.parseDouble(MyTripsActivity.tripsObject.locationDriver.get("lat")+""),
                    Double.parseDouble(MyTripsActivity.tripsObject.locationDriver.get("lng")+""));
            MarkerOptions m1 = new MarkerOptions().position(driverLatlng).title("موقع السائق");
            mMap.addMarker(m1);
            new FetchURL(TripInfoWithMapActivity.this).execute(getUrl(m.getPosition(),
                    m1.getPosition(), "driving"), "driving");

        }else {

            LatLng accessPointLatlng = new LatLng(Double.parseDouble(MyTripsActivity.tripsObject.accessPoint.get("lat")+""),
                    Double.parseDouble(MyTripsActivity.tripsObject.accessPoint.get("lng")+""));

            MarkerOptions m1 = new MarkerOptions().position(accessPointLatlng).title("موقع الوصول");
            mMap.addMarker(m1);

            new FetchURL(TripInfoWithMapActivity.this).execute(getUrl(m.getPosition(),
                    m1.getPosition(), "driving"), "driving");
        }


        init();
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    TextView txvPrice, txvDriverName , txvCarDetail , txvRate ;
    CircleImageView imgDriver ;

    void init(){
        txvPrice = findViewById(R.id.txvPrice);
        txvDriverName = findViewById(R.id.txvDriverName);
        txvCarDetail = findViewById(R.id.txvCarDetail);
        txvRate = findViewById(R.id.txvRate);
        imgDriver = findViewById(R.id.imgDriver);
        UserModel a = UserInfo_sharedPreference.getUser(TripInfoWithMapActivity.this);
        txvPrice.setText(MyTripsActivity.tripsObject.totalPrice+" JD");
        txvDriverName.setText(a.fullName);
        txvRate.setText(a.rating+"");
        txvCarDetail.setText(a.carType+" "+a.carColor +" "+a.carModel + " " + a.numberCar);

        try {
            Picasso.get().load(a.imageProfile).into(imgDriver);
        }catch (Exception e){}
    }
}