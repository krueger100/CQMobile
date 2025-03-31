package com.example.cq_mobile.HelperManagers.GlobalData;

import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.SubTasks.SubTask;
import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.TaskMainFolder.Taskmain;
import com.example.cq_mobile.ui.ticket.TicketAPICategoryFolder.TicketAPICategoryItems;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalDataManager {
    private static GlobalDataManager instance;

    private List<Taskmain> jobs;
    private List<SubTask> subTasks;
    private List<TicketAPICategoryItems> categoryTickets;
    private List<TicketAPIItem> ticketsWithToken;

    private String accessToken;
    private int userId;
    private String userName;
    private String avatarPath;

    private Map<Integer, String> jobDetails; // Stores job ID and details
    private Map<Integer, String> subTaskDetails; // Stores sub-task ID and details
    private Map<Integer, String> ticketDetails; // Stores ticket ID and details

    private GlobalDataManager() {
        jobDetails = new HashMap<>();
        subTaskDetails = new HashMap<>();
        ticketDetails = new HashMap<>();
    }

    public static synchronized GlobalDataManager getInstance() {
        if (instance == null) {
            instance = new GlobalDataManager();
        }
        return instance;
    }

    // Job Data
    public List<Taskmain> getJobs() { return jobs; }
    public void setJobs(List<Taskmain> jobs) { this.jobs = jobs; }

    // SubTask Data
    public List<SubTask> getSubTasks() { return subTasks; }
    public void setSubTasks(List<SubTask> subTasks) { this.subTasks = subTasks; }

    // Ticket Data
    public List<TicketAPICategoryItems> getCategoryTickets() { return categoryTickets; }
    public void setCategoryTickets(List<TicketAPICategoryItems> categoryTickets) { this.categoryTickets = categoryTickets; }

    public List<TicketAPIItem> getTicketsWithToken() { return ticketsWithToken; }
    public void setTicketsWithToken(List<TicketAPIItem> ticketsWithToken) { this.ticketsWithToken = ticketsWithToken; }

    // User Data
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    // Job Details
    public Map<Integer, String> getJobDetails() { return jobDetails; }
    public void addJobDetail(int jobId, String details) { jobDetails.put(jobId, details); }

    // SubTask Details
    public Map<Integer, String> getSubTaskDetails() { return subTaskDetails; }
    public void addSubTaskDetail(int subTaskId, String details) { subTaskDetails.put(subTaskId, details); }

    // Ticket Details
    public Map<Integer, String> getTicketDetails() { return ticketDetails; }
    public void addTicketDetail(int ticketId, String details) { ticketDetails.put(ticketId, details); }
}


/*

List<Taskmain> jobList = DataManager.getInstance().getJobs();
int userId = DataManager.getInstance().getUserId();
String userName = DataManager.getInstance().getUserName();
String accessToken = DataManager.getInstance().getAccessToken();
List<SubTask> subTaskList = DataManager.getInstance().getSubTasks();


public class ExampleFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_example, container, false);

        // Access stored data
        List<Taskmain> jobs = DataManager.getInstance().getJobs();
        String userName = DataManager.getInstance().getUserName();

        Log.d("ExampleFragment", "User Name: " + userName);
        return view;
    }
}



 */