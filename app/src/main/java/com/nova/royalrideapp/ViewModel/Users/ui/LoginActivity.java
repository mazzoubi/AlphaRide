package com.nova.royalrideapp.ViewModel.Users.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nova.royalrideapp.MainActivity;
import com.nova.royalrideapp.ViewModel.Main.ContactUsActivity;
import com.hbb20.CountryCodePicker;

import com.nova.royalrideapp.Data.Users.UserModel;
import com.nova.royalrideapp.R;
import com.nova.royalrideapp.ViewModel.Users.UserViewModel;

public class LoginActivity extends AppCompatActivity {

    EditText edtPhone , edtPassword ;
    CountryCodePicker countryCode_picker ;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

//        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
//        registerActivityLifecycleCallbacks(handler);
//        registerComponentCallbacks(handler);

        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);
        countryCode_picker = findViewById(R.id.countryCode_picker);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(LoginActivity.this, new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    1); }

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        }

    }

    public void onClickLogin(View view) {
        if (edtPassword.getText().toString().isEmpty()){
            edtPassword.setError("ادخل رقم الهاتف");
        }else if (edtPhone.getText().toString().isEmpty()){
            edtPassword.setError("ادخل كلمة المرور");
        }else{
            UserViewModel vm = ViewModelProviders.of(this).get(UserViewModel.class);
            vm.login(LoginActivity.this,
                    countryCode_picker.getSelectedCountryCodeWithPlus()+edtPhone.getText().toString(),
                    edtPassword.getText().toString());
        }

    }

    public void onClickForgotPassword(View view) {
        if (edtPhone.getText().toString().isEmpty()){
            edtPhone.setError("ادخل رقم الهاتف");
        }else {
            RegisterActivity.userModel = new UserModel();
            RegisterActivity.userModel.phoneNumber = countryCode_picker.getSelectedCountryCodeWithPlus()+edtPhone.getText().toString();
            startActivity(new Intent(getApplicationContext(), VerifyPhoneActivity.class)
            .putExtra("fromWhere","forgotPassword"));
        }
    }

    public void support(View view) {
        startActivity(new Intent(LoginActivity.this, ContactUsActivity.class));
    }
}