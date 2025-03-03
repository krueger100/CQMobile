package com.example.cq_mobile.LogoutFolder;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;

public class LogoutNotificationManager {

    private static final String TAG = "LogoutNotificationManager";
    private static final String USERS_NODE = "users";

    private DatabaseReference usersRef;

    public LogoutNotificationManager() {
        // Initialize reference to "users" node
        usersRef = FirebaseDatabase.getInstance().getReference(USERS_NODE);
    }

    /**
     * Deletes the notificationToken for the specified userId.
     *
     * @param userId   The ID of the user whose notificationToken should be deleted.
     * @param callback A callback to handle success or failure.
     */
    public void deleteNotificationToken(String userId, LogoutCallback callback) {
        // Path to the user's notificationToken
        DatabaseReference notificationTokenRef = usersRef.child(userId).child("notificationToken");
        // Set the value to null to delete it
        notificationTokenRef.setValue(null, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error != null) {
                    Log.e(TAG, "Failed to delete notificationToken for userId: " + userId, error.toException());
                    if (callback != null) {
                        callback.onFailure(error.getMessage());
                    }
                } else {
                    Log.d(TAG, "Successfully deleted notificationToken for userId: " + userId);
                    if (callback != null) {
                        callback.onSuccess();
                    }
                }
            }
        });
    }

    /**
     * Callback interface for logout actions.
     */
    public interface LogoutCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}
