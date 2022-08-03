package com.nova.royalrideapp.ViewModel.Notifications;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

import com.nova.royalrideapp.Data.Notifications.NotificationModel;
import com.nova.royalrideapp.Data.Users.UserInfo_sharedPreference;

public class NotificationViewModel extends ViewModel {
    private static final String notificationsCollection = "Notifications";
    public MutableLiveData<ArrayList<NotificationModel>> listOfNotifications = new MutableLiveData<>();


    public void getNotifications(Activity c){
        ArrayList<NotificationModel> temp = new ArrayList<>();

        FirebaseFirestore.getInstance()
                .collection(notificationsCollection)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot d : queryDocumentSnapshots.getDocuments()){
                    if (d.toObject(NotificationModel.class).uid.isEmpty()){
                        temp.add(d.toObject(NotificationModel.class));
                    }else if (d.toObject(NotificationModel.class).uid.equals(UserInfo_sharedPreference.getUser(c).uid)){
                        temp.add(d.toObject(NotificationModel.class));
                    }
                }
                Collections.reverse(temp);
                listOfNotifications.setValue(temp);
            }
        });
    }
}
