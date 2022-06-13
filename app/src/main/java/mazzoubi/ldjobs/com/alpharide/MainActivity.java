package mazzoubi.ldjobs.com.alpharide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui.LoginActivity;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Users.ui.RegisterActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Glide.with(MainActivity.this)
                .load(R.drawable.main_logo)
                .into((ImageView) findViewById(R.id.gif));

    }

    public void Register(View view) {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
}