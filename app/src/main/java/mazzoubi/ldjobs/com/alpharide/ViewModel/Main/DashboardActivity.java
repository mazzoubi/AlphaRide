package mazzoubi.ldjobs.com.alpharide.ViewModel.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import mazzoubi.ldjobs.com.alpharide.Data.Users.UserInfo_sharedPreference;
import mazzoubi.ldjobs.com.alpharide.R;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
//        UserInfo_sharedPreference.logout(DashboardActivity.this);
    }
}