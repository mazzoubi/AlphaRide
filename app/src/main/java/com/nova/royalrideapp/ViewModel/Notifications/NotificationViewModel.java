package com.nova.royalrideapp.ViewModel.Notifications;

import android.app.Activity;
import android.net.ParseException;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.nova.royalrideapp.Data.Notifications.NotificationModel;
import com.nova.royalrideapp.Data.Users.UserInfo_sharedPreference;

public class NotificationViewModel extends ViewModel {
    private static final String notificationsCollection = "Notifications";
    public MutableLiveData<ArrayList<NotificationModel>> listOfNotifications = new MutableLiveData<>();


    public void getNotifications(Activity c){
        ArrayList<NotificationModel> temp = new ArrayList<>();
        ArrayList<NotificationModel> temp2 = new ArrayList<>();

        FirebaseFirestore.getInstance()
                .collection(notificationsCollection)
                .whereEqualTo("typeUser", "TypeAccount.driver")
                .whereNotEqualTo("body", "لقد تم الموافقه على طلبك بنجاح")
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

                for(int i=0; i< temp.size(); i++){
                    NotificationModel obj = new NotificationModel();
                    obj = temp.get(i);
                    obj.id = Calc(obj.createdAt);
                    temp2.add(obj);
                }

                Collections.sort(temp2, new NotificationModel());

                Collections.reverse(temp2);
                listOfNotifications.setValue(temp2);
            }
        });
    }

    private long Calc(Date createdAt) {

        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        try {
            long temp = sdf.parse(createdAt.toString()).getTime();
            return temp;
        }
        catch (Exception e) {
            return 0;
        }

    }
}
