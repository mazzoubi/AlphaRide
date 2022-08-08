package com.nova.royalrideapp.Data.Notifications;

import androidx.annotation.Keep;

import com.google.firebase.firestore.PropertyName;

import java.util.Comparator;
import java.util.Date;

@Keep
public class NotificationModel implements Comparator<NotificationModel>{

    @PropertyName("uid")
    public String uid = "" ;
     @PropertyName("body")
    public String body = "" ;
     @PropertyName("createdAt")
    public Date createdAt = new Date();
     @PropertyName("isRead")
    public boolean isRead = false;
     @PropertyName("title")
    public String title = "" ;
     @PropertyName("typeUser")
    public String typeUser= "" ;

     public long id = 0;

    @Override
    public int compare(NotificationModel t1, NotificationModel t2) {

        if(t1.id == t2.id) return 0;
        else if (t1.id > t2.id) return 1;
        else return -1;

    }
}
