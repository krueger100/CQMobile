package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.RetrieveDataFromAPIMangers;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.NewBuildApiManager;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.SpinnerFolder.CustomSpinnerAdapter;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.TaskMainFolder.Taskmain;
import com.google.android.gms.maps.CameraUpdateFactory;
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
    private OnCoordinatesReceivedListener coordinatesReceivedListener; // Callback listener

    // Constructor with added listener parameter
    public SetupMainTaskManager(Context context, GoogleMap googleMap, TextView taskTitleView, TextView taskDescriptionView,
                                TextView taskLocationView, TextView taskNumberView, Spinner spinnerTask,
                                OnCoordinatesReceivedListener listener) {
        this.context = context;
        this.googleMap = googleMap;
        this.taskTitleView = taskTitleView;
        this.taskDescriptionView = taskDescriptionView;
        this.taskLocationView = taskLocationView;
        this.taskNumberView = taskNumberView;
        this.spinnerTask = spinnerTask;
        this.coordinatesReceivedListener = listener; // Set the listener
    }

    public void setupMainTask(String jobId) {
        NewBuildApiManager.fetchNewBuiltApiData(jobId, new NewBuildApiManager.ApiResponseCallback<Taskmain>() {
            @Override
            public void onDataFetched(List<Taskmain> data) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    StringBuilder taskTitle = new StringBuilder();
                    StringBuilder taskDescription = new StringBuilder();
                    StringBuilder taskLocation = new StringBuilder();
                    StringBuilder taskNumber = new StringBuilder();

                    for (Taskmain taskmain : data) {
                        // Task title and description
                        taskTitle.append(taskmain.getName());
                        taskDescription.append(taskmain.getDescription());

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

                                    // Pass coordinates to listener (NewBuild)
                                    if (coordinatesReceivedListener != null) {
                                        coordinatesReceivedListener.onCoordinatesReceived(latitude, longitude);
                                    }

                                    // Add marker to the map
                                    googleMap.addMarker(new MarkerOptions()
                                            .position(taskLatLng)
                                            .title(taskmain.getName())
                                            .snippet(taskmain.getDescription()));

                                    // Optionally, focus camera on the first task
                                    if (data.indexOf(taskmain) == 0) {
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(taskLatLng, 15));
                                    }
                                } catch (NumberFormatException e) {
                                    Log.e("SetupMainTaskManager", "Invalid coordinates: " + e.getMessage());
                                }
                            }
                        }
                    }

                    List<String> options = new ArrayList<>();
                    options.add("In Progress");
                    options.add("Pending");
                    options.add("Under Inspection");
                    options.add("Done");


// Set up the adapter
                    int[] dropDownColors = new int[]{
                            ContextCompat.getColor(context, R.color.cq_secondary_color),
                            ContextCompat.getColor(context, R.color.textBtnRed),
                            ContextCompat.getColor(context, R.color.color_inspection),
                            ContextCompat.getColor(context, R.color.color_done)
                    };

                    CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(
                            context,
                            R.layout.task_spinner_item,
                            options,
                            dropDownColors);
                    spinnerTask.setAdapter(adapter);

                    // Handle spinner item selection
                    spinnerTask.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position >= 0 && position < data.size()) { // Ensure position is within bounds
                                Taskmain selectedTask = data.get(position);

                                taskTitleView.setText(selectedTask.getName());
                                taskDescriptionView.setText(selectedTask.getDescription());

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
                    Toast.makeText(context, "Error fetching data: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // Interface to pass coordinates to NewBuild
    public interface OnCoordinatesReceivedListener {
        void onCoordinatesReceived(double latitude, double longitude);
    }
}
