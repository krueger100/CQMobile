package com.example.cq_mobile.ui.ticket.TickeNotifFolder;

import com.google.gson.annotations.SerializedName;


public class TicketNotificationItem {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("organization_id")
    private int organizationId;

    @SerializedName("notification_label")
    private String label;

    @SerializedName("notification_description")
    private String description;

    @SerializedName("notification_content")
    private String content;

    @SerializedName("types")
    private String types;

    @SerializedName("reference_id")
    private int referenceId;

    @SerializedName("notif_read")
    private int notifRead;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("isAdmin")
    private int isAdmin;

    // Constructor
    public TicketNotificationItem(int id, int userId, int organizationId, String label, String description,
                                  String content, String types, int referenceId, int notifRead,
                                  String createdAt, String updatedAt, int isAdmin) {
        this.id = id;
        this.userId = userId;
        this.organizationId = organizationId;
        this.label = label;
        this.description = description;
        this.content = content;
        this.types = types;
        this.referenceId = referenceId;
        this.notifRead = notifRead;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isAdmin = isAdmin;
    }

    // Getters (Optional but recommended)
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getOrganizationId() { return organizationId; }
    public String getLabel() { return label; }
    public String getDescription() { return description; }
    public String getContent() { return content; }
    public String getTypes() { return types; }
    public int getReferenceId() { return referenceId; }
    public int getNotifRead() { return notifRead; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public int getIsAdmin() { return isAdmin; }
}
