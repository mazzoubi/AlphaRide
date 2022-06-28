package mazzoubi.ldjobs.com.alpharide;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ClassDate extends ViewModel {
    public static String date(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy",new Locale("EN"));
        int day = new Date().getDate();
        String date = day+"-"+dateFormat.format(new Date()).split("-")[0]+"-"+new Date().toString().split(" ")[5];
        return date;
    }
    public static String daftraDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy",new Locale("EN"));
        int day = new Date().getDate();
        String []date = (day+"-"+dateFormat.format(new Date()).split("-")[0]+"-"+new Date().toString().split(" ")[5]).split("-");
        if (date[0].length()<2){
            date[0]="0"+date[0];
        }
        if(date[1].length()<2){
            date[1]="0"+date[1];
        }
        return date[2]+"-"+date[1]+"-"+date[0];
    }

    public static String addDays(String dt , int dayCount){

        SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy",new Locale("EN"));
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));
            c.add(Calendar.DATE, dayCount);  // number of days to add
            dt = sdf.format(c.getTime());
            return dt;
        } catch (ParseException e) {
            return null;
        }

    }

    public static String getDay(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("M-yyyy",new Locale("EN"));
        int day = new Date().getDate();
        String date = day+"-"+dateFormat.format(new Date()).split("-")[0]+"-"+new Date().toString().split(" ")[5];
        return day + "";
    }
    public static String getMonthYear(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("M-yyyy",new Locale("EN"));
        int day = new Date().getDate();
        String date = dateFormat.format(new Date()).split("-")[0]+"-"+new Date().toString().split(" ")[5];
        return date;
    }
    public static String time(){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss",new Locale("EN"));
        String time = timeFormat.format(new Date());
        return time;
    }


    public static String timeByDate(Date date){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss",new Locale("EN"));
        String time = timeFormat.format(date);
        return time;
    }


    public static String time_12(){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a",new Locale("EN"));
        String time = timeFormat.format(new Date());
        return time;
    }
    public static String currentTimeAtMs(){
        return System.currentTimeMillis()+"";
    }

    public static boolean dateAfter(String d1 , String d2){
        try {
            if (d1.equals(d2)){
                return false;
            }else {
                String []aa =d1.split("-");
                String [] ss = d2.split("-");
                int day1 =Integer.parseInt(aa[0]);
                int month1 =Integer.parseInt(aa[1]);
                int year1 =Integer.parseInt(aa[2]);

                int day2 =Integer.parseInt(ss[0]);
                int month2 =Integer.parseInt(ss[1]);
                int year2 =Integer.parseInt(ss[2]);

                if (year1==year2&&month1==month2&&day1>day2){
                    return true;
                }else if (year1==year2&&month1>month2){
                    return true;
                }else if (year1>year2){
                    return true;
                }else return false;
            }
        }catch (Exception e){
            return false;
        }
    }

    public static boolean dateBefore(String d1 , String d2){
        try {
            if (d1.equals(d2)){
                return false;
            }else {
                String []aa =d1.split("-");
                String [] ss = d2.split("-");
                int day1 =Integer.parseInt(aa[0]);
                int month1 =Integer.parseInt(aa[1]);
                int year1 =Integer.parseInt(aa[2]);

                int day2 =Integer.parseInt(ss[0]);
                int month2 =Integer.parseInt(ss[1]);
                int year2 =Integer.parseInt(ss[2]);

                if (year1==year2&&month1==month2&&day1<day2){
                    return true;
                }else if (year1==year2&&month1<month2){
                    return true;
                }else if (year1<year2){
                    return true;
                }else return false;
            }
        }catch (Exception e){
            return false;
        }
    }

    public static boolean dateEqual(String d1 , String d2){
        if (d1.equals(d2)){
            return true;
        }else return false;
    }






    public MutableLiveData<String> datePicker = new MutableLiveData<>();
    public void showDatePicker(Activity c){
        datePicker = new MutableLiveData<>();
        final String[] dateTo = new String[1];
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date_ = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "yyyy/MM/dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                dateTo[0] =sdf.format(myCalendar.getTime());
                datePicker.setValue(dateTo[0]);

            } };
       DatePickerDialog datePickerDialog= new DatePickerDialog(c, date_, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
               datePickerDialog.show();


    }
}
