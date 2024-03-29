package com.nova.royalrideapp.ViewModel.Users.Cars;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.nova.royalrideapp.Data.Users.DriverRequestAccountModel;
import com.nova.royalrideapp.Data.Users.UserInfo_sharedPreference;
import com.nova.royalrideapp.Data.Users.UserModel;
import com.nova.royalrideapp.R;
import com.nova.royalrideapp.ViewModel.Users.Cars.Adapter.CarsAdapter;
import com.nova.royalrideapp.ViewModel.Users.UserViewModel;

public class MyCarsActivity extends AppCompatActivity {

    public static UserModel user;
    TextView txvType , txvModel , txvNo , txvColor ;
    TextView txvFront , txvCarL , txvDriverL ;
    ListView listView;
    ArrayList<DriverRequestAccountModel> requestAccount ;
    public static DriverRequestAccountModel requestAccountModel  ;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_cars);
        init();

//        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
//        registerActivityLifecycleCallbacks(handler);
//        registerComponentCallbacks(handler);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                requestAccountModel=requestAccount.get(i);
                if (requestAccountModel.state.equals("1")){
                    Toast.makeText(getApplicationContext(), "لايمكن تعديل المعلومات", Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(getApplicationContext(),ChangeImagesActivity.class)
                            .putExtra("from","2"));
                }

            }
        });

        txvFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageDialog a = new ImageDialog(user.frontCar);
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

    @Override
    protected void onResume() {
        super.onResume();
        getCars();
    }

    void getCars(){
        UserViewModel vm = ViewModelProviders.of(this).get(UserViewModel.class);
        vm.getCars(MyCarsActivity.this);
        vm.listOfCars.observe(this, new Observer<ArrayList<DriverRequestAccountModel>>() {
            @Override
            public void onChanged(ArrayList<DriverRequestAccountModel> driverRequestAccountModels) {
                requestAccount =driverRequestAccountModels;
                ArrayAdapter<DriverRequestAccountModel> adapter = new CarsAdapter(MyCarsActivity.this,
                        R.layout.row_my_cars,driverRequestAccountModels);
                listView.setAdapter(adapter);

                if (driverRequestAccountModels.size()<1){
                    Toast.makeText(getApplicationContext(), "لا يوجد لديك سيارات اضافية!", Toast.LENGTH_SHORT).show();
                }

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
        txvCarL = findViewById(R.id.textView11);
        txvDriverL = findViewById(R.id.textView12);

        txvType.setText("نوع المركبة: "+user.carType );
        txvModel.setText("سنة الصنع : "+user.carModel );
        txvNo.setText("رقم المركبة: "+user.numberCar );
        txvColor.setText("رقم المركبة: "+user.carColor );

        getCars();
    }

    public void onClickAddCar(View view) {

        if(requestAccount.size() >= 2)
            Toast.makeText(MyCarsActivity.this, "لا يمكن إضافة أكثر من 3 سيارات على الحساب", Toast.LENGTH_SHORT).show();
        else
            startActivity(new Intent(getApplicationContext(),AddNewCarActivity.class));
    }

    public void onClickChangeMainCar(View view) {
        if (user.stateAccount.equals("StateAccount.active")){
            Toast.makeText(getApplicationContext(), "لايمكن تعديل المعلومات", Toast.LENGTH_SHORT).show();
        }else {
            startActivity(new Intent(getApplicationContext(),ChangeImagesActivity.class)
                    .putExtra("from","1"));
        }

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
            ImageView imageView = findViewById(R.id.imageViewMain);
            try {
                Picasso.get().load(image).into(imageView);
            }catch (Exception e){}
        }
    }


}