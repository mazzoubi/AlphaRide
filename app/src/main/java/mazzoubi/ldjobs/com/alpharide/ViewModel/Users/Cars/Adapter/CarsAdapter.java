package mazzoubi.ldjobs.com.alpharide.ViewModel.Users.Cars.Adapter;

import android.app.Activity;
import android.app.Dialog;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mazzoubi.ldjobs.com.alpharide.Data.Users.DriverRequestAccountModel;
import mazzoubi.ldjobs.com.alpharide.Data.Users.UserInfo_sharedPreference;
import mazzoubi.ldjobs.com.alpharide.Data.Users.UserModel;
import mazzoubi.ldjobs.com.alpharide.R;


public class CarsAdapter extends ArrayAdapter<DriverRequestAccountModel> {

    DriverRequestAccountModel user ;
    Activity c ;
    public CarsAdapter(Activity context, int view, ArrayList<DriverRequestAccountModel> arrayList){
        super(context,view,arrayList);
        this.c = context;
    }


    TextView txvType , txvModel , txvNo , txvColor ;

    TextView txvFront , txvEnd , txvInside , txvCarL , txvDriverL ;

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
        txvEnd = myView.findViewById(R.id.textView8);
        txvInside = myView.findViewById(R.id.textView9);
        txvCarL = myView.findViewById(R.id.textView11);
        txvDriverL = myView.findViewById(R.id.textView12);

        txvType.setText(user.typeCar );
        txvModel.setText(user.modelCar );
        txvNo.setText(user.numberCar );
        txvColor.setText(user.colorCar );

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