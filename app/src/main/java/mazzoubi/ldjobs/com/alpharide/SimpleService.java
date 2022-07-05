package mazzoubi.ldjobs.com.alpharide;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.siddharthks.bubbles.FloatingBubbleConfig;
import com.siddharthks.bubbles.FloatingBubbleService;

import mazzoubi.ldjobs.com.alpharide.ViewModel.Main.MapsActivity;


public class SimpleService extends FloatingBubbleService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null)
            return START_NOT_STICKY;
        if (intent.getAction() == null)
            return super.onStartCommand(intent, flags, startId);
        switch (intent.getAction()){
            case "increase":
                super.increaseNotificationCounterBy(1);
                break;
            case "decrease":
                super.decreaseNotificationCounterBy(1);
                break;
            case "updateIcon":
                super.updateBubbleIcon(ContextCompat.getDrawable(getContext(), R.drawable.close_default_icon));
                break;
            case "restoreIcon":
                super.restoreBubbleIcon();
                break;
            case "toggleExpansion":
                toggleExpansionVisibility();
                break;
        }
        return START_STICKY;
    }

    @Override
    protected FloatingBubbleConfig getConfig() {
        final Context context = getApplicationContext();

        return new FloatingBubbleConfig.Builder()
                .bubbleIcon(ContextCompat.getDrawable(context, R.drawable.logo7))
                .removeBubbleIcon(ContextCompat.getDrawable(context, com.siddharthks.bubbles.R.drawable.close_default_icon))
                .bubbleIconDp(75)
                .expandableView(aa())
                .removeBubbleIconDp(75)
                .paddingDp(4)
                .borderRadiusDp(0)
                .expandableColor(Color.WHITE)
                .triangleColor(0xFF215A64)
                .gravity(Gravity.END)
                .physicsEnabled(true)
                .moveBubbleOnTouch(false)
                .touchClickTime(100)
                .bubbleExpansionIcon(ContextCompat.getDrawable(context, com.siddharthks.bubbles.R.drawable.triangle_icon))
                .build();

    }

    View aa(){//شو هاي ؟
        View view = getInflater().inflate(R.layout.z_bobbles, null) ;
        ImageView imageView = view.findViewById(R.id.imageView3);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(view.getContext(), MapsActivity.class)
//                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                Intent ser_int = new Intent(view.getContext(), SimpleService.class);
                view.getContext().stopService(ser_int);

                Intent i = new Intent(view.getContext(), MapsActivity.class);
                i.setAction(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
        return view;

        // مشان لما تكبس على الباوندل تنقلك على الماب اكتيفيتي
    }

}

