package com.nova.royalrideapp.ViewModel.Notifications.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nova.royalrideapp.Data.Notifications.NotificationModel;
import com.nova.royalrideapp.R;

public class NotificationsAdapter extends BaseAdapter {

    public static class ViewHolder {

        public static TextView title;
        public static TextView body;
        public static TextView date;
        public static TextView count;

    }
    Activity c ;
    ArrayList<NotificationModel> arrayList;

    public NotificationsAdapter(Activity context, int view, ArrayList<NotificationModel> arrayList){
        this.c = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() { return arrayList.size(); }

    @Override
    public Object getItem(int i) { return arrayList.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View myView = convertView;
        if(myView == null)
            myView = LayoutInflater.from(c).inflate(R.layout.row_notifications,parent,false);

        ViewHolder holder = new ViewHolder();
        holder.title = myView.findViewById(R.id.textView16);
        holder.body = myView.findViewById(R.id.textView17);
        holder.date = myView.findViewById(R.id.textView18);
        holder.count = myView.findViewById(R.id.count);

        holder.title.setText(arrayList.get(position).title);
        holder.body.setText(arrayList.get(position).body);
        holder.date.setText(arrayList.get(position).createdAt.toString());
        holder.count.setText("-"+(position+1) + "-");

        holder.body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(pullLinks(arrayList.get(position).body).get(0))));
            }
        });

        return myView ;
    }

    //Pull all links from the body for easy retrieval

    public ArrayList<String> pullLinks(String text)
    {
        ArrayList<String> links = new ArrayList<String>();

        //String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        String regex = "\\(?\\b(https?://|www[.]|ftp://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);

        while(m.find())
        {
            String urlStr = m.group();

            if (urlStr.startsWith("(") && urlStr.endsWith(")"))
            {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }

            links.add(urlStr);
        }

        return links;
    }

}
