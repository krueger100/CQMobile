package com.example.cq_mobile.ui.home.CheckInFolder;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.widget.Toast;

public class CheckInManager {
    private final DatabaseReference databaseReference;
    private final Context context;
    private static final String TAG = "CheckInManager";

    public CheckInManager(Context context) {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("CheckIn_Data");
        this.context = context;
    }

    public void saveCheckIn() {
        // Get the current time and date
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        String currentTime = timeFormat.format(new Date());
        String currentDate = dateFormat.format(new Date());

        // Show AlertDialog to confirm check-in details
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Check In Details");
        builder.setMessage("Time: " + currentTime + "\nDate: " + currentDate);

        // Add "Check In" button
        builder.setPositiveButton("Check In", (dialog, which) -> {
            // Prepare data to save
            Map<String, String> checkInData = new HashMap<>();
            checkInData.put("time", currentTime);
            checkInData.put("date", currentDate);

            // Save to Firebase
            databaseReference.push().setValue(checkInData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Check-in saved successfully");
                        Toast.makeText(context, "Check-in successful!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to save check-in", e);
                        Toast.makeText(context, "Failed to save check-in.", Toast.LENGTH_SHORT).show();
                    });

            Log.d(TAG, "TIME AND DATE: " + currentTime + "  " + currentDate);
        });

        // Add "Back" button
        builder.setNegativeButton("Back", (dialog, which) -> dialog.dismiss());

        // Show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
