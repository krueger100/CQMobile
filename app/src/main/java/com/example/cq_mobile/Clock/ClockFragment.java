package com.example.cq_mobile.Clock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.cq_mobile.Clock.ClockFolder.ClockInAPIFolder.ClockINApiManager;
import com.example.cq_mobile.Clock.ClockFolder.ClockView;
import com.example.cq_mobile.Clock.ClockFolder.DigitalClockManager;
import com.example.cq_mobile.FirebaseUserData.NotificationTokenManager;
import com.example.cq_mobile.HelperManagers.Animation.ClickAnimationManager;
import com.example.cq_mobile.HelperManagers.DateAndTimeManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LoginFolder.AuthManager;
import com.example.cq_mobile.LoginFolder.EncryptionUtil;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.API_todo.Todo;
import com.example.cq_mobile.ui.home.HomeFolder.API_todo.TodoResponse;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.TaskMainFolder.Taskmain;
import com.example.cq_mobile.ui.home.HomeFolder.homeFrgmentFolder.JobApiManager;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClockFragment extends Fragment {

    ClockView clockView;
    DigitalClockManager digitalClockManager;
    TextView checkInButton;
    String email;
    String password;
    static final String TAG = "ClockFragment";
    ProgressBar progressBar;
    String avatar;
    String token;
    SharedPrefManager sharedPrefManager;
    String firstName;
    String lastName;
    AlertDialog sessionExpiredDialog;
    String accessToken;
    int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clock, container, false);
        checkInButton = view.findViewById(R.id.check_in);
        checkInButton.setEnabled(false);
        checkInButton.setAlpha(0.5f);

        sharedPrefManager = new SharedPrefManager(requireContext());
        progressBar = view.findViewById(R.id.progressBar);
        clockView = view.findViewById(R.id.analogClock);
        TextView digitalClock = view.findViewById(R.id.digitalClock);
        digitalClockManager = new DigitalClockManager(digitalClock);
        digitalClockManager.startClock();
        checkInButton.setEnabled(true);
        checkInButton.setAlpha(1.0f);
        progressBar.setVisibility(View.GONE);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                return;
            }
            token = task.getResult();
            sharedPrefManager.saveNewNotificationToken(token);
            Log.d(TAG, "FCM Token: " + token + ": saved -->");
        });


        AuthManager authManager = AuthManager.getInstance(getContext());
        if (authManager.isLoggedIn()) {
            // Get the encrypted token and decrypt it
            String encryptedAccessToken = authManager.getAccessToken();

            if (encryptedAccessToken != null) {
                try {
                    accessToken = EncryptionUtil.decrypt(encryptedAccessToken);
                    Log.d(TAG, "Decrypted access token: " + accessToken);

                    userId = authManager.getUserId();
                    firstName = authManager.getFirstName();
                    lastName = authManager.getLastName();
                    email = authManager.getEmail();
                    avatar = authManager.getAvatar();
                    

                    if (authManager.isTokenExpired()) {
                        Log.d(TAG, "Token expired. User needs to log in again.");
                    } else {
                        Log.d(TAG, "User is logged in. Access token: " + accessToken);
                        checkInButton.setEnabled(true);
                        checkInButton.setAlpha(1f);


                        JobApiManager.fetchJobData(getContext(), 1, 20, JobApiManager.JobStatus.TODO,
                                json -> new Gson().fromJson(json, TodoResponse.class).getData(),
                                new JobApiManager.ApiResponseCallback<Todo>() {

                                    @Override
                                    public void onDataFetched(List<Todo> data) {
                                        Log.d("TodoData", "Fetched " + data.size() + " items.");

                                        // Running the UI updates on the main thread
                                        requireActivity().runOnUiThread(() -> {
                                            checkInButton.setEnabled(true);
                                            checkInButton.setAlpha(1.0f);

                                            // List to hold all job details
                                            List<JobDetails> jobDetailsList = new ArrayList<>();

                                            for (Todo todo : data) {
                                                Log.d("TodoData", "-----------------------------");
                                                logTodoDetails(todo);

                                                // Creating JobDetails object with additional data
                                                JobDetails jobDetails = createJobDetails(todo);
                                                jobDetailsList.add(jobDetails);

                                                // Perform check-in (if needed)
                                                checkIN(userId, todo.getId(), todo.getJob_id());

                                                // Save the coordinates (if available)
                                                saveCoordinatesIfNeeded(todo);

                                            }

                                            // Save job details to SharedPreferences
                                            SaveJobDataManager saveJobDataManager = new SaveJobDataManager(requireContext());
                                            saveJobDataManager.saveJobData(jobDetailsList);
                                        });
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.w("Login", "Error: -> " + userId + " <-  " + error);
                                        requireActivity().runOnUiThread(() -> {
                                            progressBar.setVisibility(View.GONE);
                                            checkInButton.setEnabled(true);
                                            checkInButton.setAlpha(1.0f);
                                        });
                                    }

                                    // Method to log Todo details
                                    private void logTodoDetails(Todo todo) {
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

                                        sharedPrefManager.saveStartJobID(String.valueOf(todo.getId()));
                                        sharedPrefManager.saveStartJobInnerTask(String.valueOf(todo.getJob_id()));

                                        // Logging client details if available
                                        logClientDetails(todo.getClient_details());

                                        // Logging address details if available
                                        logAddressDetails(todo.getAddress());
                                    }

                                    // Method to log client details
                                    private void logClientDetails(Todo.ClientDetails client) {
                                        if (client != null) {
                                            Log.d("TodoData", "Client Name: " + client.getFirst_name() + " " + client.getLast_name());
                                            Log.d("TodoData", "Company: " + client.getCompany());
                                            Log.d("TodoData", "Email: " + client.getEmail());
                                            Log.d("TodoData", "Mobile: " + client.getMobile());
                                        }
                                    }

                                    // Method to log address details
                                    private void logAddressDetails(Todo.Address address) {
                                        if (address != null) {
                                            Log.d("TodoData", "Address: " + address.getAddress() + ", " + address.getCity());
                                            Log.d("TodoData", "Postal Code: " + address.getPostal_code());
                                        }
                                    }

                                    private JobDetails createJobDetails(Todo todo) {
                                        String currentDate = DateAndTimeManager.getCurrentDateAndTime(getContext());
                                        sharedPrefManager.saveClockinDate(currentDate);
                                        sharedPrefManager.saveClockinStartDate(todo.getStart_date());
                                        sharedPrefManager.saveClockinStopDate(todo.getEnd_date());
                                        return new JobDetails(
                                                todo.getId(),
                                                todo.getJob_id(),
                                                todo.getJob_title(),
                                                todo.getName(),
                                                todo.getDescription(),
                                                todo.getStatus(),
                                                todo.getCategory(),
                                                todo.getStart_date(),
                                                todo.getEnd_date(),
                                                todo.isChecked(),
                                                todo.getClient_details(),
                                                todo.getAddress(),
                                                todo.getCoordinates()
                                        );
                                    }


                                    // Method to save coordinates (if available)
                                    private void saveCoordinatesIfNeeded(Todo todo) {
                                        Todo.Coordinates coords = todo.getCoordinates();
                                        if (coords != null) {
                                            Log.d("TodoData", "Latitude: " + coords.getLatitude());
                                            Log.d("TodoData", "Longitude: " + coords.getLongitude());

                                            Taskmain.Coordinates taskCoords = convertToTaskmainCoordinates(coords);
                                            SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(requireContext());
                                            sharedPrefManager.saveCoordinatesList(Collections.singletonList(taskCoords));

                                        }
                                    }
                                });


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error decrypting access token: " + e.getMessage());
                }
            }
        } else {
            Log.d(TAG, "User is not logged in.");
        }

        return view;

    }

    private Taskmain.Coordinates convertToTaskmainCoordinates(Todo.Coordinates todoCoordinates) {
        String latitudeStr = todoCoordinates.getLatitude();
        String longitudeStr = todoCoordinates.getLongitude();
        double lat = (latitudeStr != null && !latitudeStr.isEmpty()) ? Double.parseDouble(latitudeStr) : 0.0;
        double lon = (longitudeStr != null && !longitudeStr.isEmpty()) ? Double.parseDouble(longitudeStr) : 0.0;

        return new Taskmain.Coordinates(lat, lon);
    }


    private void checkIN(int userId, int id, int jobId) {
        progressBar.setVisibility(View.GONE);
        if (checkInButton != null) {
            checkInButton.setOnClickListener(v -> {
                if (this.accessToken != null && !this.accessToken.isEmpty()) {

                    checkInButton.setEnabled(false);
                    checkInButton.setAlpha(0.5f);
                    if (v != null) {
                        ClickAnimationManager.applyClickAnimation(v);
                    }

                    ClockINApiManager.clockIN(id, jobId, this.userId, requireContext(), new ClockINApiManager.ApiCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Clock IN Successful");
                            progressBar.setVisibility(View.GONE);
                            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE);
                            if (sharedPreferences != null) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("ClockInSuccess", true);
                                editor.apply();
                                String startDateTime = DateAndTimeManager.getCurrentTime(requireContext());
                                Log.w(TAG, "Start_date_time " + startDateTime);
                            }

                            Log.w(TAG, "   <<<<<<<<< CHECK IN PRESSED >>>>>>>> ");
                            Log.w(TAG, "   <<<<<<<<< ACCESS TOKEN >>>>>>>> "+ "\n "+" ->  "+ ClockFragment.this.accessToken);
                            Log.w(TAG, "   <<<<<<<< NOTIFICATION TOKEN >>>>>>>>> "+ "\n "+" ->  "+ ClockFragment.this.token);
                            Log.w(TAG, "   <<<<<<<<< USER ID >>>>>>>> "+ "\n "+" ->  "+ ClockFragment.this.userId);
                            Log.w(TAG, "   <<<<<<<<< AVATAR >>>>>>>>>> "+ "\n "+" ->  "+ ClockFragment.this.avatar);
                            Log.w(TAG, "   <<<<<<<<< FIRST NAME >>>>>>>>>> "+ "\n "+" ->  "+ ClockFragment.this.firstName);
                            Log.w(TAG, "   <<<<<<<<< LAST NAME >>>>>>>>>> "+ "\n "+" ->  "+ ClockFragment.this.lastName);
                            Log.w(TAG, "   <<<<<<<<< EMAIL >>>>>>>>>> "+ "\n "+" ->  "+ ClockFragment.this.email);

                            // Save user data to Firebase
                            NotificationTokenManager notificationTokenManager = new NotificationTokenManager(ClockFragment.this.userId);
                            notificationTokenManager.saveUserDataToFirebase(ClockFragment.this.accessToken,
                                    String.valueOf(ClockFragment.this.userId),
                                    ClockFragment.this.avatar,
                                    ClockFragment.this.firstName,
                                    ClockFragment.this.lastName,
                                    token);



                            // Hide ClockFragment or replace it
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).hideClockFragment();
                            }
                        }


                        @Override
                        public void onFailure(String error) {
                            Log.d(TAG, "Clock in Error: " + (error != null ? error : "Unknown error"));
                            checkInButton.setEnabled(true);
                            checkInButton.setAlpha(1.0f);
                        }
                    });
                } else {
                    Log.e(TAG, "Access token is missing.");
                    Toast.makeText(requireContext(), "Access token is required to check in.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            Log.e(TAG, "Check-In button is null.");
            progressBar.setVisibility(View.GONE);
        }
    }


}
