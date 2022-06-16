package mazzoubi.ldjobs.com.alpharide.ViewModel.Users.Cars;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import mazzoubi.ldjobs.com.alpharide.Data.Users.UserInfo_sharedPreference;
import mazzoubi.ldjobs.com.alpharide.Data.Users.UserModel;
import mazzoubi.ldjobs.com.alpharide.R;

public class MyCarsActivity extends AppCompatActivity {

    UserModel user;
    TextView txvType , txvModel , txvNo , txvColor ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cars);
        init();

    }

    void init(){
        user= UserInfo_sharedPreference.getUser(MyCarsActivity.this);
        txvType = findViewById(R.id.txvType);
        txvModel = findViewById(R.id.txvModel);
        txvNo = findViewById(R.id.txvNo);
        txvColor = findViewById(R.id.txvColor);

        txvType.setText(user.carType );
        txvModel.setText(user.carModel );
        txvNo.setText(user.numberCar );
        txvColor.setText(user.carColor );
    }
}