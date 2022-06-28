package mazzoubi.ldjobs.com.alpharide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import mazzoubi.ldjobs.com.alpharide.Data.Users.UserInfo_sharedPreference;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.UserViewModel;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui.LoginActivity;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui.RegisterActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    1); }

        if (UserInfo_sharedPreference.getUser(MainActivity.this).uid==null||
                UserInfo_sharedPreference.getUser(MainActivity.this).uid.equals("")){

        }else {
            UserViewModel vm = ViewModelProviders.of(this).get(UserViewModel.class);
            vm.login(MainActivity.this,UserInfo_sharedPreference.getUser(MainActivity.this).phoneNumber,
                    UserInfo_sharedPreference.getUser(MainActivity.this).password);
        }
    }

    public void Login(View view) {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
    public void Register(View view) {
        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
    }

    @Override
    public void onBackPressed() {
        MainActivity.this.finishAffinity();
    }
}