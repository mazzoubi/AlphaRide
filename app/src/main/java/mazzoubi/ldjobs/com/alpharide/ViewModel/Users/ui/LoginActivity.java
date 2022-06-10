package mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import mazzoubi.ldjobs.com.alpharide.Data.Users.UserInfo_sharedPreference;
import mazzoubi.ldjobs.com.alpharide.R;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Main.DashboardActivity;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.UserViewModel;

public class LoginActivity extends AppCompatActivity {

    EditText edtPhone , edtPassword ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);

        if (UserInfo_sharedPreference.getUser(LoginActivity.this).uid.isEmpty()||
                UserInfo_sharedPreference.getUser(LoginActivity.this).uid==null){

        }else {
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            finish();
        }

    }

    public void onClickLogin(View view) {
        if (edtPassword.getText().toString().isEmpty()){
            edtPassword.setError("ادخل رقم الهاتف");
        }else if (edtPhone.getText().toString().isEmpty()){
            edtPassword.setError("ادخل كلمة المرور");
        }else{
            UserViewModel vm = ViewModelProviders.of(this).get(UserViewModel.class);
            vm.login(LoginActivity.this,edtPhone.getText().toString(),
                    edtPassword.getText().toString());
        }

    }

    public void onClickRegister(View view) {
        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
    }
}