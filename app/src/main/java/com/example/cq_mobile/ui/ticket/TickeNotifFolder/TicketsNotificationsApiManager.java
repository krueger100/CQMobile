package com.example.cq_mobile.ui.ticket.TickeNotifFolder;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TicketsNotificationsApiManager {

    private static final String TAG = "TicketsNotificationsApiManager";
    private static final String BASE_URL = "https://aws.customquoter.co.uk";
    private static Retrofit retrofit = null;

    public interface ApiCallback {
        void onSuccess(TicketNotifAPIResponse response);
        void onFailure(String error);
    }

    private static TicketNotifApi getTicketNotifApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(TicketNotifApi.class);
    }

    public static void fetchTicketNotifications(String accessToken, TicketsNotificationsApiManager.ApiCallback callback) {
        TicketNotifApi apiService = getTicketNotifApi();
        Call<TicketNotifAPIResponse> call = apiService.getTicketsNotifications("Bearer " + accessToken);

        call.enqueue(new Callback<TicketNotifAPIResponse>() {
            @Override
            public void onResponse(Call<TicketNotifAPIResponse> call, Response<TicketNotifAPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Notifications fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorResponse = response.errorBody() != null ? response.errorBody().toString() : "Unknown error";
                    Log.e(TAG, "Request Failed: " + response.code() + " - " + errorResponse);
                    callback.onFailure("Failed to fetch notifications: " + errorResponse);
                }
            }

            @Override
            public void onFailure(Call<TicketNotifAPIResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching notifications: " + t.getMessage(), t);
                callback.onFailure("Error fetching notifications: " + t.getMessage());
            }
        });
    }
}
    /*

  getTicketNotif(accessToken);


    private void getTicketNotif(String accessToken) {
        TicketsNotificationsApiManager.fetchTicketNotifications(accessToken, new TicketsNotificationsApiManager.ApiCallback() {
            @Override
            public void onSuccess(TicketNotifAPIResponse response) {
                if (response != null && response.isSuccess() && response.getData() != null) {
                    Toast.makeText(getApplicationContext(), "Notifications fetched successfully", Toast.LENGTH_SHORT).show();

                    List<TicketNotificationItem> notifications = response.getData()
                            .getNotif()
                            .getNotifTicketDetails()
                            .getData();

                    // Convert response to JSON for better logging
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonResponse = gson.toJson(notifications);

                    Log.d("GetChatNotif", "Chat Notification Response: \n" + jsonResponse);
                    Log.d("GetChatNotif", "SIZE: " + notifications.size());

                    // Display notifications
                    displayTicketNotifications(MainActivity.this, notifications);

                } else {
                    Toast.makeText(getApplicationContext(), "Failed to fetch notifications: Invalid response", Toast.LENGTH_LONG).show();
                    Log.e("GetChatNotif", "Invalid or null response received");
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(), "Failed to fetch notifications: " + error, Toast.LENGTH_LONG).show();
                Log.e("GetChatNotif", "API Request Failed: " + error);
            }
        });
    }

    private void displayTicketNotifications(Context context, List<TicketNotificationItem> notifications) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "chat_notifications";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Chat Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for chat updates");
            notificationManager.createNotificationChannel(channel);
        }

        // Iterate over notifications and display each one
        for (TicketNotificationItem notification : notifications) {
            String title = notification.getLabel();
            String content = notification.getContent();

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://aws.customquoter.co.uk/tasks?task=" + notification.getReferenceId()));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.nav_chat)
                    .setContentTitle(title)
                    .setContentText("Tap to view details")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            // Generate a unique notification ID for each notification
            int notificationId = (int) System.currentTimeMillis();
            notificationManager.notify(notificationId, builder.build());
        }
    }

     */
