package com.nova.royalrideapp.ViewModel.Users;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.nova.royalrideapp.ClassDate;
import com.nova.royalrideapp.Data.Users.DriverRequestAccountModel;
import com.nova.royalrideapp.Data.Users.UserInfo_sharedPreference;
import com.nova.royalrideapp.Data.Users.UserModel;
import com.nova.royalrideapp.ViewModel.Main.DashboardActivity;
import com.nova.royalrideapp.ViewModel.Main.MapsActivity;

public class UserViewModel extends ViewModel {
    private static final String userCollection = "Users" ;

    public MutableLiveData<UserModel> userObject = new MutableLiveData<>();
    public MutableLiveData<ArrayList<DriverRequestAccountModel>> listOfCars = new MutableLiveData<>();



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
        map.put("typeUser", "TypeAccount.driver" );
        map.put("password", user.password );
        map.put("AID", user.AID );
//        map.put("tripid", tams );

        map.put("balance",user.balance );
        map.put("countRating",user.countRating );
        map.put("countTrips",user.countTrips );
        map.put("points",user.points );

        //////////////////////////////////////////////////      new detail added
        map.put("driverLicense",dd.driverLicense );
        map.put("drivingLicense",dd.drivingLicense );
        map.put("yourPhoto","");
        map.put("endCar","");
        map.put("insideCar","");
        map.put("frontCar",dd.frontCar );

        ProgressDialog progressDialog = new ProgressDialog(c);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("الرجاء الإنتظار...");
        progressDialog.show();
        FirebaseFirestore.getInstance().collection(userCollection)
                .document(user.uid).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        Toast.makeText(c, "تمت اضافة المستخدم بنجاح", Toast.LENGTH_SHORT).show();
                        c.startActivity(intent);
                        for (Activity d:activities){
                            d.finish();
                        }
                        c.finish();
                    }
                });
    }

    public void getUserInfo(Activity c, String uid){
        userObject = new MutableLiveData<>();
        FirebaseFirestore.getInstance().collection(userCollection)
                .whereEqualTo("uid",uid)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size()<1){

                }else {
                    UserModel userModel = queryDocumentSnapshots.getDocuments().get(0).toObject(UserModel.class);
                    userObject.setValue(userModel);
                }
            }
        });
    }

    public void getUserInfoByPhoneNumber(Activity c, String phone){

        ProgressDialog progressDialog = new ProgressDialog(c);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("الرجاء الإنتظار...");
        progressDialog.show();

        userObject = new MutableLiveData<>();
        FirebaseFirestore.getInstance().collection(userCollection)
                .whereEqualTo("phoneNumber",phone)
                .whereEqualTo("typeUser", "TypeAccount.driver")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                progressDialog.dismiss();
                if (queryDocumentSnapshots.getDocuments().size()<1){
                    userObject.setValue(null);
                }else {
                    UserModel userModel = queryDocumentSnapshots.getDocuments().get(0).toObject(UserModel.class);
                    userObject.setValue(userModel);

                }
            }
        });
    }

    public void UpdateUser(Activity c , UserModel user){

        UserInfo_sharedPreference.setInfo(c,user);
        Map<String,Object> map = new HashMap<>();
        map.put("email", user.email );
        map.put("fullName", user.fullName );
        map.put("password", user.password );
        map.put("imageProfile", user.imageProfile );

        final ProgressDialog progressDialogg = new ProgressDialog(c);
        progressDialogg.setCancelable(false);
        progressDialogg.setTitle("الرجاء الإنتظار...");
        try{progressDialogg.show();} catch (Exception ex){}
        FirebaseFirestore.getInstance().collection(userCollection)
                .document(user.uid).update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialogg.dismiss();
                        UserInfo_sharedPreference.setInfo(c,user);
                        Toast.makeText(c, "تمت عملية الحفظ بنجاح", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void UpdateAID(Activity c , String AID){
        Map<String,Object> map = new HashMap<>();
        map.put("AID", AID );

        if(!(UserInfo_sharedPreference.getUser(c).uid+"").equals("")
                && UserInfo_sharedPreference.getUser(c).uid != null){

            FirebaseFirestore.getInstance().collection(userCollection)
                    .document(UserInfo_sharedPreference.getUser(c).uid)
                    .update(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            SharedPreferences.Editor editor = c.getSharedPreferences("User", Context.MODE_PRIVATE).edit();
                            editor.putString("AID" , AID);
                            editor.apply();
                            editor.commit();
                        }
                    });
        }
    }

    public void forgotPassword(Activity c , UserModel user){

        UserInfo_sharedPreference.setInfo(c,user);
        Map<String,Object> map = new HashMap<>();
        map.put("password", user.password );

        ProgressDialog progressDialog = new ProgressDialog(c);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("الرجاء الإنتظار...");
        progressDialog.show();

        if(user.uid != null && !user.uid.equals("")){
            try{
                FirebaseFirestore.getInstance().collection(userCollection)
                        .document(user.uid)
                        .update(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                UserInfo_sharedPreference.setInfo(c,user);
                                c.startActivity(new Intent(c, MapsActivity.class));
                                Toast.makeText(c, "تمت عملية الحفظ بنجاح", Toast.LENGTH_SHORT).show();
                                c.finish();
                            }
                        });
            }
            catch (Exception ex){}
        }
    }

    public void addDriverRequest(Activity c, DriverRequestAccountModel dd){
        String key = ClassDate.currentTimeAtMs();
        Map<String,Object> map = new HashMap<>();
        map.put("colorCar",dd.colorCar );
        map.put("driverLicense",dd.driverLicense );
        map.put("drivingLicense",dd.drivingLicense );
        map.put("yourPhoto","");
        map.put("endCar","");
        map.put("insideCar","");
        map.put("frontCar",dd.frontCar );
        map.put("fullName",dd.fullName );
        map.put("email",dd.email );
        map.put("idUser",dd.idUser );
        map.put("modelCar",dd.modelCar );
        map.put("numberCar",dd.numberCar );
        map.put("phoneNumber",dd.phoneNumber );
        map.put("typeCar",dd.typeCar );
        map.put("create_date", ClassDate.date());
        map.put("_id", key);

        ProgressDialog progressDialog = new ProgressDialog(c);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("الرجاء الإنتظار...");
        progressDialog.show();

        FirebaseFirestore.getInstance().collection("DriverRequestsAccount")
                .document(key).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
            }
        });
    }

    public void addNewCar(Activity c, UserModel user , DriverRequestAccountModel dd){
        dd.idUser=UserInfo_sharedPreference.getUser(c).uid;
        dd.fullName=UserInfo_sharedPreference.getUser(c).fullName;
        dd.colorCar = user.carColor;
        dd.modelCar = user.carModel;
        dd.typeCar = user.carType;
        dd.numberCar = user.numberCar;
        dd.email = UserInfo_sharedPreference.getUser(c).email;
        dd.phoneNumber = UserInfo_sharedPreference.getUser(c).phoneNumber;

        String key = System.currentTimeMillis()+"";
        Map<String,Object> map = new HashMap<>();
        map.put("colorCar",dd.colorCar );
        map.put("driverLicense",dd.driverLicense );
        map.put("drivingLicense",dd.drivingLicense );
        map.put("yourPhoto","");
        map.put("endCar","");
        map.put("insideCar","");
        map.put("frontCar",dd.frontCar );
        map.put("fullName",dd.fullName );
        map.put("email",dd.email );
        map.put("idUser",dd.idUser );
        map.put("modelCar",dd.modelCar );
        map.put("numberCar",dd.numberCar );
        map.put("phoneNumber",dd.phoneNumber );
        map.put("typeCar",dd.typeCar );
        map.put("state","0" );
        map.put("create_date", ClassDate.date());
        map.put("_id",key );

        ProgressDialog progressDialog = new ProgressDialog(c);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("الرجاء الإنتظار...");
        progressDialog.show();

        FirebaseFirestore.getInstance().collection("OtherCars")
                .whereEqualTo("idUser",dd.idUser)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size()>=2){
                            Toast.makeText(c, "لا يجوز اضافة اكثر من سيارتين اضافيات!", Toast.LENGTH_SHORT).show();
                        }else {
                            FirebaseFirestore.getInstance().collection("OtherCars")
                                    .document(key).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()){
                                        Toast.makeText(c, "تمت اضافة المركبة بنجاح ", Toast.LENGTH_SHORT).show();
                                        c.finish();
                                    }
                                }
                            });
                        }
                    }
                });


    }

    public void getCars(Activity c ){

        ProgressDialog progressDialog = new ProgressDialog(c);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("الرجاء الإنتظار...");
        progressDialog.show();

        listOfCars = new MutableLiveData<>();
        ArrayList<DriverRequestAccountModel> temp = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("OtherCars")
                .whereEqualTo("idUser",UserInfo_sharedPreference.getUser(c).uid )
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot d: queryDocumentSnapshots.getDocuments()){
                    temp.add(d.toObject(DriverRequestAccountModel.class));
                }
                progressDialog.dismiss();
                listOfCars.setValue(temp);
            }
        });
    }

    public void login(Activity c , String phone,String password){

        ProgressDialog progressDialog = new ProgressDialog(c);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("الرجاء الإنتظار...");
        progressDialog.show();
        FirebaseFirestore.getInstance()
                .collection(userCollection)
                .whereEqualTo("phoneNumber",phone)
                .whereEqualTo("password",password)
                .whereEqualTo("typeUser", "TypeAccount.driver")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                progressDialog.dismiss();
                if (queryDocumentSnapshots.getDocuments().size()<1){
                    Toast.makeText(c, "خطأ في اسم المستخدم او كلمة المرور!", Toast.LENGTH_SHORT).show();
                }else {
                    UserModel a = queryDocumentSnapshots.getDocuments().get(0).toObject(UserModel.class);
                    String AID = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);

                    UserInfo_sharedPreference.setInfo(c,a);

                    if (a.AID.isEmpty()){
                        UpdateAID(c,AID);
                        c.startActivity(new Intent(c,DashboardActivity.class));
                        c.finish();
                    }
                    else if (!a.AID.equals(AID)){
                        Toast.makeText(c, "تم تسجيل خروجك من الجهاز القديم..", Toast.LENGTH_SHORT).show();
                        UpdateAID(c,AID);
                        c.startActivity(new Intent(c,DashboardActivity.class));
                        c.finish();
                    }
                        else {
                        c.startActivity(new Intent(c, DashboardActivity.class));
                        c.finish();
                    }

                }
            }
        });
    }

    public void login2(Activity c , String phone,String password){

        FirebaseFirestore.getInstance()
                .collection(userCollection)
                .whereEqualTo("phoneNumber",phone)
                .whereEqualTo("password",password)
                .whereEqualTo("typeUser", "TypeAccount.driver")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (queryDocumentSnapshots.getDocuments().size()<1){
                    Toast.makeText(c, "خطأ في اسم المستخدم او كلمة المرور!", Toast.LENGTH_SHORT).show();
                }else {
                    UserModel a = queryDocumentSnapshots.getDocuments().get(0).toObject(UserModel.class);
                    String AID = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);

                    UserInfo_sharedPreference.setInfo(c,a);
                    if (a.AID.isEmpty()||!a.AID.equals(AID)){
                        UpdateAID(c,AID);
                        c.startActivity(new Intent(c,DashboardActivity.class));
                        c.finish();
                    }else {
                        c.startActivity(new Intent(c, DashboardActivity.class));
                        c.finish();
                    }

                }
            }
        });
    }

    public void updateToken(Activity c){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(UserInfo_sharedPreference.getUser(c).uid != null && !UserInfo_sharedPreference.getUser(c).uid.equals(""))
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (task.isSuccessful()) {
                                        String token = task.getResult();
                                        Map<String,Object> map = new HashMap<>();
                                        map.put("token",token);
                                        FirebaseFirestore.getInstance().collection(userCollection)
                                                .document(UserInfo_sharedPreference.getUser(c).uid)
                                                .update(map);
                                    }
                                }
                            });
            }
        }, 5000);

    }
}
