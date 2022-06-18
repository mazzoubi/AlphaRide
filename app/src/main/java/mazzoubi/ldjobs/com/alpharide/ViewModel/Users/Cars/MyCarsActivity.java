package mazzoubi.ldjobs.com.alpharide.ViewModel.Users.Cars;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mazzoubi.ldjobs.com.alpharide.Data.Users.DriverRequestAccountModel;
import mazzoubi.ldjobs.com.alpharide.Data.Users.UserInfo_sharedPreference;
import mazzoubi.ldjobs.com.alpharide.Data.Users.UserModel;
import mazzoubi.ldjobs.com.alpharide.R;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.Cars.Adapter.CarsAdapter;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.UserViewModel;

public class MyCarsActivity extends AppCompatActivity {

    UserModel user;
    TextView txvType , txvModel , txvNo , txvColor ;

    TextView txvFront , txvEnd , txvInside , txvCarL , txvDriverL ;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cars);
        init();

        txvFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageDialog a = new ImageDialog(user.frontCar);
                a.show();
            }
        });

        txvEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageDialog a = new ImageDialog(user.endCar);
                a.show();
            }
        });

        txvInside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageDialog a = new ImageDialog(user.insideCar);
                a.show();
            }
        });

        txvCarL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageDialog a = new ImageDialog(user.drivingLicense);
                a.show();
            }
        });

        txvDriverL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageDialog a = new ImageDialog(user.driverLicense);
                a.show();
            }
        });


    }

    void getCars(){
        UserViewModel vm = ViewModelProviders.of(this).get(UserViewModel.class);
        vm.getCars(MyCarsActivity.this);
        vm.listOfCars.observe(this, new Observer<ArrayList<DriverRequestAccountModel>>() {
            @Override
            public void onChanged(ArrayList<DriverRequestAccountModel> driverRequestAccountModels) {
                ArrayAdapter<DriverRequestAccountModel> adapter = new CarsAdapter(MyCarsActivity.this,
                        R.layout.row_my_cars,driverRequestAccountModels);

                listView.setAdapter(adapter);
            }
        });

    }

    void init(){
        user= UserInfo_sharedPreference.getUser(MyCarsActivity.this);
        txvType = findViewById(R.id.txvType);
        txvModel = findViewById(R.id.txvModel);
        txvNo = findViewById(R.id.txvNo);
        txvColor = findViewById(R.id.txvColor);
        listView = findViewById(R.id.listView);

        txvFront = findViewById(R.id.textView7);
        txvEnd = findViewById(R.id.textView8);
        txvInside = findViewById(R.id.textView9);
        txvCarL = findViewById(R.id.textView11);
        txvDriverL = findViewById(R.id.textView12);

        txvType.setText(user.carType );
        txvModel.setText(user.carModel );
        txvNo.setText(user.numberCar );
        txvColor.setText(user.carColor );

        txvColor.setText(user.carColor );

        getCars();
    }

    public void onClickAddCar(View view) {
        startActivity(new Intent(getApplicationContext(),AddNewCarActivity.class));
    }


    class ImageDialog extends Dialog{
        String image ;
        public ImageDialog(String image){
            super(MyCarsActivity.this);
            this.image = image;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_image);
            ImageView imageView = findViewById(R.id.imageView);
            try {
                PhotoView photoView = new PhotoView(MyCarsActivity.this);
                Picasso.get().load(Uri.parse(image)).into(photoView);

                new android.app.AlertDialog.Builder(MyCarsActivity.this)
                        .setView(photoView)
                        .show();
            }catch (Exception e){}
        }
    }


}