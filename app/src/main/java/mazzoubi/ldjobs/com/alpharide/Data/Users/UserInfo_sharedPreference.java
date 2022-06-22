package mazzoubi.ldjobs.com.alpharide.Data.Users;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.PropertyName;

import mazzoubi.ldjobs.com.alpharide.MainActivity;

public class UserInfo_sharedPreference {
    public static UserModel getUser(Activity c){
        UserModel user = new UserModel();
        SharedPreferences sharedPreferences = c.getSharedPreferences("User", Context.MODE_PRIVATE);
        user.balance=sharedPreferences.getInt("balance",0);
        user.countRating=sharedPreferences.getInt("countRating",0);
        user.countTrips=sharedPreferences.getInt("countTrips",0);
        user.points=sharedPreferences.getInt("points",0);

        user.uid=sharedPreferences.getString("uid","");
        user.carColor=sharedPreferences.getString("carColor","");
        user.carModel=sharedPreferences.getString("carModel","");
        user.carType=sharedPreferences.getString("carType","");
        user.email=sharedPreferences.getString("email","");
        user.fullName=sharedPreferences.getString("fullName","");
        user.imageProfile=sharedPreferences.getString("imageProfile","");
        user.emailFacebook=sharedPreferences.getString("emailFacebook","");
        user.numberCar=sharedPreferences.getString("numberCar","");
        user.phoneNumber=sharedPreferences.getString("phoneNumber","");
        user.stateAccount=sharedPreferences.getString("stateAccount","");
        user.typeUser=sharedPreferences.getString("typeUser","");
        user.password=sharedPreferences.getString("password","");

        user.emailVerified=sharedPreferences.getBoolean("emailVerified",false);
        user.usePassword=sharedPreferences.getBoolean("usePassword",false);
        user.rating=sharedPreferences.getFloat("rating",0);

        user.driverLicense = sharedPreferences.getString("driverLicense" , "");
        user.drivingLicense = sharedPreferences.getString("drivingLicense" , "");
        user.yourPhoto = sharedPreferences.getString("yourPhoto" , "");
        user.endCar = sharedPreferences.getString("endCar" , "");
        user.insideCar = sharedPreferences.getString("insideCar" , "");
        user.frontCar = sharedPreferences.getString("frontCar" , "");

        return user;
    }

    public static void setInfo(Activity c,UserModel user){
        SharedPreferences.Editor editor = c.getSharedPreferences("User", Context.MODE_PRIVATE).edit();

        editor.putBoolean("emailVerified" , user.emailVerified );
        editor.putBoolean("usePassword" , user.usePassword );
        editor.putFloat("rating" , (float) user.rating);

        editor.putString("uid", user.uid );
        editor.putString("carColor", user.carColor );
        editor.putString("carModel", user.carModel );
        editor.putString("carType", user.carType );
        editor.putString("email", user.email );
        editor.putString("fullName", user.fullName );
        editor.putString("imageProfile", user.imageProfile );
        editor.putString("emailFacebook", user.emailFacebook );
        editor.putString("numberCar", user.numberCar );
        editor.putString("phoneNumber", user.phoneNumber );
        editor.putString("stateAccount", user.stateAccount );
        editor.putString("typeUser", user.typeUser );
        editor.putString("password", user.password );

        editor.putInt("balance",user.balance );
        editor.putInt("countRating",user.countRating );
        editor.putInt("countTrips",user.countTrips );
        editor.putInt("points",user.points );


        editor.putString("driverLicense",user.driverLicense);
        editor.putString("drivingLicense",user.drivingLicense);
        editor.putString("yourPhoto",user.yourPhoto);
        editor.putString("endCar",user.endCar);
        editor.putString("insideCar",user.insideCar);
        editor.putString("frontCar",user.frontCar);

        editor.apply();
    }


    public static void logout(Activity c){
        SharedPreferences.Editor editor = c.getSharedPreferences("User", Context.MODE_PRIVATE).edit();

        editor.putBoolean("emailVerified" , false );
        editor.putBoolean("usePassword" , false );
        editor.putFloat("rating" , 0);

        editor.putString("uid", "" );
        editor.putString("carColor", "" );
        editor.putString("carModel", "" );
        editor.putString("carType", "" );
        editor.putString("email", "" );
        editor.putString("fullName", "" );
        editor.putString("imageProfile", "" );
        editor.putString("emailFacebook", "" );
        editor.putString("numberCar", "" );
        editor.putString("phoneNumber", "" );
        editor.putString("stateAccount", "" );
        editor.putString("typeUser", "" );
        editor.putString("password", "" );

        editor.putInt("balance",0 );
        editor.putInt("countRating",0 );
        editor.putInt("countTrips",0 );
        editor.putInt("points",0 );


        editor.putString("driverLicense" , "");
        editor.putString("drivingLicense" , "");
        editor.putString("yourPhoto" , "");
        editor.putString("endCar" , "");
        editor.putString("insideCar" , "");
        editor.putString("frontCar" , "");


        editor.apply();

        FirebaseAuth.getInstance().signOut();
        c.startActivity(new Intent(c, MainActivity.class));
        c.finish();
    }
}
