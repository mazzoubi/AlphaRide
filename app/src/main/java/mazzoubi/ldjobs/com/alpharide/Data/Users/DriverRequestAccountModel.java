package mazzoubi.ldjobs.com.alpharide.Data.Users;

import androidx.annotation.Keep;

import com.google.firebase.firestore.PropertyName;

@Keep
public class DriverRequestAccountModel {
    @PropertyName("colorCar")
    public String colorCar = "" ;
    @PropertyName("driverLicense")
    public String driverLicense = "" ;
    @PropertyName("drivingLicense")
    public String drivingLicense = "" ;
    @PropertyName("email")
    public String email = "" ;
    @PropertyName("frontCar")
    public String frontCar = "" ;
    @PropertyName("fullName")
    public String fullName = "" ;
    @PropertyName("idUser")
    public String idUser = "" ;
    @PropertyName("modelCar")
    public String modelCar = "" ;
    @PropertyName("numberCar")
    public String numberCar = "" ;
    @PropertyName("phoneNumber")
    public String phoneNumber = "" ;
    @PropertyName("typeCar")
    public String typeCar = "" ;
    @PropertyName("state")
    public String state = "0" ;
}
