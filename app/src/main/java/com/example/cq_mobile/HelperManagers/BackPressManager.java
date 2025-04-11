package com.example.cq_mobile.HelperManagers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
/*
public class BackPressManager {

    private Context context;

    public BackPressManager(Context context) {
        this.context = context;
    }
    public void handleBackPress(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(context, activityClass);
        context.startActivity(intent);

        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }
}

 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.FragmentManager;

import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;

public class BackPressManager {

    private Context context;

    public BackPressManager(Context context) {
        this.context = context;
    }

    public void handleBackPress(Class<? extends Activity> activityClass) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            FragmentManager fragmentManager = ((androidx.fragment.app.FragmentActivity) activity).getSupportFragmentManager();

            new Handler(Looper.getMainLooper()).post(() -> {
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                } else {
                    Intent intent = new Intent(context, activityClass);
                    context.startActivity(intent);
                    activity.finish();
                }
            });
        }
    }
}



/*
                     // for bottom to top animation
                     // overridePendingTransition(R.anim.slide_from_bottom,R.anim.slide_to_top);

                   // for top to bottom animation
                   // overridePendingTransition(R.anim.slide_from_top,R.anim.slide_to_bottom);
 */
