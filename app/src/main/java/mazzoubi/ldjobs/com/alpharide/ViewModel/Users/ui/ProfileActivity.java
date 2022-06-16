package mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import mazzoubi.ldjobs.com.alpharide.Data.Users.UserInfo_sharedPreference;
import mazzoubi.ldjobs.com.alpharide.Data.Users.UserModel;
import mazzoubi.ldjobs.com.alpharide.R;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.UserViewModel;

public class ProfileActivity extends AppCompatActivity {

    EditText edtName , edtPhone , edtEmail , edtPassword ;
    Button btnSave ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();

    }


    void init(){
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSave = findViewById(R.id.btnSave);

        edtName.setText(UserInfo_sharedPreference.getUser(ProfileActivity.this).fullName);
        edtPhone.setText(UserInfo_sharedPreference.getUser(ProfileActivity.this).phoneNumber);
        edtEmail.setText(UserInfo_sharedPreference.getUser(ProfileActivity.this).email);
        edtPassword.setText(UserInfo_sharedPreference.getUser(ProfileActivity.this).password);

        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edtName.getText().toString().equals(UserInfo_sharedPreference
                        .getUser(ProfileActivity.this).fullName)){
                    btnSave.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edtEmail.getText().toString().equals(UserInfo_sharedPreference
                        .getUser(ProfileActivity.this).email)){
                    btnSave.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edtPassword.getText().toString().equals(UserInfo_sharedPreference
                        .getUser(ProfileActivity.this).password)){
                    btnSave.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    public void onClickSave(View view) {
        if (edtName.getText().toString().isEmpty()){
            edtName.setError("الاسم مطلوب!");
        }else if (edtEmail.getText().toString().isEmpty()){
            edtEmail.setError("الاميل مطلوب!");
        }else if (edtPassword.getText().toString().isEmpty()){
            edtPassword.setError("كلمة المرور مطلوبه!");
        }else {
            UserModel userModel = UserInfo_sharedPreference.getUser(ProfileActivity.this);
            userModel.fullName = edtName.getText().toString();
            userModel.email = edtEmail.getText().toString();
            userModel.password = edtPassword.getText().toString();
            UserViewModel vm = ViewModelProviders.of(this).get(UserViewModel.class);
            vm.UpdateUser(ProfileActivity.this,userModel);
        }
    }
}