package mazzoubi.ldjobs.com.alpharide.ViewModel.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import mazzoubi.ldjobs.com.alpharide.R;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_contact_us);
    }

    public void onClickSendEmail(View view) {
        Intent email= new Intent(Intent.ACTION_SENDTO);
        email.setData(Uri.parse("mailto:almabdallah48@gmail.com"));
        email.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        email.putExtra(Intent.EXTRA_TEXT, "My Email message");
        startActivity(email);
    }

    public void onClickLiveChat(View view) {
        startActivity(new Intent(getApplicationContext(),LiveChatActivity.class));
    }
}