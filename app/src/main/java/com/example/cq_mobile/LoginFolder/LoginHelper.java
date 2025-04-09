package com.example.cq_mobile.LoginFolder;

import android.content.Context;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class LoginHelper {

    public interface LoginCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static void login(Context context, String email, String password, LoginCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://aws.customquoter.co.uk/api/m/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject payload = new JSONObject();
                payload.put("email", email);
                payload.put("password", password);

                OutputStream os = conn.getOutputStream();
                os.write(payload.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
                    String response = scanner.hasNext() ? scanner.next() : "";

                    JSONObject resJson = new JSONObject(response);

                    String token = resJson.getString("access_token");
                    long expiresIn = resJson.getLong("expires_in") * 1000; // Convert seconds to ms
                    long expirationTime = System.currentTimeMillis() + expiresIn;

                    JSONObject user = resJson.getJSONObject("user");
                    int userId = user.getInt("id");
                    String firstName = user.getString("first_name");
                    String lastName = user.getString("last_name");
                    String avatar = user.optString("avatar", "");
                    String userEmail = user.getString("email");

                    AuthManager.getInstance(context).saveToken(token, userId, firstName, lastName, userEmail, avatar, expirationTime);

                    callback.onSuccess();
                } else {
                    callback.onFailure("Login failed: HTTP " + responseCode);
                }

            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Login error: " + e.getMessage());
            }
        }).start();
    }
}
