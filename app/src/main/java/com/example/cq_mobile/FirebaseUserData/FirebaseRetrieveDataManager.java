package com.example.cq_mobile.FirebaseUserData;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FirebaseRetrieveDataManager {
    private static final String TAG = "FirebaseRetrieveDataManager";
    private static final String USERS_NODE = "users";

    private DatabaseReference userRef;

    public FirebaseRetrieveDataManager(String receiverId) {
        // Directly reference the user's node using receiverId
        userRef = FirebaseDatabase.getInstance().getReference(USERS_NODE).child(receiverId);
    }

    public void retrieveUserData(UserDataCallback callback) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userId = dataSnapshot.child("userId").getValue(String.class);
                    String notificationToken = dataSnapshot.child("notificationToken").getValue(String.class);

                    Log.d(TAG, "Retrieved User Data:");
                    Log.d(TAG, "User ID: " + userId);
                    Log.d(TAG, "Notification Token: " + notificationToken);

                    callback.onUserDataRetrieved(userId, notificationToken);
                } else {
                    Log.d(TAG, "No user data found for receiverId: " + userRef.getKey());
                    callback.onUserDataRetrieved(null, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error fetching user data", databaseError.toException());
                callback.onUserDataRetrieved(null, null);
            }
        });
    }

    public interface UserDataCallback {
        void onUserDataRetrieved(String userId, String notificationToken);
    }
}







/*
public class FirebaseRetrieveDataManager {
    private static final String TAG = "FirebaseRetrieveDataManager";
    private static final String USERS_NODE = "users";

    private DatabaseReference userRef;

    public FirebaseRetrieveDataManager(String userId) {
        userRef = FirebaseDatabase.getInstance().getReference(USERS_NODE).child(String.valueOf(userId));
    }

    public void retrieveUserData(UserDataCallback callback) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userId = dataSnapshot.child("userId").getValue(String.class);
                    String notificationToken = dataSnapshot.child("notificationToken").getValue(String.class);

                    Log.d(TAG, "Retrieved User Data:");
                    Log.d(TAG, "User ID: " + userId);
                    Log.d(TAG, "Notification Token: " + notificationToken);

                    callback.onUserDataRetrieved(userId, notificationToken);
                } else {
                    Log.d(TAG, "No user data found");
                    callback.onUserDataRetrieved( null,null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error fetching user data", databaseError.toException());
                callback.onUserDataRetrieved(null, null);
            }
        });
    }

    public interface UserDataCallback {
        void onUserDataRetrieved( String userId , String notificationToken);
    }
}


 */


/*

        FirebaseRetrieveDataManager firebaseRetrieveDataManager = new FirebaseRetrieveDataManager(331);

        firebaseRetrieveDataManager.retrieveUserData(new FirebaseRetrieveDataManager.UserDataCallback() {
            @Override
            public void onUserDataRetrieved(String userId, String notificationToken) {
                if (accessToken != null) {
                    Log.w("InnerChats", "Notification ID: " + userId);
                    Log.w("InnerChats", "Notification Token: " + notificationToken);



                } else {
                    Log.d("MainActivity", "No user data found.");
                }
            }

        });
 */