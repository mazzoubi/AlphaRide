package mazzoubi.ldjobs.com.alpharide.Data.Users;

import android.location.Location;

import java.util.Date;
import java.util.Map;

public class MyTripsModel {
    public String typeTrip="";
    public String state="";
    public String idDriver="";
    public String idCustomer="";
    public String addressCurrent="";
    public String date="";


    public int tripsid=0;

    public double totalPrice=0;
    public double km=0;
    public double hours=0;
    public double discount=0;


    public Date dateAcceptRequest=null;
    public Date dateStart=null;


    public Map<String,Object> locationDriver=null;
    public Map<String,Object> locationCustomer=null;
    public Map<String,Object> accessPoint=null;


    @Override
    public String toString() {
        return
                "typeTrip='" + typeTrip + "\n"+
                "state='" + state + "\n"+
                "idDriver='" + idDriver + "\n"+
                "idCustomer='" + idCustomer + "\n"+
                "addressCurrent='" + addressCurrent + "\n"+
                "date='" + date + "\n"+
                "tripsid=" + tripsid +"\n"+
                "totalPrice=" + totalPrice +"\n"+
                "km=" + km +"\n"+
                "hours=" + hours +"\n"+
                "discount=" + discount +"\n"+
                "dateAcceptRequest=" + dateAcceptRequest +"\n"+
                "dateStart=" + dateStart +"\n"+
                "locationDriver=" + locationDriver +"\n\n"+
                "locationCustomer=" + locationCustomer +"\n\n"+
                "accessPoint=" + accessPoint ;
    }
}
