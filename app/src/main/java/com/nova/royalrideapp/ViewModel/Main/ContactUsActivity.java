package com.nova.royalrideapp.ViewModel.Main;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nova.royalrideapp.R;
public class ContactUsActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_contact_us);

//        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
//        registerActivityLifecycleCallbacks(handler);
//        registerComponentCallbacks(handler);
    }

    public void onClickSendEmail(View view) {
        Intent email= new Intent(Intent.ACTION_SENDTO);
        email.setData(Uri.parse("mailto:royal.ride.app@gmail.com"));
        email.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        email.putExtra(Intent.EXTRA_TEXT, "My Email message");
        startActivity(email);
    }

    public void onClickLiveChat(View view) {
//        startActivity(new Intent(getApplicationContext(),LiveChatActivity.class));
        openWhatsappContact();
    }
    void openWhatsappContact() {

        FirebaseFirestore.getInstance()
                .collection("AdminDataConfig")
                .document("Data")
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + documentSnapshot.getString("SupportMobile"));
                Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                ContactUsActivity.this.startActivity(sendIntent);
            }
        });
    }
}