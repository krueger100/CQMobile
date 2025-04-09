package com.example.cq_mobile.SplashFolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LoginFolder.AuthManager;
import com.example.cq_mobile.LoginFolder.Login;
import com.example.cq_mobile.MainActivity;

public class SplashManager {
    private Context context;
    private SharedPrefManager sharedPrefManager;
    private ProgressBar progressBar;

    public SplashManager(Context context, ProgressBar progressBar) {
        this.context = context;
        this.sharedPrefManager = new SharedPrefManager(context);
        this.progressBar = progressBar;
    }


    public void checkCreadentials(boolean isClockedIn, boolean isLoggedIn) {

        if (isClockedIn) {
            Log.d("SplashManager", "User is CLOCKIN");
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent;
                if (isLoggedIn) {
                    intent = new Intent(context, MainActivity.class);
                    Log.d("SplashManager", "User is LoggedIn , Navigate to Main Page.");

                } else {
                    intent = new Intent(context, Login.class);
                    Log.d("SplashManager", "User is not LoggedIn , Navigate to Login Page.");

                }
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }, 500);



        } else {
            Log.d("SplashManager", "User is NOT CLOCKIN");
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent;
                if (isLoggedIn) {
                    intent = new Intent(context, MainActivity.class);
                    Log.d("SplashManager", "User is LoggedIn , Navigate to Main Page.");

                } else {
                    intent = new Intent(context, Login.class);
                    sharedPrefManager = new SharedPrefManager(context);
                    String accessToken = AuthManager.getInstance(context).getToken();
                    int userId = sharedPrefManager.getUserId();
                    Log.d("SplashManager", "User is NOT LoggedIn , Navigate to Login Page.");

                }
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }, 500);

        }


    }




}


/*

  autoClockOut(accessToken,userId);

    private void autoClockOut(String accessToken, int userId) {
        int savedJobId = sharedPrefManager.getJobId();
        int savedTaskId = sharedPrefManager.getTaskId();


        if (userId == -1) {
            Log.w("SplashManager", "Skipping AutoClockOut: Missing userID.");
            return;
        }

//         Clear Clock in Data
        SharedPreferences sharedPreferences = context.getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();



        ClockOutManager clockOutManager = new ClockOutManager(context, progressBar, savedJobId, savedTaskId,userId);
        clockOutManager.AutoClockOut(accessToken, savedJobId);
    }
 */
