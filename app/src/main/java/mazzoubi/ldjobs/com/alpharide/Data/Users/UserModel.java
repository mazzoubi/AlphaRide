package mazzoubi.ldjobs.com.alpharide.Data.Users;

import androidx.annotation.Keep;

import com.google.firebase.firestore.PropertyName;

@Keep
public class UserModel {
    @PropertyName("balance")
    public int balance = 0;
    @PropertyName("countRating")
    public int countRating = 0;
    @PropertyName("countTrips")
    public int countTrips = 0;
    @PropertyName("points")
    public int points = 0;
    @PropertyName("uid")
    public String uid = "" ;
    @PropertyName("carColor")
    public String carColor = "" ;
    @PropertyName("carModel")
    public String carModel = "" ;
    @PropertyName("carType")
    public String carType = "" ;
    @PropertyName("email")
    public String email = "" ;
    @PropertyName("fullName")
    public String fullName = "" ;
    @PropertyName("imageProfile")
    public String imageProfile = "" ;
    @PropertyName("emailFacebook")
    public String emailFacebook = "" ;
    @PropertyName("numberCar")
    public String numberCar = "" ;
    @PropertyName("phoneNumber")
    public String phoneNumber = "" ;
    @PropertyName("stateAccount")
    public String stateAccount = "" ;
    @PropertyName("typeUser")
    public String typeUser = "" ;
    @PropertyName("password")
    public String password = "" ;
    @PropertyName("emailVerified")
    public boolean emailVerified = false;
    @PropertyName("usePassword")
    public boolean usePassword = false;
    @PropertyName("rating")
    public double rating = 5;



    @PropertyName("driverLicense")
    public String driverLicense="";
    @PropertyName("drivingLicense")
    public String drivingLicense="";
    @PropertyName("yourPhoto")
    public String yourPhoto="";
    @PropertyName("endCar")
    public String endCar="";
    @PropertyName("insideCar")
    public String insideCar="";
    @PropertyName("frontCar")
    public String frontCar="";
}
