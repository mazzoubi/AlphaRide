package com.app.alpha_ride.alpharide.ViewModel.Users.Cars.Adapter;

import android.app.Activity;
import android.app.Dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.app.alpha_ride.alpharide.Data.Users.DriverRequestAccountModel;
import com.app.alpha_ride.alpharide.R;


public class CarsAdapter extends ArrayAdapter<DriverRequestAccountModel> {

    DriverRequestAccountModel user ;
    Activity c ;
    public CarsAdapter(Activity context, int view, ArrayList<DriverRequestAccountModel> arrayList){
        super(context,view,arrayList);
        this.c = context;
    }


    TextView txvType , txvModel , txvNo , txvColor ;

    TextView txvFront, txvCarL , txvDriverL,txvCarState ;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
        View myView = layoutInflater.inflate(R.layout.row_my_cars,parent,false);
        user = getItem(position);

        txvType = myView.findViewById(R.id.txvType);
        txvModel = myView.findViewById(R.id.txvModel);
        txvNo = myView.findViewById(R.id.txvNo);
        txvColor = myView.findViewById(R.id.txvColor);

        txvFront = myView.findViewById(R.id.textView7);
        txvCarL = myView.findViewById(R.id.textView11);
        txvDriverL = myView.findViewById(R.id.textView12);
        txvCarState = myView.findViewById(R.id.txvColor3);

        txvType.setText("نوع المركبة: "+user.typeCar );
        txvModel.setText("سنة الصنع : "+user.modelCar );
        txvNo.setText("رقم المركبة: "+user.numberCar );
        txvColor.setText("رقم المركبة: "+user.colorCar );

        if (user.state.equals("0")){
            txvCarState.setText("حالة الموافقة: في إنتظار الموافقة من الادمن");
            txvCarState.setTextColor(Color.YELLOW);
        }else if (user.state.equals("1")){
            txvCarState.setText("حالة الموافقة: تمت الموافقة من الادمن");
            txvCarState.setTextColor(Color.GREEN);
        }else {
            txvCarState.setText("حالة الموافقة: تمت الرفض من الادمن");
            txvCarState.setTextColor(Color.RED);
        }

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



        return myView ;
    }


    class ImageDialog extends Dialog {
        String image ;
        public ImageDialog(String image){
            super(c);
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