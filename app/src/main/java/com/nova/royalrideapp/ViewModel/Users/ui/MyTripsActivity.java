package com.nova.royalrideapp.ViewModel.Users.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.nova.royalrideapp.ClassDate;
import com.nova.royalrideapp.Data.Users.MyTripsModel;
import com.nova.royalrideapp.R;
import com.nova.royalrideapp.ViewModel.Main.MapsActivity;
import com.nova.royalrideapp.ViewModel.Users.Adapters.MyTripsAdapter;
import com.nova.royalrideapp.ViewModel.Users.ProfitActivity;
import com.nova.royalrideapp.ViewModel.Users.TripsViewModel;

public class MyTripsActivity extends AppCompatActivity {

    ArrayList<MyTripsModel> myTrips ;
    public static MyTripsModel tripsObject ;
    ListView listView ;

    TextView txvDateTo,txvDateFrom;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_trips);
        init();

//        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
//        registerActivityLifecycleCallbacks(handler);
//        registerComponentCallbacks(handler);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tripsObject = myTrips.get(i);
                startActivity(new Intent(getApplicationContext(),TripInfoWithMapActivity.class));
            }
        });
    }

    void init(){
        listView = findViewById(R.id.listView);
//        txvDateTo = findViewById(R.id.txvDateTo);
//        txvDateFrom = findViewById(R.id.txvDateFrom);
        getMyTrips();
    }

    void getMyTrips(){
        ProgressDialog p = new ProgressDialog(MyTripsActivity.this);
        p.setTitle("الرجاء الإنتظار....");
        p.setCancelable(false);
        p.show();

        TripsViewModel vm = ViewModelProviders.of(this).get(TripsViewModel.class);
        vm.getMyTrips(MyTripsActivity.this);
        vm.listOfMyTrips.observe(this, new Observer<ArrayList<MyTripsModel>>() {
            @Override
            public void onChanged(ArrayList<MyTripsModel> myTripsModels) {
                myTrips = myTripsModels;

                ArrayAdapter<MyTripsModel> adapter = new MyTripsAdapter(MyTripsActivity.this,
                        R.layout.activity_my_trips,myTrips);
                listView.setAdapter(adapter);
                p.dismiss();
            }
        });
    }

    public void onClickDateFrom(View view) {
        ClassDate d = ViewModelProviders.of(this).get(ClassDate.class);
        d.showDatePicker(MyTripsActivity.this);
        d.datePicker.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                txvDateFrom.setText(s);
            }
        });
    }

    public void onClickDateTo(View view) {
        ClassDate d = ViewModelProviders.of(this).get(ClassDate.class);
        d.showDatePicker(MyTripsActivity.this);
        d.datePicker.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                txvDateTo.setText(s);
            }
        });
    }

    public void Profit(View view) {

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MyTripsActivity.this);
        LayoutInflater inflater = MyTripsActivity.this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_profit, null));
        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        ((FrameLayout) dialog.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 280, getResources().getDisplayMetrics()));
        dialog.getWindow().setAttributes(lp);
        dialog.show();

        TextView t1 = dialog.findViewById(R.id.txvType);
        TextView t2 = dialog.findViewById(R.id.txvColor);
        TextView t3 = dialog.findViewById(R.id.txvNo);
        TextView t4 = dialog.findViewById(R.id.txvNo5);

        int Scount = 0, Ccount = 0;
        BigDecimal profit = new BigDecimal("0");

        for(int i=0; i<myTrips.size(); i++) {
            if (myTrips.get(i).state.contains("done") || myTrips.get(i).state.contains("need")) {
                Scount++;
                profit = profit.add(new BigDecimal(myTrips.get(i).totalPrice).subtract(new BigDecimal(myTrips.get(i).totalPrice).multiply(new BigDecimal(myTrips.get(i).discount))));
            }
            if (myTrips.get(i).state.contains("canc"))
                Ccount++;
        }

        t1.setText("إجمالي الرحلات الناجحة : "+Scount+" رحلة");
        t2.setText("إجمالي الرحلات الملغاة : "+Ccount+" رحلة");
        t3.setText("إجمالي سعر الرحلات : "+String.format(Locale.ENGLISH,"%.3f", profit.doubleValue())+" دينار");
        t4.setText("إجمالي أرباح الرحلات : "+String.format(Locale.ENGLISH,"%.3f", profit.subtract(profit.multiply(new BigDecimal("0.1"))).doubleValue())+" دينار");

    }

    public void ProfitScreen(View view) {

//        startActivity(new Intent(MyTripsActivity.this, ProfitActivity.class));

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MyTripsActivity.this);
        LayoutInflater inflater = MyTripsActivity.this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_profit2, null));
        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        ((FrameLayout) dialog.getWindow().getDecorView().findViewById(android.R.id.content)).setForeground(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 280, getResources().getDisplayMetrics()));
        dialog.getWindow().setAttributes(lp);
        dialog.show();

        int Scount = 0, Ccount = 0;
        BigDecimal profit = new BigDecimal("0");

        for(int i=0; i<myTrips.size(); i++) {
            if (myTrips.get(i).state.contains("done") || myTrips.get(i).state.contains("need")) {
                Scount++;
                profit = profit.add(new BigDecimal(myTrips.get(i).totalPrice).subtract(new BigDecimal(myTrips.get(i).totalPrice).multiply(new BigDecimal(myTrips.get(i).discount))));
            }
            if (myTrips.get(i).state.contains("canc"))
                Ccount++;
        }

//        t1.setText("إجمالي الرحلات الناجحة : "+Scount+" رحلة");
//        t2.setText("إجمالي الرحلات الملغاة : "+Ccount+" رحلة");
//        t3.setText("إجمالي سعر الرحلات : "+String.format(Locale.ENGLISH,"%.3f", profit.doubleValue())+" دينار");
//        t4.setText("إجمالي أرباح الرحلات : "+String.format(Locale.ENGLISH,"%.3f", profit.subtract(profit.multiply(new BigDecimal("0.1"))).doubleValue())+" دينار");

        AnyChartView anyChartView1 = dialog.findViewById(R.id.any_chart_view);
        List<DataEntry> out = new ArrayList<>();
        Pie pie1 = AnyChart.pie();
        APIlib.getInstance().setActiveAnyChartView(anyChartView1);

        out.add(new ValueDataEntry("إجمالي الناجحة", Double.parseDouble(Scount+"")));
        out.add(new ValueDataEntry("إجمالي الملغاة", Double.parseDouble(Ccount+"")));
        out.add(new ValueDataEntry("إجمالي السعر", profit.doubleValue()*-1));
        out.add(new ValueDataEntry("إجمالي الأرباح", new BigDecimal(profit.doubleValue()*-1).subtract(new BigDecimal(profit.doubleValue()*-1).multiply(new BigDecimal("0.1"))).doubleValue()));

//        out.add(new ValueDataEntry("1", 1.0));
//        out.add(new ValueDataEntry("2", 2.0));
//        out.add(new ValueDataEntry("3", 3.0));
//        out.add(new ValueDataEntry("4", 4.0));
        pie1.data(out);
        anyChartView1.setChart(pie1);
        pie1.background().fill("#000000");
        pie1.legend().enabled(false);

    }
}