package com.example.cq_mobile.ui.ticket.ReplyTicketFolder.DeleteMessageFolder;


import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DeleteTicketMessageApiManager {

    private static final String TAG = "DeleteTicketMessageApiManager"; // Tag for logging
    private static final String BASE_URL = "https://aws.customquoter.co.uk/api/m/tickets/";
    private static final String AUTH_TOKEN = "6331|n98FC0W7s7RlA4o5mnCmfxTDYlzWkWF2qg2B4c0m";
    private static final String API_KEY = "BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2";

    public static String deleteTicketMessage(int ticketId, int messageId) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        Log.d(TAG, "Attempting to delete ticket message with Ticket ID: " + ticketId + ", Message ID: " + messageId);

        try {
            // Build the URL for the DELETE request
            String completeUrl = BASE_URL + ticketId + "/" + messageId;
            Log.d(TAG, "Connecting to URL: " + completeUrl); // Log URL
            URL url = new URL(completeUrl);

            // Open the connection
            connection = (HttpURLConnection) url.openConnection();

            // Set request method and headers
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Authorization", "Bearer " + AUTH_TOKEN);
            connection.setRequestProperty("x-api-key", API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            Log.d(TAG, "Headers set: Authorization, x-api-key, Content-Type, Accept"); // Log headers

            // Connect to the API
            connection.connect();
            Log.d(TAG, "Connection established"); // Log connection status

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