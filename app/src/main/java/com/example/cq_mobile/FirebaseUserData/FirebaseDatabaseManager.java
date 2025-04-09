package com.example.cq_mobile.FirebaseUserData;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDatabaseManager {

    private static final String USERS_NODE = "users";
    private final DatabaseReference databaseReference;

    public FirebaseDatabaseManager() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference(USERS_NODE);
    }

    // Retrieve user data from Firebase safely
    public void getUserData(String userId, UserDataCallback callback) {
        if (userId == null || userId.trim().isEmpty()) {
            Log.e("FirebaseDatabaseManager", "User ID is null or empty. Cannot retrieve data.");
            callback.onFailure("Invalid user ID");
            return;
        }

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String accessToken = snapshot.child("accessToken").getValue(String.class);
                    String avatar = snapshot.child("avatar").getValue(String.class);
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);

                    // Ensure no field is null or empty
                    accessToken = (accessToken != null && !accessToken.trim().isEmpty()) ? accessToken : "Unknown";
                    avatar = (avatar != null && !avatar.trim().isEmpty() && !avatar.equalsIgnoreCase("N/A")) ? avatar : "default_avatar.png";
                    firstName = (firstName != null && !firstName.trim().isEmpty()) ? firstName : "Unknown";
                    lastName = (lastName != null && !lastName.trim().isEmpty()) ? lastName : "Unknown";

                    User user = new User(userId, accessToken, avatar, firstName, lastName);
                    callback.onSuccess(user);
                } else {
                    Log.w("FirebaseDatabaseManager", "User data not found for userId: " + userId);
                    callback.onFailure("User not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseDatabaseManager", "Database error: " + error.getMessage());
                callback.onFailure(error.getMessage());
            }
        });
    }

    // Callback interface for fetching user data
    public interface UserDataCallback {
        void onSuccess(User user);
        void onFailure(String error);
    }

    // User model class
    public static class User {
        public String userId;
        public String accessToken;
        public String avatar;
        public String firstName;
        public String lastName;

        public User(String userId, String accessToken, String avatar, String firstName, String lastName) {
            this.userId = userId;
            this.accessToken = accessToken;
            this.avatar = avatar;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }
}

/*
    FirebaseDatabaseManager firebaseDatabaseManager = new FirebaseDatabaseManager();
                                firebaseDatabaseManager.getUserData(userId, new FirebaseDatabaseManager.UserDataCallback() {
                                    @Override
                                    public void onSuccess(FirebaseDatabaseManager.User user) {
                                        Log.d("MainActivity", "User Retrieved: " + user.firstName + " " + user.lastName);

                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        Log.e("MainActivity", "Failed to retrieve user: " + error);
                                    }
                                });
 */


/*

FirebaseDatabaseManager databaseManager = new FirebaseDatabaseManager();

String userId = "12345"; // Replace with an actual user ID from Firebase
databaseManager.getUserData(userId, new FirebaseDatabaseManager.UserDataCallback() {
    @Override
    public void onSuccess(FirebaseDatabaseManager.User user) {
        // Handle successful data retrieval
        Log.d("UserData", "User ID: " + user.userId);
        Log.d("UserData", "Access Token: " + user.accessToken);
        Log.d("UserData", "Avatar: " + user.avatar);
        Log.d("UserData", "First Name: " + user.firstName);
        Log.d("UserData", "Last Name: " + user.lastName);

        // You can now use the user object to update the UI
    }

    @Override
    public void onFailure(String error) {
        // Handle error
        Log.e("UserData", "Error retrieving user data: " + error);
    }
});

 */