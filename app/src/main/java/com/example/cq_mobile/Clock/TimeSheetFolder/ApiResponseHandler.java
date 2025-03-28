package com.example.cq_mobile.Clock.TimeSheetFolder;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cq_mobile.LogoutFolder.LogoutManager;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

class ApiResponseHandler implements Callback {
    private final ProgressBar progressBar;
    private final Context context;
    private final ApiTSCallback callback;
    private final boolean isColleague;

    public ApiResponseHandler(ProgressBar progressBar, Context context, ApiTSCallback callback, boolean isColleague) {
        this.progressBar = progressBar;
        this.context = context;
        this.callback = callback;
        this.isColleague = isColleague;
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        String responseBody = response.body().string();
        Log.d("StopJobApi", "API Response: " + responseBody);

        if (response.isSuccessful()) {
            try {
                StopJobApi.StopJobTimeSheetResponse stopJobResponse = new Gson().fromJson(responseBody, StopJobApi.StopJobTimeSheetResponse.class);
                if (stopJobResponse != null && stopJobResponse.success) {
                    progressBar.post(() -> progressBar.setVisibility(View.GONE));
                    Log.w("StopJobApi", "Success API Response: " + responseBody);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (isColleague) {
                            LogoutManager.logoutUser(context);
                        }
                    }, 3000);
                    callback.onSuccess(stopJobResponse.data.message);
                } else {
                    callback.onFailure("Failed to stop the job.");
                }
            } catch (Exception e) {
                callback.onFailure("Error parsing response: " + e.getMessage());
            }
        } else {
            callback.onFailure("API Error: " + response.code() + " - " + response.message());
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        progressBar.post(() -> progressBar.setVisibility(View.GONE));
        Log.e("StopJobApi", "API call failed", e);
        callback.onFailure("API call failed: " + e.getMessage());
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(progressBar.getContext(), "API call failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
