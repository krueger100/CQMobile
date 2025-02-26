package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.RetrieveDataFromAPIMangers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.cq_mobile.HelperManagers.CategoryColorManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.NewBuildApiManager;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SpinnerFolder.SetupMainTaskSpinnerAdapter;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.TaskMainFolder.Taskmain;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class SetupMainTaskManager {

    private Context context;
    private GoogleMap googleMap;
    private TextView taskTitleView;
    private TextView taskDescriptionView;
    private TextView taskLocationView;
    private TextView taskNumberView;
    private Spinner spinnerTask;
    TextView category_todo;
    private OnCoordinatesReceivedListener coordinatesReceivedListener; // Callback listener
    ImageView statusImageView;
    String taskStatus;

    public SetupMainTaskManager(Context context, GoogleMap googleMap, TextView taskTitleView, TextView taskDescriptionView,
                                TextView taskLocationView, TextView taskNumberView, Spinner spinnerTask,
                                OnCoordinatesReceivedListener listener, TextView category_todo, ImageView statusImageView) {
        this.context = context;
        this.googleMap = googleMap;
        this.taskTitleView = taskTitleView;
        this.taskDescriptionView = taskDescriptionView;
        this.taskLocationView = taskLocationView;
        this.taskNumberView = taskNumberView;
        this.spinnerTask = spinnerTask;
        this.coordinatesReceivedListener = listener;
        this.category_todo = category_todo;
        this.statusImageView = statusImageView;



    }

    public void setupMainTask(String jobId) {
        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        String accessToken = sharedPrefManager.getAccessToken();
        String userId = sharedPrefManager.getUserId();
        String firstName = sharedPrefManager.getFirstName();
        String lastName = sharedPrefManager.getLastName();
        String email = sharedPrefManager.getEmail();

        Log.d("SetupMainTaskManagerSharedPreff", "Retrieved User Data: ");
        Log.d("SetupMainTaskManagerSharedPreff", "Access Token: " + accessToken);
        Log.d("SetupMainTaskManagerSharedPreff", "User ID: " + userId);
        Log.d("SetupMainTaskManagerSharedPreff", "First Name: " + firstName);
        Log.d("SetupMainTaskManagerSharedPreff", "Last Name: " + lastName);
        Log.d("SetupMainTaskManagerSharedPreff", "Email: " + email);

        NewBuildApiManager.fetchNewBuiltApiData(jobId,accessToken, new NewBuildApiManager.ApiResponseCallback<Taskmain>() {
            @Override
            public void onDataFetched(List<Taskmain> data) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    StringBuilder taskTitle = new StringBuilder();
                    StringBuilder taskDescription = new StringBuilder();
                    StringBuilder taskLocation = new StringBuilder();
                    StringBuilder taskNumber = new StringBuilder();
                    StringBuilder category_maintask = new StringBuilder();
                    StringBuilder status_maintask = new StringBuilder();


                    for (Taskmain taskmain : data) {
                        // Task title and description
                        taskTitle.append(taskmain.getName());
                        taskDescription.append(taskmain.getDescription());
                        category_maintask.append(taskmain.getCategory());
                        status_maintask.append(taskmain.getStatus());

                        String clientInfo = taskmain.getClient_details() != null
                                ? taskmain.getClient_details().getPhone()
                                : "No client info";
                        taskNumber.append(clientInfo);

                        String address = taskmain.getAddress() != null
                                ? taskmain.getAddress().getAddress()
                                : "No address";
                        taskLocation.append(address);

                        if (googleMap != null) {
                            Taskmain.Coordinates coordinates = taskmain.getCoordinates();
                            if (coordinates != null) {
                                try {
                                    double latitude = Double.parseDouble(coordinates.getLatitude());
                                    double longitude = Double.parseDouble(coordinates.getLongitude());
                                    LatLng taskLatLng = new LatLng(latitude, longitude);

                                    Log.d("SetupMainTaskManager", "Parsed coordinates: Latitude = " + latitude + ", Longitude = " + longitude);

                                    // Pass coordinates to listener (NewBuild)
                                    if (coordinatesReceivedListener != null) {
                                        coordinatesReceivedListener.onCoordinatesReceived(latitude, longitude);
                                    } else {
                                        Log.e("SetupMainTaskManager", "coordinatesReceivedListener is null");
                                    }


                                    // Add marker to the map
                                    googleMap.addMarker(new MarkerOptions()
                                            .position(taskLatLng)
                                            .title(taskmain.getName())
                                            .snippet(taskmain.getDescription()));

                                    // Optionally, focus camera on the first task
                                    if (data.indexOf(taskmain) == 0) {
                                        //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(taskLatLng, 15));
                                    }
                                } catch (NumberFormatException e) {
                                    Log.e("SetupMainTaskManager", "Invalid coordinates: " + e.getMessage());

                                }
                            }
                        }
                    }


                   String status_main = status_maintask.toString().trim();
                    Log.d("Stats", "SetupMainTaskManager -> "+ status_main);

                    List<String> options = new ArrayList<>();
                    options.add("Todo");
                    options.add("Skipped");
                    options.add("Done");



                    SetupMainTaskSpinnerAdapter adapter = new SetupMainTaskSpinnerAdapter(
                            context,
                            R.layout.task_spinner_item,
                            options,jobId,statusImageView,status_main,accessToken);
                    spinnerTask.setAdapter(adapter);

                    // Handle spinner item selection
                    spinnerTask.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position >= 0 && position < data.size()) {
                                Taskmain selectedTask = data.get(position);

                                taskTitleView.setText(selectedTask.getName());
                                taskDescriptionView.setText(selectedTask.getDescription());

                                String categories = selectedTask.getCategory() != null && !selectedTask.getCategory().trim().isEmpty()
                                        ? selectedTask.getCategory().trim()
                                        : "No Category";
                                String categoriesColors = String.valueOf(selectedTask.getCategory_color()).trim();

                                Drawable categoryBackground = CategoryColorManager.getCategoryBackground(context, categoriesColors);
                                category_todo.setText(categories);

                                if (categoriesColors != null && !categoriesColors.isEmpty()) {
                                    try {
                                        int categoryColor = Color.parseColor(categoriesColors);
                                        category_todo.setTextColor(categoryColor);
                                        category_todo.setBackground(categoryBackground);

                                    } catch (IllegalArgumentException e) {
                                        Log.e("category_todo", "Invalid category color format: " + categoriesColors, e);
                                        category_todo.setTextColor(ContextCompat.getColor(context, R.color.textBtnGrey));
                                    }
                                } else {
                                    category_todo.setTextColor(ContextCompat.getColor(context, R.color.textBtnGrey));
                                }

                                String clientInfo = selectedTask.getClient_details() != null
                                        ? selectedTask.getClient_details().getPhone()
                                        : "No client info";
                                taskNumberView.setText(clientInfo);

                                String address = selectedTask.getAddress() != null
                                        ? selectedTask.getAddress().getAddress()
                                        : "No address";
                                taskLocationView.setText(address);



                            } else {
                                Log.e("SetupMainTaskManager", "Selected spinner position is out of bounds");
                            }


                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Optional: handle case when no item is selected
                        }
                    });

                    // Update the TextViews with the tasks
                    taskTitleView.setText(taskTitle.toString());
                    taskDescriptionView.setText(taskDescription.toString());
                    taskLocationView.setText(taskLocation.toString());
                    taskNumberView.setText(taskNumber.toString());
                });
            }

            @Override
            public void onError(String error) {
                ((Activity) context).runOnUiThread(() -> {
                    Log.e("SetupMainTaskManager","fetching data: " +error);
                    showErrorDialog(context,error);
                });
            }
        });
    }

    private void showErrorDialog(Context context, String error) {
        new AlertDialog.Builder(context)
                .setTitle("API Response:")
                .setMessage(error)
                .setPositiveButton("Ok", (dialog, which) -> {
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(false)
                .show();
    }

    public interface OnCoordinatesReceivedListener {
        void onCoordinatesReceived(double latitude, double longitude);
    }


}
