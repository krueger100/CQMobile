package com.example.cq_mobile.ui.home.HomeFolder.homeFrgmentFolder;

import android.os.Parcel;
import android.os.Parcelable;
public class JobData implements Parcelable {

    private int id;
    private int jobId;
    private String jobTitle;
    private String status;
    private String startDate;
    private String endDate;

    public JobData(int id, int jobId, String jobTitle, String status, String startDate, String endDate) {
        this.id = id;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    protected JobData(Parcel in) {
        id = in.readInt();
        jobId = in.readInt();
        jobTitle = in.readString();
        status = in.readString();
        startDate = in.readString();
        endDate = in.readString();
    }

    public static final Creator<JobData> CREATOR = new Creator<JobData>() {
        @Override
        public JobData createFromParcel(Parcel in) {
            return new JobData(in);
        }

        @Override
        public JobData[] newArray(int size) {
            return new JobData[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(jobId);
        dest.writeString(jobTitle);
        dest.writeString(status);
        dest.writeString(startDate);
        dest.writeString(endDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}
