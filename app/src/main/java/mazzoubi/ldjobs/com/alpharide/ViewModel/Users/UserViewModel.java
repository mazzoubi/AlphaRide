package mazzoubi.ldjobs.com.alpharide.ViewModel.Users;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mazzoubi.ldjobs.com.alpharide.Data.Users.DriverRequestAccountModel;
import mazzoubi.ldjobs.com.alpharide.Data.Users.UserInfo_sharedPreference;
import mazzoubi.ldjobs.com.alpharide.Data.Users.UserModel;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Main.DashboardActivity;

public class UserViewModel extends ViewModel {
    private static final String userCollection = "Users" ;
    public void addUser(Activity c , UserModel user, DriverRequestAccountModel dd,
                        Intent intent, ArrayList<Activity> activities){

        dd.idUser=user.uid;
        dd.fullName=user.fullName;
        dd.colorCar = user.carColor;
        dd.modelCar = user.carModel;
        dd.typeCar = user.carType;
        dd.numberCar = user.numberCar;
        dd.email = user.email;
        dd.phoneNumber = user.phoneNumber;
        addDriverRequest(c,dd);


        UserInfo_sharedPreference.setInfo(c,user);
        Map<String,Object> map = new HashMap<>();
        map.put("emailVerified" , user.emailVerified );
        map.put("usePassword" , user.usePassword );
        map.put("rating" , (float) user.rating);

        map.put("uid", user.uid );
        map.put("carColor", user.carColor );
        map.put("carModel", user.carModel );
        map.put("carType", user.carType );
        map.put("email", user.email );
        map.put("fullName", user.fullName );
        map.put("imageProfile", user.imageProfile );
        map.put("emailFacebook", user.emailFacebook );
        map.put("numberCar", user.numberCar );
        map.put("phoneNumber", user.phoneNumber );
        map.put("stateAccount", user.stateAccount );
        map.put("typeUser", user.typeUser );
        map.put("password", user.password );

        map.put("balance",user.balance );
        map.put("countRating",user.countRating );
        map.put("countTrips",user.countTrips );
        map.put("points",user.points );

        Toast.makeText(c, "الرجاء الانتظار ....", Toast.LENGTH_SHORT).show();
        FirebaseFirestore.getInstance().collection(userCollection).document(user.uid).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(c, "تمت اضافة المستخدم بنجاح", Toast.LENGTH_SHORT).show();
                        c.startActivity(intent);
                        for (Activity d:activities){
                            d.finish();
                        }
                        c.finish();
                    }
                });
    }

    public void addDriverRequest(Activity c, DriverRequestAccountModel dd){
        Map<String,Object> map = new HashMap<>();
        map.put("colorCar",dd.colorCar );
        map.put("driverLicense",dd.driverLicense );
        map.put("drivingLicense",dd.drivingLicense );
        map.put("email",dd.email );
        map.put("frontCar",dd.frontCar );
        map.put("fullName",dd.fullName );
        map.put("idUser",dd.idUser );
        map.put("modelCar",dd.modelCar );
        map.put("numberCar",dd.numberCar );
        map.put("phoneNumber",dd.phoneNumber );
        map.put("typeCar",dd.typeCar );

        FirebaseFirestore.getInstance().collection("DriverRequestsAccount")
                .document(dd.idUser).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    public void login(Activity c , String phone,String password){
        FirebaseFirestore.getInstance().collection(userCollection)
                .whereEqualTo("phoneNumber",phone)
                .whereEqualTo("password",password)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size()<1){
                    Toast.makeText(c, "خطأ في اسم المستخدم او كلمة المرور!", Toast.LENGTH_SHORT).show();
                }else {
                    UserModel a = queryDocumentSnapshots.getDocuments().get(0).toObject(UserModel.class);
                    UserInfo_sharedPreference.setInfo(c,a);
                    c.startActivity(new Intent(c,DashboardActivity.class));
                    c.finish();
                }
            }
        });
    }
}
