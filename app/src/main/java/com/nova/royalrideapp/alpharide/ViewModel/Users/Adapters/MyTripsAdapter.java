package com.nova.royalrideapp.alpharide.ViewModel.Users.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.nova.royalrideapp.alpharide.ClassDate;
import com.nova.royalrideapp.alpharide.Data.Users.MyTripsModel;
import com.nova.alpha_ride.alpharide.R;

public class MyTripsAdapter extends ArrayAdapter<MyTripsModel> {

    MyTripsModel a ;
    Activity c ;
    public MyTripsAdapter(Activity context, int view, ArrayList<MyTripsModel> arrayList){
        super(context,view,arrayList);
        this.c = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
        View myView = layoutInflater.inflate(R.layout.row_my_trips,parent,false);

        TextView txvDatetime = myView.findViewById(R.id.txt3);
        TextView txvAddress = myView.findViewById(R.id.txt5);
        TextView txvTripState = myView.findViewById(R.id.txt1);

        a= getItem(position);

        txvDatetime.setText(a.date + " "+ ClassDate.timeByDate(a.dateStart));
        txvAddress.setText(a.addressCurrent);

        if (a.state.equals("StateTrip.cancelByCustomer")){
            txvTripState.setText("تم الغاء الرحلة");
            txvTripState.setTextColor(Color.RED);
        }


        return myView ;
    }

}
