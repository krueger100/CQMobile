package com.example.cq_mobile.HelperManagers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

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
