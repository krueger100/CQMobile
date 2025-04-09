package com.example.cq_mobile.FirebaseUserData;

public class NotificationTokenManager {

    private FirebaseDataManager firebaseDataManager;

    public NotificationTokenManager(int userId) {
        // Initialize FirebaseDataManager with userId
        firebaseDataManager = new FirebaseDataManager(userId);
    }

    // Method to save user data to Firebase
    public void saveUserDataToFirebase(String accessToken, String userId, String avatar,
                                       String firstName, String lastName, String notificationToken) {
        // Save the user data using FirebaseDataManager
        firebaseDataManager.saveUserData(accessToken, userId, avatar, firstName, lastName, notificationToken);
    }
}
