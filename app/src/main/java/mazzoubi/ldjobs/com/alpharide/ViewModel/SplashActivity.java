package mazzoubi.ldjobs.com.alpharide.ViewModel;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import mazzoubi.ldjobs.com.alpharide.MainActivity;
import mazzoubi.ldjobs.com.alpharide.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        String msg = "Whenever you want. Wherever you are";
        final TextView tv = findViewById(R.id.intro);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        tv.setText("");
                        tv.setTextColor(Color.parseColor("#AE994C"));
                        for (int i = 0; i < msg.length(); i++) {
                            sleep(80);
                            tv.setText(tv.getText().toString() + msg.charAt(i));
                        }
                        sleep(2000);
                        logTo();
                    } catch (InterruptedException e) {
                    }
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        sleep(3000);
                        logTo();
                    } catch (InterruptedException e) {
                    }
                }
            }).start();
        }
    }

    private void logTo() {

        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();

    }
}