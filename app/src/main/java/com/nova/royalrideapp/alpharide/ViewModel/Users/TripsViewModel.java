package com.nova.royalrideapp.alpharide.ViewModel.Users;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

import com.nova.royalrideapp.alpharide.Data.Users.MyTripsModel;
import com.nova.royalrideapp.alpharide.Data.Users.UserInfo_sharedPreference;

public class TripsViewModel extends ViewModel {
    private static final String tripsCollection = "Trips" ;

    public MutableLiveData<ArrayList<MyTripsModel>> listOfMyTrips = new MutableLiveData<>();

    public void getMyTrips(Activity c){
        listOfMyTrips = new MutableLiveData<>();
        ArrayList<MyTripsModel> temp = new ArrayList<>();

        FirebaseFirestore.getInstance().collection(tripsCollection).whereEqualTo("idDriver", UserInfo_sharedPreference
                .getUser(c).uid).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot d: queryDocumentSnapshots.getDocuments()){
                            temp.add(0,d.toObject(MyTripsModel.class));
                        }
                        Collections.reverse(temp);
                        listOfMyTrips.setValue(temp);
                    }
                });
    }
}
