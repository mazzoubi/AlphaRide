package com.nova.royalrideapp.ViewModel.Users.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.nova.royalrideapp.ClassDate;
import com.nova.royalrideapp.Data.Users.MyTripsModel;
import com.nova.royalrideapp.R;

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
        TextView distance = myView.findViewById(R.id.txt54);
        TextView time = myView.findViewById(R.id.txt544);
        TextView tid = myView.findViewById(R.id.txt5414);

        a= getItem(position);

        txvDatetime.setText(a.date + " "+ ClassDate.timeByDate(a.dateStart));
        txvAddress.setText(a.addressCurrent);
        distance.setText(a.km+" Km. ");
        time.setText(a.hours+" Min. ");
        tid.setText(a.tripsid+"");

        if (a.state.equals("StateTrip.cancelByCustomer")){
            txvTripState.setText("تم الغاء الرحلة");
            txvTripState.setTextColor(Color.RED);
        }
        else if (a.state.equals("StateTrip.active")){
            txvTripState.setText("تم قبول الرحلة");
            txvTripState.setTextColor(Color.GREEN);
        }
        else if (a.state.equals("StateTrip.started")){
            txvTripState.setText("تم بدء الرحلة");
            txvTripState.setTextColor(Color.GREEN);
        }
        else if (a.state.equals("StateTrip.needRatingByDriver")){
            txvTripState.setText("تم إنهاء الرحلة");
            txvTripState.setTextColor(Color.GREEN);
        }
        else if (a.state.equals("StateTrip.done")){
            txvTripState.setText("تم إنهاء الرحلة");
            txvTripState.setTextColor(Color.GREEN);
        }


        return myView ;
    }

}
