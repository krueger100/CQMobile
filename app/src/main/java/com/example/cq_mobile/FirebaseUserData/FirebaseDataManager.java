package com.example.cq_mobile.FirebaseUserData;

import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;


public class FirebaseDataManager {
    private static final String TAG = "FirebaseDataManager";
    private static final String USERS_NODE = "users";

    private DatabaseReference userRef;
    private ValueEventListener valueEventListener;

    public FirebaseDataManager(String userId) {
        userRef = FirebaseDatabase.getInstance().getReference(USERS_NODE).child(userId);
    }

    public void saveUserData(String accessToken, String userId, String avatar, String firstName, String lastName, String notificationToken) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("accessToken", accessToken);
        userData.put("userId", userId);
        userData.put("avatar", avatar);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("notificationToken", notificationToken);

        userRef.setValue(userData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User data successfully saved."))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving user data", e));
    }

    public void retrieveUserData(UserDataCallback callback) {
        valueEventListener = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String accessToken = dataSnapshot.child("accessToken").getValue(String.class);
                    String userId = dataSnapshot.child("userId").getValue(String.class);
                    String avatar = dataSnapshot.child("avatar").getValue(String.class);
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);
                    String notificationToken = dataSnapshot.child("notificationToken").getValue(String.class);

                    Log.d(TAG, "Retrieved User Data: ");
                    Log.d(TAG, "Access Token: " + accessToken);
                    Log.d(TAG, "User ID: " + userId);
                    Log.d(TAG, "User AVATAR: " + avatar);
                    Log.d(TAG, "First Name: " + firstName);
                    Log.d(TAG, "Last Name: " + lastName);
                    Log.d(TAG, "Notification Token: " + notificationToken);

                    // Update user data every time data is retrieved
                    saveUserData(accessToken, userId, avatar, firstName, lastName, notificationToken);

                    callback.onUserDataRetrieved(accessToken, userId, avatar, firstName, lastName, notificationToken);
                } else {
                    Log.d(TAG, "No user data found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error fetching user data", databaseError.toException());
            }
        });
    }

    public void removeListener() {
        if (valueEventListener != null) {
            userRef.removeEventListener(valueEventListener);
        }
    }

    public interface UserDataCallback {
        void onUserDataRetrieved(String accessToken, String userId, String avatar, String firstName, String lastName, String notificationToken);
    }
}




/*
        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        String accessToken = sharedPrefManager.getAccessToken();
        String userId = sharedPrefManager.getUserId();
        String avatar = sharedPrefManager.getAvatarUrl();
        String firstName = sharedPrefManager.getFirstName();
        String lastName = sharedPrefManager.getLastName();
        String notificationToken = sharedPrefManager.getNotiftoken();

        FirebaseDataManager firebaseDataManager = new FirebaseDataManager(userId);
        firebaseDataManager.saveUserData(accessToken, userId, avatar, firstName, lastName, notificationToken);

        firebaseDataManager.retrieveUserData(new FirebaseDataManager.UserDataCallback() {
            @Override
            public void onUserDataRetrieved(String accessToken, String userId, String avatar, String firstName, String lastName, String notificationToken) {
                // Use the retrieved data
                Log.d("MainActivity", "Access Token: " + accessToken);
                Log.d("MainActivity", "User ID: " + userId);
                Log.d("MainActivity", "Avatar: " + avatar);
                Log.d("MainActivity", "First Name: " + firstName);
                Log.d("MainActivity", "Last Name: " + lastName);
                Log.d("MainActivity", "Notification token: " + notificationToken);
            }
        });


 */


/*
String userId = "someUserId"; // Replace with actual user ID
FirebaseRetrieveDataManager firebaseRetrieveDataManager = new FirebaseRetrieveDataManager(userId);

firebaseRetrieveDataManager.retrieveUserData(new FirebaseRetrieveDataManager.UserDataCallback() {
    @Override
    public void onUserDataRetrieved(String accessToken, String userId, String avatar, String firstName, String lastName, String notificationToken) {
        if (accessToken != null) {
            Log.d("MainActivity", "Access Token: " + accessToken);
            Log.d("MainActivity", "User ID: " + userId);
            Log.d("MainActivity", "Avatar: " + avatar);
            Log.d("MainActivity", "First Name: " + firstName);
            Log.d("MainActivity", "Last Name: " + lastName);
            Log.d("MainActivity", "Notification Token: " + notificationToken);
        } else {
            Log.d("MainActivity", "No user data found.");
        }
    }
});

firebase database data structure
users -|
           | -2  <- user Id
           |   |-accessToken:"8205|2Q08dgeKT2dihh5AKziJ0j4k6lMEeSpTj99klKjr"
           |   |-avatar:"uploads/contacts/avatars/colleague_avatar_21702984331."
           |   |-firstName:"Marc"
           |   |-lastName:"Lucien"
           |   |-notificationToken:"cJ8_yGiBThWD_elJuKGugN:APA91bFBQHDPL
oq2mORQqQ6e7AuPg5Yhe4rLag7r82ImIZ1rKFHBWQ0_
cTgVDQSqQd1PZGItWzkZvoiYOwLIYBxIfhSQPAF1-5_GXQ4rxymxVTRLMxQD1as"
           |   |-userId:"2"
           |-3
           |   |-accessToken:"11191|ltBl6uvn0LOE8B449JBlihaOcKVulIY6xRcparBB"
           |   |-avatar:"2/uploads/contacts/avatars/colleague_avatar_31734941421."
           |   |-firstName:"Richard"
           |   |-lastName:"Wetherell"
           |   |-notificationToken:"dh2HSw3oRpC1S4n-4OoNif:APA91bH2reoSsMuZVtApoh8fOxfXqiLrzV-Qls_u85qEqRgJGREOi-_EJX7HgxuXHGWz7W8SSzYT_cxOyy1VEdPq7NbBRZ26qnCQxrXQxej5-Fq6oWadQuU"
           |   |-userId:"3"
           |-331


 */