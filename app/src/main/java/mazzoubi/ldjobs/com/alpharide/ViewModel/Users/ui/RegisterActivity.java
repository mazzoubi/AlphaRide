package mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

import mazzoubi.ldjobs.com.alpharide.Data.Users.UserModel;
import mazzoubi.ldjobs.com.alpharide.R;

public class RegisterActivity extends AppCompatActivity {

    FrameLayout frameLayout;

    public static UserModel userModel ;
    public static Activity activity ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();


    }

    void init(){
        activity = RegisterActivity.this;
        userModel = new UserModel();
        frameLayout = findViewById(R.id.frameLayout);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new UserInfoFragment()).commit();
    }
}