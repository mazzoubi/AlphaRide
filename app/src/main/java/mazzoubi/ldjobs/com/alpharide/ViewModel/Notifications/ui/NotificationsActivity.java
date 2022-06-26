package mazzoubi.ldjobs.com.alpharide.ViewModel.Notifications.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import mazzoubi.ldjobs.com.alpharide.Data.Notifications.NotificationModel;
import mazzoubi.ldjobs.com.alpharide.R;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Notifications.Adapters.NotificationsAdapter;
import mazzoubi.ldjobs.com.alpharide.ViewModel.Notifications.NotificationViewModel;

public class NotificationsActivity extends AppCompatActivity {

    ListView listView ;
    TextView txvWaiting ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_notifications);
        init();


    }

    void init(){
        listView = findViewById(R.id.listView);
        txvWaiting = findViewById(R.id.textView19);
        getNotifications();


    }

    void getNotifications(){
        NotificationViewModel vm = ViewModelProviders.of(this).get(NotificationViewModel.class);
        vm.getNotifications(NotificationsActivity.this);
        vm.listOfNotifications.observe(this, new Observer<ArrayList<NotificationModel>>() {
            @Override
            public void onChanged(ArrayList<NotificationModel> notificationModels) {
                if (notificationModels.size()<1){
                    txvWaiting.setText("لا يوجد اشعارات حالياً");
                }else {
                    txvWaiting.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    ArrayAdapter<NotificationModel> adapter = new NotificationsAdapter(NotificationsActivity.this,
                            R.layout.row_notifications,notificationModels);
                    listView.setAdapter(adapter);
                }
            }
        });
    }
}