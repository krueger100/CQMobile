package com.example.cq_mobile.ui.home.HomeFolder.homeFrgmentFolder;

import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.LoginFolder.AuthManager;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class JobApiManager {

    public interface ApiResponseCallback<T> {
        void onDataFetched(List<T> data);
        void onError(String error);
    }

    public interface JobResponseParser<T> {
        List<T> parse(String jsonResponse) throws Exception;
    }

    public enum JobStatus {
        TODO("todo"),
        DONE("done"),
        SKIPPED("skipped");

        private final String value;

        JobStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static <T> void fetchJobData(
            Context context,
            int page,
            int pageSize,
            JobStatus status,
            JobResponseParser<T> parser,
            ApiResponseCallback<T> callback
    ) {
        if (context == null) {
            callback.onError("Context is null.");
            return;
        }

        AuthManager authManager = AuthManager.getInstance(context);
        if (!authManager.isLoggedIn() || authManager.isTokenExpired()) {
            callback.onError("User not logged in or token expired.");
            return;
        }

        String accessToken = authManager.getToken();
        String baseUrl = "https://cqbms.app";
        String endpoint = "/api/m/jobs/schedules/today";
        String url = String.format("%s%s?page=%d&per_page=%d&status=%s", baseUrl, endpoint, page, pageSize, status.getValue());
        String apiKey = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("x-api-key", apiKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String message;
                if (e instanceof SocketTimeoutException) {
                    message = "Request timed out. Please check your internet connection.";
                } else if (e instanceof UnknownHostException) {
                    message = "No internet connection. Please check your network.";
                } else {
                    message = "Network error: " + e.getMessage();
                }
                Log.e("API Failure", message, e);
                callback.onError(message);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (!response.isSuccessful()) {
                        callback.onError("Server error: " + response.code() + " - " + response.message());
                        return;
                    }

                    ResponseBody responseBody = response.body();
                    if (responseBody == null) {
                        callback.onError("Empty response from server.");
                        return;
                    }

                    String jsonResponse = responseBody.string();
                    Log.d("JobApiManager-> API Response", jsonResponse);

                    List<T> dataList = parser.parse(jsonResponse);

                    if (dataList != null && !dataList.isEmpty()) {
                        callback.onDataFetched(dataList);
                    } else {
                        callback.onError("No jobs found.");
                    }

                } catch (Exception e) {
                    Log.e("Parsing Error", "Failed: " + e.getMessage(), e);
                    callback.onError("Failed to parse data: " + e.getMessage());
                } finally {
                    response.close();
                }
            }
        });
    }
}


/*


    private void UseDetails_JobID(String accessToken, int userId, String avatar, String firstName, String lastName) {
        JobApiManager.fetchJobData(accessToken, 1, 20, JobApiManager.JobStatus.TODO,
                json -> new Gson().fromJson(json, TodoResponse.class).getData(),
                new JobApiManager.ApiResponseCallback<Todo>() {
                    @Override
                    public void onDataFetched(List<Todo> data) {
                        Log.d("TodoData", "Fetched " + data.size() + " items.");

                        for (Todo todo : data) {
                            Log.d("TodoData", "-----------------------------");
                            Log.d("TodoData", "ID: " + todo.getId());
                            Log.d("TodoData", "Job ID: " + todo.getJob_id());
                            Log.d("TodoData", "Job Title: " + todo.getJob_title());
                            Log.d("TodoData", "Task Name: " + todo.getName());
                            Log.d("TodoData", "Description: " + todo.getDescription());
                            Log.d("TodoData", "Status: " + todo.getStatus());
                            Log.d("TodoData", "Category: " + todo.getCategory());
                            Log.d("TodoData", "Start Date: " + todo.getStart_date());
                            Log.d("TodoData", "End Date: " + todo.getEnd_date());
                            Log.d("TodoData", "Checked: " + todo.isChecked());

                            // Client Info
                            Todo.ClientDetails client = todo.getClient_details();
                            if (client != null) {
                                Log.d("TodoData", "Client Name: " + client.getFirst_name() + " " + client.getLast_name());
                                Log.d("TodoData", "Company: " + client.getCompany());
                                Log.d("TodoData", "Email: " + client.getEmail());
                                Log.d("TodoData", "Mobile: " + client.getMobile());
                            }

                            // Address
                            Todo.Address address = todo.getAddress();
                            if (address != null) {
                                Log.d("TodoData", "Address: " + address.getAddress() + ", " + address.getCity());
                                Log.d("TodoData", "Postal Code: " + address.getPostal_code());
                            }

                            // Coordinates
                            Todo.Coordinates coords = todo.getCoordinates();
                            if (coords != null) {
                                Log.d("TodoData", "Latitude: " + coords.getLatitude());
                                Log.d("TodoData", "Longitude: " + coords.getLongitude());
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.w("Login", "Error: -> " + userId +" <-  "+  error);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }


 */