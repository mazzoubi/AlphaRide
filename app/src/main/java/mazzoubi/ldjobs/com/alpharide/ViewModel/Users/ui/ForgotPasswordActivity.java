package mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import mazzoubi.ldjobs.com.alpharide.Data.Users.UserModel;
import mazzoubi.ldjobs.com.alpharide.R;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.UserViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {

    UserModel userModel;
    EditText edtPassword ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtPassword = findViewById(R.id.editTextTextPersonName);

        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        ProgressDialog p = new ProgressDialog(ForgotPasswordActivity.this);
        p.setCancelable(false);
        p.setTitle("الرجاء الإنتظار...");
        p.show();
        FirebaseFirestore.getInstance().collection("Users")
                .whereEqualTo("phoneNumber",phoneNumber)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size()<1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                    builder.setTitle("النظام...");
                    builder.setMessage("انت لم تقم بانشاء الحساب بعد الرجاء انشاء حساب أولاً !");
                    builder.setCancelable(false);
                    builder.setPositiveButton("موافق", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    });
                    builder.show();
                }else {
                    userModel= queryDocumentSnapshots.getDocuments().get(0).toObject(UserModel.class);
                }

            }
        });
    }

    public void onClickSave(View view) {
        String pass = edtPassword.getText().toString();
        if (pass.isEmpty()){
            edtPassword.setError("الإدخال غير صحيح!");
            Toast.makeText(getApplicationContext(), "الرجاء ادخال كلمة المرور الجديدة !", Toast.LENGTH_SHORT).show();
        }else if (pass.length()<6){
            edtPassword.setError("الإدخال غير صحيح!");
            Toast.makeText(getApplicationContext(), "كلمة المرور الجديدة يجب ان تحتوي على 6 خانات على الأقل !", Toast.LENGTH_SHORT).show();
        }else {
            userModel.password = pass ;
            UserViewModel vm = ViewModelProviders.of(this).get(UserViewModel.class);
            vm.forgotPassword(ForgotPasswordActivity.this,userModel);
        }
    }
}