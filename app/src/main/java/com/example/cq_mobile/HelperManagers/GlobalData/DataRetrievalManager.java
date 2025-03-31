package com.example.cq_mobile.HelperManagers.GlobalData;

import java.util.Map;

public class DataRetrievalManager {
    private static DataRetrievalManager instance;

    private DataRetrievalManager() {
        // Private constructor to enforce singleton pattern
    }

    public static synchronized DataRetrievalManager getInstance() {
        if (instance == null) {
            instance = new DataRetrievalManager();
        }
        return instance;
    }

    // Retrieve Job Details
    public String getJobDetail(int jobId) {
        Map<Integer, String> jobDetails = GlobalDataManager.getInstance().getJobDetails();
        return jobDetails != null ? jobDetails.getOrDefault(jobId, "Not available") : "Not available";
    }

    // Retrieve SubTask Details
    public String getSubTaskDetail(int subTaskId) {
        Map<Integer, String> subTaskDetails = GlobalDataManager.getInstance().getSubTaskDetails();
        return subTaskDetails != null ? subTaskDetails.getOrDefault(subTaskId, "Not available") : "Not available";
    }

    // Retrieve Ticket Details
    public String getTicketDetail(int ticketId) {
        Map<Integer, String> ticketDetails = GlobalDataManager.getInstance().getTicketDetails();
        return ticketDetails != null ? ticketDetails.getOrDefault(ticketId, "Not available") : "Not available";
    }

    // Retrieve User Details
    public int getUserId() {
        return GlobalDataManager.getInstance().getUserId();
    }

    public String getUserName() {
        String userName = GlobalDataManager.getInstance().getUserName();
        return userName != null ? userName : "Unknown User";
    }

    public String getAccessToken() {
        String token = GlobalDataManager.getInstance().getAccessToken();
        return token != null ? token : "No Token Available";
    }

    public String getAvatarPath() {
        String avatar = GlobalDataManager.getInstance().getAvatarPath();
        return avatar != null ? avatar : "No Avatar Available";
    }
}
/*

DataRetrievalManager dataManager = DataRetrievalManager.getInstance();

String jobDetail = dataManager.getJobDetail(101);
String subTaskDetail = dataManager.getSubTaskDetail(202);
String ticketDetail = dataManager.getTicketDetail(303);



int userId = dataManager.getUserId();
String userName = dataManager.getUserName();
String accessToken = dataManager.getAccessToken();
String avatarPath = dataManager.getAvatarPath();

 */