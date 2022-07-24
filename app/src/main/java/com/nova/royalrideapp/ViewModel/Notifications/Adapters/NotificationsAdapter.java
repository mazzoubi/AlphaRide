package com.nova.royalrideapp.ViewModel.Notifications.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.nova.royalrideapp.Data.Notifications.NotificationModel;
import com.nova.royalrideapp.R;

public class NotificationsAdapter extends ArrayAdapter<NotificationModel> {

    NotificationModel a ;
    Activity c ;
    public NotificationsAdapter(Activity context, int view, ArrayList<NotificationModel> arrayList){
        super(context,view,arrayList);
        this.c = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
        View myView = layoutInflater.inflate(R.layout.row_notifications,parent,false);

        TextView title = myView.findViewById(R.id.textView16);
        TextView body = myView.findViewById(R.id.textView17);
        TextView date = myView.findViewById(R.id.textView18);
        TextView count = myView.findViewById(R.id.count);

        a= getItem(position);

        title.setText(a.title);
        body.setText(a.body);
        date.setText(a.createdAt.toString());
        count.setText("-"+(position+1) + "-");


        return myView ;
    }

}
