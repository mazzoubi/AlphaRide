package mazzoubi.ldjobs.com.alpharide.Data.Notifications;

import androidx.annotation.Keep;

import com.google.firebase.firestore.PropertyName;

import java.util.Date;

@Keep
public class NotificationModel {

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

}
