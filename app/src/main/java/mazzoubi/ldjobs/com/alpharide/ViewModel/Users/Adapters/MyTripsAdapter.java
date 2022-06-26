package mazzoubi.ldjobs.com.alpharide.ViewModel.Users.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mazzoubi.ldjobs.com.alpharide.Data.Notifications.NotificationModel;
import mazzoubi.ldjobs.com.alpharide.Data.Users.MyTripsModel;
import mazzoubi.ldjobs.com.alpharide.R;

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

        TextView title = myView.findViewById(R.id.textView2);

        a= getItem(position);

        title.setText(a.toString());

        return myView ;
    }

}
