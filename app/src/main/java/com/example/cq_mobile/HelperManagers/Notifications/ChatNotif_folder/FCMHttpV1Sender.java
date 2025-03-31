package com.example.cq_mobile.HelperManagers.Notifications.ChatNotif_folder;
import com.google.gson.JsonObject;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
public class FCMHttpV1Sender {

    private static final Logger logger = Logger.getLogger(FCMHttpV1Sender.class.getName());
    private static final String PROJECT_ID = "cqbms-app";
    private static final String FCM_URL = "https://fcm.googleapis.com/v1/projects/" + PROJECT_ID + "/messages:send";
    private static final String SERVICE_ACCOUNT_FILE = "C:/Users/carlo/AndroidStudioProjects/CQ_mobile/app/src/main/resources/service_account.json";

    public static void sendPushNotification(String token, String title, String body) {
        try {
            logger.info("Starting push notification process...");
            String accessToken = getAccessToken();
            logger.info("Access token obtained successfully.");

            URL url = new URL(FCM_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json; UTF-8");
            conn.setDoOutput(true);

            JsonObject message = new JsonObject();
            JsonObject notification = new JsonObject();
            notification.addProperty("title", title);
            notification.addProperty("body", body);

            JsonObject messageObj = new JsonObject();
            messageObj.addProperty("token", token);
            messageObj.add("notification", notification);

            message.add("message", messageObj);

            logger.info("Sending payload: " + message.toString());

            try (OutputStream os = conn.getOutputStream()) {
                os.write(message.toString().getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            logger.info("FCM Response Code: " + responseCode);

            if (responseCode == 200) {
                logger.info("Push notification sent successfully.");
            } else {
                try (InputStream errorStream = conn.getErrorStream()) {
                    if (errorStream != null) {
                        String errorResponse = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                        logger.severe("Failed to send push notification. Response: " + errorResponse);
                    } else {
                        logger.severe("Failed to send push notification. No error details available.");
                    }
                }
            }

            conn.disconnect();

        } catch (Exception e) {
            logger.severe("Error while sending push notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getAccessToken() throws Exception {
        logger.info("Fetching access token...");
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(SERVICE_ACCOUNT_FILE))
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
        credentials.refreshIfExpired();
        String token = credentials.getAccessToken().getTokenValue();
        logger.info("Access token fetched successfully.");
        return token;
    }

    public static void main(String[] args) {
        String token = "dh2HSw3oRpC1S4n-4OoNif:APA91bH2reoSsMuZVtApoh8fOxfXqiLrzV-Qls_u85qEqRgJGREOi-_EJX7HgxuXHGWz7W8SSzYT_cxOyy1VEdPq7NbBRZ26qnCQxrXQxej5-Fq6oWadQuU";
        logger.info("Initiating push notification test...");
        sendPushNotification(token, "Test Title", "Test Message with HTTP v1");
        logger.info("Push notification test completed.");
    }
}


/*

FIREBASE:

public class GetNotificationToken extends FirebaseMessagingService {

    private static final String TAG = "GetNotificationToken";
    private static final String CHANNEL_ID = "chat_channel";

    public static void getToken(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Notification permission not granted!");
                return; // Don't proceed without permission
            }
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);
                    // Send token to server if needed
                });
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM Token: " + token);
        // Send token to your server if necessary
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Message Received: " + remoteMessage.getData());

        String title = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getTitle() : "New Notification";
        String message = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "You have a new message.";

        showNotification(this, title, message);
    }

    // Show Notification with Permission Check
    public static void showNotification(Context context, String title, String message) {
        createNotificationChannel(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Notification permission denied!");
                return; // Don't show notification if permission is denied
            }
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.cq_logo) // Use a valid drawable icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

    // Create Notification Channel (Required for Android 8+)
    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Channel for push notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}


curl -X POST "https://fcm.googleapis.com/fcm/send" \
     -H "Authorization: key=BO6kpn3DTwsfrctIhkxeWFv4cfgAY62GsfsGyilL3ZfesC36wpr-4Qn1ylBeet-Jcm7NEoe65H4pW1vYr1OtLkM" \
     -H "Content-Type: application/json" \
     -d '{
           "to": "dh2HSw3oRpC1S4n-4OoNif:APA91bH2reoSsMuZVtApoh8fOxfXqiLrzV-Qls_u85qEqRgJGREOi-_EJX7HgxuXHGWz7W8SSzYT_cxOyy1VEdPq7NbBRZ26qnCQxrXQxej5-Fq6oWadQuU",
           "notification": {
               "title": "Hello!",
               "body": "This is a test push notification.",
               "sound": "default"
           },
           "data": {
               "customKey": "customValue"
           }
         }'

 */