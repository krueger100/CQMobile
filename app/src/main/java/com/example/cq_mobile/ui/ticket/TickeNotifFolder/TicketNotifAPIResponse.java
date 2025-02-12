package com.example.cq_mobile.ui.ticket.TickeNotifFolder;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TicketNotifAPIResponse {
    private boolean success;
    @SerializedName("error_code")
    private String errorCode;
    private String message;
    private NotificationTicketsData data;

    public boolean isSuccess() {
        return success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public NotificationTicketsData getData() {
        return data;
    }

    public static class NotificationTicketsData {
        private Notif notif;

        public Notif getNotif() {
            return notif;
        }
    }

    public static class Notif {
        @SerializedName("_notif")
        private NotifTicketDetails notifTicketDetails;

        public NotifTicketDetails getNotifTicketDetails() {
            return notifTicketDetails;
        }
    }

    public static class NotifTicketDetails {
        @SerializedName("current_page")
        private int currentPage;
        private List<TicketNotificationItem> data;

        public int getCurrentPage() {
            return currentPage;
        }

        public List<TicketNotificationItem> getData() {
            return data;
        }
    }
}