package com.example.cq_mobile.HelperManagers.IDSfolder;

import com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.TaskMainFolder.Taskmain;

import java.util.ArrayList;
import java.util.List;

public class UserJobData {

    private static UserJobData instance;
    private List<Taskmain>jobsList;

    private UserJobData() {
        jobsList = new ArrayList<>();
    }

    public static UserJobData getInstance() {
        if (instance == null) {
            instance = new UserJobData();
        }
        return instance;
    }

    public void setJobs(List<Taskmain> tasks) {
        this.jobsList = tasks;
    }

    public List<Taskmain> getJobs() {
        return jobsList;
    }

    public Taskmain getJobById(int id) {
        for (Taskmain task : jobsList) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    public void clearJobs() {
        jobsList.clear();
    }
}


/*

in any other activity, you can access the globally stored tasks like this:
List<Taskmain> globalTasks = TaskDataManager.getInstance().getTasks();

if (globalTasks != null && !globalTasks.isEmpty()) {
    for (Taskmain task : globalTasks) {
        Log.d("OtherActivity", "Global Task ID: " + task.getId());
        Log.d("OtherActivity", "Global Task Name: " + task.getName());
    }
} else {
    Log.d("OtherActivity", "No global tasks found.");
}

You can also get a specific task by its ID:
Taskmain specificTask = TaskDataManager.getInstance().getTaskById(101);
if (specificTask != null) {
    Log.d("OtherActivity", "Fetched Task: " + specificTask.getName());
}


 */