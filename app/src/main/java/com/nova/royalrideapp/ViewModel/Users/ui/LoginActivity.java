package com.nova.royalrideapp.ViewModel.Users.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

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