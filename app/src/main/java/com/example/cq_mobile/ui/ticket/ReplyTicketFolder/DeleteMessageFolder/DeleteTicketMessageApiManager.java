package com.example.cq_mobile.ui.ticket.ReplyTicketFolder.DeleteMessageFolder;


import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.LoginFolder.AuthManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteTicketMessageApiManager {

    private static final String TAG = "DeleteTicketMessageApiManager";
    private static final String BASE_URL = "https://cqbms.app/api/m/tickets/";
    private static final String API_KEY = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

    public static String deleteTicketMessage(Context context, int ticketId, int messageId) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        Log.d(TAG, "Attempting to delete ticket message with Ticket ID: " + ticketId + ", Message ID: " + messageId);

        try {
            String accessToken = AuthManager.getInstance(context).getToken();

            if (accessToken == null || AuthManager.getInstance(context).isTokenExpired()) {
                Log.e(TAG, "Access token is missing or expired.");
                return "Error: Access token is missing or expired. Please log in again.";
            }
            String completeUrl = BASE_URL + ticketId + "/" + messageId;
            Log.d(TAG, "Connecting to URL: " + completeUrl); // Log URL
            URL url = new URL(completeUrl);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("x-api-key", API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            Log.d(TAG, "Headers set: Authorization, x-api-key, Content-Type, Accept");

            connection.connect();
            Log.d(TAG, "Connection established");

            // Get the response code
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "Response Code: " + responseCode); // Log response code

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                // Read response if successful
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                Log.d(TAG, "Response: " + response.toString()); // Log successful response
                return response.toString();
            } else {
                // Handle error responses
                String errorMessage = "Error: HTTP " + responseCode + " - " + connection.getResponseMessage();
                Log.d(TAG, errorMessage); // Log error response
                return errorMessage;
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception occurred: " + e.getMessage(), e); // Log exception
            return "Error: " + e.getMessage();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                    Log.d(TAG, "Reader closed"); // Log reader closure
                }
                if (connection != null) {
                    connection.disconnect();
                    Log.d(TAG, "Connection disconnected"); // Log connection closure
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error closing resources: " + ex.getMessage(), ex); // Log resource closure errors
            }
        }
    }
}
