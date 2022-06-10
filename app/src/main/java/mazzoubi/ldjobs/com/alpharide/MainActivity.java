package mazzoubi.ldjobs.com.alpharide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui.LoginActivity;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui.RegisterActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }catch (Exception e){}
            }
        }.start();
    }
}