package mazzoubi.ldjobs.com.alpharide.ViewModel.Wallet.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mazzoubi.ldjobs.com.alpharide.ClassDate;
import mazzoubi.ldjobs.com.alpharide.Data.Users.UserInfo_sharedPreference;
import mazzoubi.ldjobs.com.alpharide.R;
import mazzoubi.ldjobs.com.alpharide.ScratchCardsClass;

public class WalletActivity extends AppCompatActivity {

    TextView txt;
    String Balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_wallet);

        txt = findViewById(R.id.balance);

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(UserInfo_sharedPreference.getUser(WalletActivity.this).uid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Balance = documentSnapshot.get("balance").toString();
                txt.setText("الرصيد الحالي : "+documentSnapshot.get("balance").toString()+" دينار ");

            }
        });

    }

    public void CheckCard(View view) {

        EditText edt = findViewById(R.id.edittext);
        FirebaseFirestore.getInstance()
                .collection("ScratchCards")
                .whereEqualTo("serial", edt.getText().toString())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                try{
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    ScratchCardsClass obj = list.get(0).toObject(ScratchCardsClass.class);
                    if(obj.available.equals("0"))
                        Toast.makeText(WalletActivity.this, "هذه البطاقة مستخدمة مسبقا", Toast.LENGTH_SHORT).show();
                    else if(!obj.serial.equals(edt.getText().toString()))
                        Toast.makeText(WalletActivity.this, "لم يتم العثور على البطاقة", Toast.LENGTH_SHORT).show();
                    else{
                        new AlertDialog.Builder(WalletActivity.this)
                                .setMessage("هذه البطاقة بعنوان "+obj.title+" وتقوم بإضافة رصيد فعلي بقيمة "+obj.value+" تنتهي صلاحية هذه البطاقة بتاريخ "+obj.expire_date+" هل ترغب بشحن الرصيد ؟")
                                .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).setPositiveButton("شحن", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(WalletActivity.this, "يرجى الإنتظار..", Toast.LENGTH_SHORT).show();
                                double a = Double.parseDouble(Balance)+Double.parseDouble(obj.value);
                                double x = new BigDecimal(a).setScale(2, RoundingMode.HALF_UP).doubleValue();
                                Map<String, Object> map = new HashMap<>();
                                map.put("available", "0");
                                map.put("balance_before", Balance+"");
                                map.put("balance_after", x + "");
                                map.put("withdraw_by_id", UserInfo_sharedPreference.getUser(WalletActivity.this).uid);
                                map.put("withdraw_by_name", UserInfo_sharedPreference.getUser(WalletActivity.this).fullName);
                                map.put("withdraw_date", ClassDate.date());
                                map.put("withdraw_time", ClassDate.time());

                                FirebaseFirestore.getInstance()
                                        .collection("ScratchCards")
                                        .document(obj.cid)
                                        .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        FirebaseFirestore.getInstance()
                                                .collection("Users")
                                                .document(UserInfo_sharedPreference.getUser(WalletActivity.this).uid)
                                                .update("balance", x).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                Toast.makeText(WalletActivity.this, "تم شحن الرصيد بنجاح", Toast.LENGTH_SHORT).show();
                                                recreate();

                                            }
                                        });

                                    }
                                });
                            }
                        }).setCancelable(false).create().show();
                    }
                }
                catch (Exception ex){
                    Toast.makeText(WalletActivity.this, "لم يتم العثور على البطاقة..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}