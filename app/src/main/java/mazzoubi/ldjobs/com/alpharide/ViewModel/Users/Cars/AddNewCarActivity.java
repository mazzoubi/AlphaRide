package mazzoubi.ldjobs.com.alpharide.ViewModel.Users.Cars;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;

import mazzoubi.ldjobs.com.alpharide.ApplicationLifecycleHandler;
import mazzoubi.ldjobs.com.alpharide.Data.Users.DriverRequestAccountModel;
import mazzoubi.ldjobs.com.alpharide.Data.Users.UserModel;
import mazzoubi.ldjobs.com.alpharide.R;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.Cars.ui.NewCarInfoFragment;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui.RegisterActivity;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui.UserInfoFragment;

public class AddNewCarActivity extends AppCompatActivity {

    FrameLayout frameLayout;

    public static UserModel userModel ;
    public static DriverRequestAccountModel driverRequestAccountModel;
    public static Activity activity ;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_new_car);
        init();

        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
        registerActivityLifecycleCallbacks(handler);
        registerComponentCallbacks(handler);

    }

    void init(){
        activity = AddNewCarActivity.this;
        userModel = new UserModel();
        driverRequestAccountModel = new DriverRequestAccountModel();
        frameLayout = findViewById(R.id.frameLayout);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new NewCarInfoFragment()).commit();
    }

}