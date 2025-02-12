package com.example.cq_mobile.ui.chat.ChatNotif;

import com.google.gson.annotations.SerializedName;

import java.util.List;public class NotificationAPIResponse {
    private boolean success;
    private String error_code;
    private String message;
    private NotificationData data;

    public boolean isSuccess() {
        return success;
    }

    public String getErrorCode() {
        return error_code;
    }

    public String getMessage() {
        return message;
    }

    public NotificationData getData() {
        return data;
    }

    public static class NotificationData {
        private Chat chat; // Change from Notif to Chat

        public Chat getChat() {
            return chat;
        }
    }

    public static class Chat {
        private int count;
        private List<ChatNotificationItem> data;

        public int getCount() {
            return count;
        }

        public List<ChatNotificationItem> getData() {
            return data;
        }
    }
}
