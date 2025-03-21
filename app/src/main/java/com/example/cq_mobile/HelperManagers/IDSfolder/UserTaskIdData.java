package com.example.cq_mobile.HelperManagers.IDSfolder;

import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.SubTasks.SubTask;
import com.example.cq_mobile.ui.ticket.TicketAPICategoryFolder.TicketAPICategoryItems;

import java.util.List;

public class UserTaskIdData {

    private static UserTaskIdData instance;

    private String accessToken;
    private int userId;
    private String userName;
    private String avatarPath;
    private List<SubTask> subTasks;
    private List<TicketAPICategoryItems> tickets;

    private UserTaskIdData() {
        // Private constructor to prevent instantiation
    }

    public static synchronized UserTaskIdData getInstance() {
        if (instance == null) {
            instance = new UserTaskIdData();
        }
        return instance;
    }

    // Getter and Setter for AccessToken
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // Getter and Setter for UserId
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Getter and Setter for UserName
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // Getter and Setter for Avatar Path
    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    // Getter and Setter for SubTasks
    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    // Getter and Setter for Tickets
    public List<TicketAPICategoryItems> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketAPICategoryItems> tickets) {
        this.tickets = tickets;
    }
}


/*

UserTaskIdData globalData = GlobalData.getInstance();

String accessToken = globalData.getAccessToken();
int userId = globalData.getUserId();
String userName = globalData.getUserName();
String avatarPath = globalData.getAvatarPath();
List<SubTask> subTasks = globalData.getSubTasks();

Log.d("AnotherActivity", "User Name: " + userName);
Log.d("AnotherActivity", "Access Token: " + accessToken);

if (subTasks != null) {
    for (SubTask task : subTasks) {
        Log.d("AnotherActivity", "SubTask: " + task.getTitle());
    }
}

 */