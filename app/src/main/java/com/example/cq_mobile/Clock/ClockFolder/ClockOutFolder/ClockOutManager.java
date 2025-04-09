package com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.cq_mobile.Clock.ApiTimeSheetCallback;
import com.example.cq_mobile.HelperManagers.Animation.ClickAnimationManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LogoutFolder.LogoutManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateAPIFolder.TimeSheetAPI;
import com.example.cq_mobile.ui.home.UpdateJobsFolder.UpdateAPIFolder.TimeSheetColleagueAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ClockOutManager {
    private Context context;
    private ProgressBar progressBar;
    private int savedJobId;
    private int savedTaskId;
    int userID;

    private View rootView;
    TextView clockoutBtn;

    public ClockOutManager(Context context, ProgressBar progressBar, int savedJobId, int savedTaskId, int userID , SharedPrefManager sharedPrefManagerMain ) {
        this.context = context;
        this.progressBar = progressBar;
        this.savedJobId = savedJobId; // ✅
        this.userID = userID;
        this.clockoutBtn = clockoutBtn;

    }


    public void setupClockOutButtonFab(FloatingActionButton clockOutBtnfab, String accessToken, int jobId) {
        SpannableString spannable = new SpannableString("  Clock out");
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.outline_timer_24);
        Log.w("ClockOutManager", " AccessToken : "+"\n" +" -->>  "+ accessToken + "\n" + "savedJobId - " + savedJobId + " jobId - " + savedTaskId );

        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        int startedJobID =  sharedPrefManager.getJobId();
        double userLatitude =  sharedPrefManager.getUserStartJobLatitude();
        double userLongitude =sharedPrefManager.getUserStartJobLongitude();
        double latitude = sharedPrefManager.getStartJobLatitude();
        double longitude = sharedPrefManager.getUserStartJobLongitude();
        String startedDate = sharedPrefManager.getKeyStartDate();
        String stopDate = sharedPrefManager.getKeyStopDate();
        Log.w("ClockOutManager" ," Job Started ->  " +"startedJobID "  + jobId);
        Log.w("ClockOutManager" , " Job Started ->  " +"userLatitude "  +  userLatitude);
        Log.w("ClockOutManager" , " Job Started -> " +"userLongitude "  +  userLongitude);
        Log.w("ClockOutManager" , " Job Started ->  " +"latitude "  +  latitude);
        Log.w("ClockOutManager" , " Job Started ->  " +"longitude "  +  longitude);
        Log.w("ClockOutManager" , " Job Started ->  " +"startedDate "  +  startedDate);
        Log.w("ClockOutManager" , " Job Started ->  " +"stopDate "  +  stopDate);


        Log.w("JobTitle", "ClockOutManager" + "Calling: TimeSheetAPI from setupClockOutButtonFab");

        clockOutBtnfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickAnimationManager.applyClickAnimation(v);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (accessToken != null && !accessToken.isEmpty()) {
                        new Thread(() -> {
                            TimeSheetAPI.sendTimeSheetData(accessToken,userID, userLatitude, userLongitude, latitude, longitude, savedTaskId, progressBar, context, new ApiTimeSheetCallback() {
                                        @Override
                                        public void onSuccess(String serverMessage) {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                Log.w("ClockOutManager", "TimeSheetManager: sendTimeSheetData -- >> " + serverMessage);

                                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                    sharedPrefManager.saveJobSuccessAsFalse(false);
                                                    progressBar.setVisibility(View.GONE);
                                                }, 2000);

                                                AutoClockOut(accessToken, jobId, savedTaskId, startedDate);
                                            });
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            Log.e("ClockOutManager", "Failed to send TimeSheet Data: " + error);
                                            new Handler(Looper.getMainLooper()).post(() ->
                                                    Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show()
                                            );
                                        }
                                    }
                            );
                        }).start();
                    } else {
                        Log.d("ClockOutManager", "Access token is missing!");
                        Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }
    public void setupStopJobWithTimeSheetFab(FloatingActionButton clockoutBtnfab, String accessToken, int jobId, int taskId, SharedPrefManager sharedPrefManager, ProgressBar progressBar) {
        String jobTittle = sharedPrefManager.getStartJob();

        SharedPrefManager sharedPrefManagerWithTimeSheet = new SharedPrefManager(context);
        String startedJobID =  sharedPrefManagerWithTimeSheet.getStartJobID();
        String startedInnerTaskID =  sharedPrefManagerWithTimeSheet.getStartJobIDInnerTask();
        double userLatitude =  sharedPrefManagerWithTimeSheet.getUserStartJobLatitude();
        double userLongitude =sharedPrefManagerWithTimeSheet.getUserStartJobLongitude();
        double latitude = sharedPrefManagerWithTimeSheet.getStartJobLatitude();
        double longitude = sharedPrefManagerWithTimeSheet.getUserStartJobLongitude();
        String startedDate = sharedPrefManagerWithTimeSheet.getKeyStartDate();
        String stopDate = sharedPrefManagerWithTimeSheet.getKeyStopDate();

        int startedJobID_int = 0;
        if (startedJobID != null && !startedJobID.trim().isEmpty()) {
            try {
                startedJobID_int = Integer.parseInt(startedJobID);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        Log.w("ClockOutManager" , "No Job Started "  + " AccessToken:" +
                "\n" + " -->>  " + accessToken + "\n" + "savedJobId - " + jobId + " jobId - " + taskId);
        Log.d("ClockOutManager" , "No Job Started  -> "  + userID);
        Log.d("ClockOutManager" , "No Job Started  -> "  +  jobTittle);

        Log.w("ClockOutManager" ," No Job Started ->  " +"startedID "  + startedJobID + "Converted to Integer: " + startedJobID_int);
        Log.w("ClockOutManager" , " No Job Started -> "  +"startedJobID " + startedInnerTaskID);
        Log.w("ClockOutManager" , " No Job Started -> "  +"startedTaskID " + taskId);
        Log.w("ClockOutManager" , " No Job Started ->  " +"userLatitude "  +  userLatitude);
        Log.w("ClockOutManager" , " No Job Started -> " +"userLongitude "  +  userLongitude);
        Log.w("ClockOutManager" , " No Job Started ->  " +"latitude "  +  latitude);
        Log.w("ClockOutManager" , " No Job Started ->  " +"longitude "  +  longitude);
        Log.w("ClockOutManager" , " No Job Started ->  " +"startedDate "  +  startedDate);
        Log.w("ClockOutManager" , " No Job Started ->  " +"stopDate "  +  stopDate);


        Log.w("JobTitle", "ClockOutManager" + "Calling: TimeSheetColleagueAPI : setupStopJobWithTimeSheetFab");
        int finalStartedJobID_int = startedJobID_int;
        clockoutBtnfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickAnimationManager.applyClickAnimation(v);
                progressBar.setVisibility(View.VISIBLE);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (accessToken != null && !accessToken.isEmpty()) {
                        new Thread(() -> {
                            TimeSheetColleagueAPI.sendTimeSheetColleagueData(accessToken, userID, userLatitude, userLongitude, latitude, longitude
                                    ,finalStartedJobID_int,taskId,progressBar , context, new ApiTimeSheetCallback() {
                                        @Override
                                        public void onSuccess(String serverMessage) {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                Log.w("ClockOutManager "+" TimeSheetColleagueAPI", "TimeSheetManager: sendTimeSheetData -- >> " + serverMessage);
                                                sharedPrefManager.saveJobSuccessAsFalse(false);
                                                AutoStopJobWithTimeSheet(accessToken, jobId, savedTaskId, startedDate);
                                                Toast.makeText(context, serverMessage, Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            });
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            Log.e("ClockOutManager "+" TimeSheetColleagueAPI", "Failed to send TimeSheet Data: " + error);
                                            progressBar.setVisibility(View.GONE);
                                            new Handler(Looper.getMainLooper()).post(() ->
                                                    Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show());
                                        }
                                    }
                            );
                        }).start();
                    } else {
                        Log.d("ClockOutManager", "Access token is missing!");
                        Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show();
                    }
                });

                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(context, "Timer Stopped", Toast.LENGTH_SHORT).show();

                });
            }
        });


    }

    public void setupClockOutButtonLogout(TextView clockOutBtnLogOut, String accessToken, int jobId) {
        SpannableString spannable = new SpannableString("  Confirm Logout");
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.baseline_logout_24);
        Log.w("ClockOutManager", " AccessToken : "+"\n" +" -->>  "+ accessToken + "\n" + "savedJobId - " + savedJobId + " jobId - " + savedTaskId );

        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        int startedJobID =  sharedPrefManager.getJobId();
        double userLatitude =  sharedPrefManager.getUserStartJobLatitude();
        double userLongitude =sharedPrefManager.getUserStartJobLongitude();
        double latitude = sharedPrefManager.getStartJobLatitude();
        double longitude = sharedPrefManager.getUserStartJobLongitude();
        String startedDate = sharedPrefManager.getKeyStartDate();
        String stopDate = sharedPrefManager.getKeyStopDate();
        Log.w("ClockOutManager" ," Job Started ->  " +"startedJobID "  + jobId);
        Log.w("ClockOutManager" , " Job Started ->  " +"userLatitude "  +  userLatitude);
        Log.w("ClockOutManager" , " Job Started -> " +"userLongitude "  +  userLongitude);
        Log.w("ClockOutManager" , " Job Started ->  " +"latitude "  +  latitude);
        Log.w("ClockOutManager" , " Job Started ->  " +"longitude "  +  longitude);
        Log.w("ClockOutManager" , " Job Started ->  " +"startedDate "  +  startedDate);
        Log.w("ClockOutManager" , " Job Started ->  " +"stopDate "  +  stopDate);
        Log.w("JobTitle", "ClockOutManager" + "Calling: TimeSheetAPI ");
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.white));

            int drawableSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
            drawable.setBounds(0, 0, drawableSize, drawableSize);

            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        }

        Log.w("JobTitle", "ClockOutManager" + "Calling: TimeSheetAPI ");

        spannable.setSpan(new android.text.style.RelativeSizeSpan(1.2f), 2, spannable.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        clockOutBtnLogOut.setText(spannable);
        clockOutBtnLogOut.setGravity(Gravity.CENTER);
        clockOutBtnLogOut.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);


        clockOutBtnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickAnimationManager.applyClickAnimation(v);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (accessToken != null && !accessToken.isEmpty()) {
                        new Thread(() -> {
                            TimeSheetAPI.sendTimeSheetData(accessToken,userID, userLatitude, userLongitude, latitude, longitude, savedTaskId, progressBar, context, new ApiTimeSheetCallback() {
                                        @Override
                                        public void onSuccess(String serverMessage) {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                Log.w("ClockOutManager", "TimeSheetManager: sendTimeSheetData -- >> " + serverMessage);

                                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                    sharedPrefManager.saveJobSuccessAsFalse(false);
                                                    progressBar.setVisibility(View.GONE);
                                                }, 2000);

                                                AutoClockOut(accessToken, jobId, savedTaskId, startedDate);
                                            });
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            Log.e("ClockOutManager", "Failed to send TimeSheet Data: " + error);
                                            new Handler(Looper.getMainLooper()).post(() ->
                                                    Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show()
                                            );
                                        }
                                    }
                            );
                        }).start();
                    } else {
                        Log.d("ClockOutManager", "Access token is missing!");
                        Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }
    public void setupStopJobWithTimeSheetLogout(TextView clockoutBtnLogOut, String accessToken, int jobId, int taskId, SharedPrefManager sharedPrefManager, ProgressBar progressBar) {
        String jobTittle = sharedPrefManager.getStartJob();

        SharedPrefManager sharedPrefManagerWithTimeSheet = new SharedPrefManager(context);
        String startedJobID =  sharedPrefManagerWithTimeSheet.getStartJobID();
        String startedInnerTaskID =  sharedPrefManagerWithTimeSheet.getStartJobIDInnerTask();
        double userLatitude =  sharedPrefManagerWithTimeSheet.getUserStartJobLatitude();
        double userLongitude =sharedPrefManagerWithTimeSheet.getUserStartJobLongitude();
        double latitude = sharedPrefManagerWithTimeSheet.getStartJobLatitude();
        double longitude = sharedPrefManagerWithTimeSheet.getUserStartJobLongitude();
        String startedDate = sharedPrefManagerWithTimeSheet.getKeyStartDate();
        String stopDate = sharedPrefManagerWithTimeSheet.getKeyStopDate();

        int startedJobID_int = 0;
        if (startedJobID != null && !startedJobID.trim().isEmpty()) {
            try {
                startedJobID_int = Integer.parseInt(startedJobID);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        Log.w("ClockOutManager" , "No Job Started "  + " AccessToken:" +
                "\n" + " -->>  " + accessToken + "\n" + "savedJobId - " + jobId + " jobId - " + taskId);
        Log.d("ClockOutManager" , "No Job Started  -> "  + userID);
        Log.d("ClockOutManager" , "No Job Started  -> "  +  jobTittle);

        Log.w("ClockOutManager" ," No Job Started ->  " +"startedID "  + startedJobID + "Converted to Integer: " + startedJobID_int);
        Log.w("ClockOutManager" , " No Job Started -> "  +"startedJobID " + startedInnerTaskID);
        Log.w("ClockOutManager" , " No Job Started -> "  +"startedTaskID " + taskId);
        Log.w("ClockOutManager" , " No Job Started ->  " +"userLatitude "  +  userLatitude);
        Log.w("ClockOutManager" , " No Job Started -> " +"userLongitude "  +  userLongitude);
        Log.w("ClockOutManager" , " No Job Started ->  " +"latitude "  +  latitude);
        Log.w("ClockOutManager" , " No Job Started ->  " +"longitude "  +  longitude);
        Log.w("ClockOutManager" , " No Job Started ->  " +"startedDate "  +  startedDate);
        Log.w("ClockOutManager" , " No Job Started ->  " +"stopDate "  +  stopDate);

        Log.w("JobTitle", "ClockOutManager" + "Calling: TimeSheetColleagueAPI ");
        int finalStartedJobID_int = startedJobID_int;
        clockoutBtnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickAnimationManager.applyClickAnimation(v);
                progressBar.setVisibility(View.VISIBLE);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (accessToken != null && !accessToken.isEmpty()) {
                        new Thread(() -> {
                            TimeSheetColleagueAPI.sendTimeSheetColleagueData(accessToken, userID, userLatitude, userLongitude, latitude, longitude
                                    ,finalStartedJobID_int,taskId,progressBar , context, new ApiTimeSheetCallback(){
                                        @Override
                                        public void onSuccess(String serverMessage) {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                Log.w("ClockOutManager "+" TimeSheetColleagueAPI", "TimeSheetManager: sendTimeSheetData -- >> " + serverMessage);
                                                sharedPrefManager.saveJobSuccessAsFalse(false);
                                                AutoStopJobWithTimeSheet(accessToken, jobId, savedTaskId, startedDate);
                                                Toast.makeText(context, serverMessage, Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            });
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            Log.e("ClockOutManager "+" TimeSheetColleagueAPI", "Failed to send TimeSheet Data: " + error);
                                            progressBar.setVisibility(View.GONE);
                                            new Handler(Looper.getMainLooper()).post(() ->
                                                    Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show());
                                        }
                                    }
                            );
                        }).start();
                    } else {
                        Log.d("ClockOutManager", "Access token is missing!");
                        Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show();
                    }
                });

                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(context, "Timer Stopped", Toast.LENGTH_SHORT).show();

                });
            }
        });


    }



    ///ClockOut Without Logout
    public void setupClockOutButton(TextView clockOutBtn, String accessToken, int jobId) {
        SpannableString spannable = new SpannableString("  Clock out");
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.outline_timer_24);
        Log.w("ClockOutManager", " AccessToken : "+"\n" +" -->>  "+ accessToken + "\n" + "savedJobId - " + savedJobId + " jobId - " + savedTaskId );

        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        int startedJobID =  sharedPrefManager.getJobId();
        double userLatitude =  sharedPrefManager.getUserStartJobLatitude();
        double userLongitude =sharedPrefManager.getUserStartJobLongitude();
        double latitude = sharedPrefManager.getStartJobLatitude();
        double longitude = sharedPrefManager.getUserStartJobLongitude();
        String startedDate = sharedPrefManager.getKeyStartDate();
        String stopDate = sharedPrefManager.getKeyStopDate();

        Log.w("ClockOutManager" ," Job Started ->  " +"startedJobID "  + jobId);
        Log.w("ClockOutManager" , " Job Started ->  " +"userLatitude "  +  userLatitude);
        Log.w("ClockOutManager" , " Job Started -> " +"userLongitude "  +  userLongitude);
        Log.w("ClockOutManager" , " Job Started ->  " +"latitude "  +  latitude);
        Log.w("ClockOutManager" , " Job Started ->  " +"longitude "  +  longitude);
        Log.w("ClockOutManager" , " Job Started ->  " +"startedDate "  +  startedDate);
        Log.w("ClockOutManager" , " Job Started ->  " +"stopDate "  +  stopDate);


        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.white));

            int drawableSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
            drawable.setBounds(0, 0, drawableSize, drawableSize);

            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        }

        Log.w("JobTitle", "ClockOutManager" + "Calling: TimeSheetAPI ");

        spannable.setSpan(new android.text.style.RelativeSizeSpan(1.2f), 2, spannable.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        clockOutBtn.setText(spannable);
        clockOutBtn.setGravity(Gravity.CENTER);
        clockOutBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        clockOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickAnimationManager.applyClickAnimation(v);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (accessToken != null && !accessToken.isEmpty()) {
                        new Thread(() -> {
                            TimeSheetAPI.sendTimeSheetData(accessToken,userID, userLatitude, userLongitude, latitude, longitude, savedTaskId, progressBar, context, new ApiTimeSheetCallback() {
                                        @Override
                                        public void onSuccess(String serverMessage) {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                Log.w("ClockOutManager", "TimeSheetManager: sendTimeSheetData -- >> " + serverMessage);

                                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                    sharedPrefManager.saveJobSuccessAsFalse(false);
                                                    progressBar.setVisibility(View.GONE);
                                                }, 2000);

                                                AutoClockOut(accessToken, jobId, savedTaskId, startedDate);
                                            });
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            Log.e("ClockOutManager", "Failed to send TimeSheet Data: " + error);
                                            new Handler(Looper.getMainLooper()).post(() ->
                                                    Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show()
                                            );
                                        }
                                    }
                            );
                        }).start();
                    } else {
                        Log.d("ClockOutManager", "Access token is missing!");
                        Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }
    public void AutoClockOut(String accessToken, int jobId, int savedTaskId, String startDate) {
        initiateClockOut(accessToken, jobId, this.savedTaskId, startDate);
    }
    private void initiateClockOut(String accessToken, int jobId, int taskId, String startDate) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        new Handler(Looper.getMainLooper()).post(() -> {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            if (startDate == null || startDate.trim().isEmpty()) {
                progressBar.setVisibility(View.GONE);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    LogoutManager.logoutUser(context);
                }, 3000);

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Job Stopped Successfully")
                        .setPositiveButton("ok", (dialog, which) -> {
                            dialog.dismiss();
                        }).setCancelable(true)
                        .show();
            }

        });

    }


    ///StopJob With Logout
    public void setupStopJobWithTimeSheet(TextView clockoutBtn, String accessToken, int jobId, int taskId, SharedPrefManager sharedPrefManager, ProgressBar progressBar) {
        SpannableString spannable = new SpannableString("  Clock out");
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.outline_timer_24);
        String jobTittle = sharedPrefManager.getStartJob();

        SharedPrefManager sharedPrefManagerWithTimeSheet = new SharedPrefManager(context);
        String startedJobID =  sharedPrefManagerWithTimeSheet.getStartJobID();
        String startedInnerTaskID =  sharedPrefManagerWithTimeSheet.getStartJobIDInnerTask();
        double userLatitude =  sharedPrefManagerWithTimeSheet.getUserStartJobLatitude();
        double userLongitude =sharedPrefManagerWithTimeSheet.getUserStartJobLongitude();
        double latitude = sharedPrefManagerWithTimeSheet.getStartJobLatitude();
        double longitude = sharedPrefManagerWithTimeSheet.getUserStartJobLongitude();
        String startedDate = sharedPrefManagerWithTimeSheet.getKeyStartDate();
        String stopDate = sharedPrefManagerWithTimeSheet.getKeyStopDate();
        int startedJobID_int = 0;
        if (startedJobID != null && !startedJobID.trim().isEmpty()) {
            try {
                startedJobID_int = Integer.parseInt(startedJobID);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        Log.w("ClockOutManager" , "No Job Started "  + " AccessToken:" +
                "\n" + " -->>  " + accessToken + "\n" + "savedJobId - " + jobId + " jobId - " + taskId);
        Log.d("ClockOutManager" , "No Job Started  -> "  + userID);
        Log.d("ClockOutManager" , "No Job Started  -> "  +  jobTittle);

        Log.w("ClockOutManager" ," No Job Started ->  " +"startedID "  + startedJobID + "Converted to Integer: " + startedJobID_int);
        Log.w("ClockOutManager" , " No Job Started -> "  +"startedJobID " + startedInnerTaskID);
        Log.w("ClockOutManager" , " No Job Started -> "  +"startedTaskID " + taskId);
        Log.w("ClockOutManager" , " No Job Started ->  " +"userLatitude "  +  userLatitude);
        Log.w("ClockOutManager" , " No Job Started -> " +"userLongitude "  +  userLongitude);
        Log.w("ClockOutManager" , " No Job Started ->  " +"latitude "  +  latitude);
        Log.w("ClockOutManager" , " No Job Started ->  " +"longitude "  +  longitude);
        Log.w("ClockOutManager" , " No Job Started ->  " +"startedDate "  +  startedDate);
        Log.w("ClockOutManager" , " No Job Started ->  " +"stopDate "  +  stopDate);


        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.white));

            int drawableSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
            drawable.setBounds(0, 0, drawableSize, drawableSize);

            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        }

        Log.w("JobTitle", "ClockOutManager" + "Calling: TimeSheetColleagueAPI ");



        spannable.setSpan(new android.text.style.RelativeSizeSpan(1.2f), 2, spannable.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        clockoutBtn.setText(spannable);
        clockoutBtn.setGravity(Gravity.CENTER);
        clockoutBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        int finalStartedJobID_int = startedJobID_int;
        clockoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickAnimationManager.applyClickAnimation(v);
                progressBar.setVisibility(View.VISIBLE);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (accessToken != null && !accessToken.isEmpty()) {
                        new Thread(() -> {
                            TimeSheetColleagueAPI.sendTimeSheetColleagueData(accessToken, userID, userLatitude, userLongitude, latitude, longitude,finalStartedJobID_int,taskId,progressBar , context, new ApiTimeSheetCallback() {
                                        @Override
                                        public void onSuccess(String serverMessage) {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                Log.w("ClockOutManager "+" TimeSheetColleagueAPI", "TimeSheetManager: sendTimeSheetData -- >> " + serverMessage);
                                                sharedPrefManager.saveJobSuccessAsFalse(false);
                                                AutoStopJobWithTimeSheet(accessToken, jobId, savedTaskId, startedDate);
                                                Toast.makeText(context, serverMessage, Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            });
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            Log.e("ClockOutManager "+" TimeSheetColleagueAPI", "Failed to send TimeSheet Data: " + error);
                                            progressBar.setVisibility(View.GONE);
                                            new Handler(Looper.getMainLooper()).post(() ->
                                                    Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show());
                                        }
                                    }
                            );
                        }).start();
                    } else {
                        Log.d("ClockOutManager", "Access token is missing!");
                        Toast.makeText(context, "Unable to Create Time Sheet", Toast.LENGTH_SHORT).show();
                    }
                });

                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(context, "Timer Stopped", Toast.LENGTH_SHORT).show();

                });
            }
        });


    }
    private void AutoStopJobWithTimeSheet(String accessToken, int jobId, int taskId, String startDate) {
        initiateStopTimeSheetColleagueAPI(accessToken, jobId, taskId,startDate);
    }
    private void initiateStopTimeSheetColleagueAPI(String accessToken, int jobId, int taskId, String startDate) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        new Handler(Looper.getMainLooper()).post(() -> {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            clearStopTimeSheetColleagueAPI(startDate);
        });

    }


    private void clearStopTimeSheetColleagueAPI(String startDate) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        Log.w("ClockOutManager", "<<<< TimerManager >>>> "+"\n" +" -->>  "+ startDate );

    }






}




/*

A. CLocked In
        : api/m/start-working/timed_in/{user_id}
            Method: Post
            Payload: {
                "user_id": 379,//optiotn - if not set the authenticated user will be use.
                "lat_out": 15.1453696,
                "long_out": 120.5960704
            }

    B. Start Job:
        : api/m/jobs/work-status/start/{user_id}
            Method: Put
            Payload: {
                "e": "jobs",
                "status": "start",
                "job": 5803,
                "custom_job": null,
                "lat_out": 15.1486464,
                "long_out": 120.6059008,
                "manual": 1
            }

    C. Stop Job:
        : api/m/time-sheet/store/{job_id}
            Method: Post
            Payload: {
                "user": 379,
                "lat": 15.1449853,
                "long": 120.5887029,
                "job": 2709,
                "date": 2025-03-21,
                "start": 09:28,
                "end": 09:33,
                "remarks": "",
                "lat_out": 15.1449853,
                "long_out": 120.5887029,
                "manual": 1
            }

        : api/m/jobs/work-status/stop/{user_id}
            Method: Put
            Payload: {
                "user_id": "jobs",
                "status": "stop",
            }

    D. Clock Out:
        : api/m/time-sheet/store/colleague/{user_id}
            Method: POST
            Payload: {
                "user": 379,
                "lat": 15.2073561,
                "long": 120.6534098,
                "date": "2025-03-21",
                "start": 09:51,
                "end": 09:53,
                "clocked_out": 1,
                "remarks": "",
                "lat_out": 15.1449853,
                "long_out": 120.5887029,
                "manual": 1
            }

        : api/m/start-working/timed_out/{user_id}
            Method: PUT
            Payload: {
                "user_id": 379,
                "status": "stop"
            }

 */

/*

 */