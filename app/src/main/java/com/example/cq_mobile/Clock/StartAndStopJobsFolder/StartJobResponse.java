package com.example.cq_mobile.Clock.StartAndStopJobsFolder;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class StartJobResponse {
    private boolean success;
    private String message;
    private Data data;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    // Inner class: Data
    public static class Data {
        private String status;
        private String event;
        private String message;
        private String message2;
        private Work work;

        // Getters
        public String getStatus() { return status; }
        public String getEvent() { return event; }
        public String getMessage() { return message; }
        public String getMessage2() { return message2; }
        public Work getWork() { return work; }

        // Inner class: Work
        public static class Work {
            private int id;
            private int organization_id;
            private int user_id;
            private int job_id;
            private String start_time;
            private String end_time;
            private Remarks remarks;
            private Job job;

            // Getters
            public int getId() { return id; }
            public int getOrganization_id() { return organization_id; }
            public int getUser_id() { return user_id; }
            public int getJob_id() { return job_id; }
            public String getStart_time() { return start_time; }
            public String getEnd_time() { return end_time; }
            public Remarks getRemarks() { return remarks; }
            public Job getJob() { return job; }

            // Inner class: Remarks
            public static class Remarks {
                @SerializedName("lat")
                private double lat;

                @SerializedName("long") // Mapping "long" to a valid Java field
                private double longitude;

                public double getLat() { return lat; }
                public double getLongitude() { return longitude; }
            }

            // Inner class: Job
            public static class Job {
                private int id;
                private String title;
                private String description;
                private int job_status;

                public int getId() { return id; }
                public String getTitle() { return title; }
                public String getDescription() { return description; }
                public int getJob_status() { return job_status; }
            }
        }
    }
}
