package com.example.cq_mobile.NotificationData;


import java.util.List;

public class NotificationResponse {
    private List<NotificationData> data;

    // Getter and Setter for 'data'
    public List<NotificationData> getData() {
        return data;
    }

    public void setData(List<NotificationData> data) {
        this.data = data;
    }

    // Inner class to represent Notification Data
    public static class NotificationData {
        private List<UserData> teammembersdata;
        private List<Team> team;
        private JobData jobData;  // New field for job data
        private JobSchedule jobSchedule;  // New field for job schedule

        // Getters and setters for NotificationData...

        public List<UserData> getTeammembersdata() {
            return teammembersdata;
        }

        public void setTeammembersdata(List<UserData> teammembersdata) {
            this.teammembersdata = teammembersdata;
        }

        public List<Team> getTeam() {
            return team;
        }

        public void setTeam(List<Team> team) {
            this.team = team;
        }

        public JobData getJobData() {
            return jobData;
        }

        public void setJobData(JobData jobData) {
            this.jobData = jobData;
        }

        public JobSchedule getJobSchedule() {
            return jobSchedule;
        }

        public void setJobSchedule(JobSchedule jobSchedule) {
            this.jobSchedule = jobSchedule;
        }

        public static class UserData {
            private String id;
            private String name;
            private String initials;
            private String avatar;
            private String type;
            private String color;

            // Getters and setters for UserData...
            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getInitials() {
                return initials;
            }

            public void setInitials(String initials) {
                this.initials = initials;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }
        }

        public static class Team {
            private int id;
            private String name;
            private String initials;
            private String avatar;
            private String color;

            // Getters and setters for Team...
            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getInitials() {
                return initials;
            }

            public void setInitials(String initials) {
                this.initials = initials;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }
        }

        public static class JobData {
            private String title;
            private String description;
            private String jobStatus;
            private JobCategory jobCategory;
            private String startAt;

            // Getters and setters for JobData...
            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getJobStatus() {
                return jobStatus;
            }

            public void setJobStatus(String jobStatus) {
                this.jobStatus = jobStatus;
            }

            public JobCategory getJobCategory() {
                return jobCategory;
            }

            public void setJobCategory(JobCategory jobCategory) {
                this.jobCategory = jobCategory;
            }

            public String getStartAt() {
                return startAt;
            }

            public void setStartAt(String startAt) {
                this.startAt = startAt;
            }

            public static class JobCategory {
                private String name;

                // Getters and setters for JobCategory...
                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }
        }

        public static class JobSchedule {
            private int id;
            private String type;
            private List<Task> availableTasks;

            // Getters and setters for JobSchedule...
            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<Task> getAvailableTasks() {
                return availableTasks;
            }

            public void setAvailableTasks(List<Task> availableTasks) {
                this.availableTasks = availableTasks;
            }

            public static class Task {
                private String taskHeader;
                private String priorityLevel;

                // Getters and setters for Task...
                public String getTaskHeader() {
                    return taskHeader;
                }

                public void setTaskHeader(String taskHeader) {
                    this.taskHeader = taskHeader;
                }

                public String getPriorityLevel() {
                    return priorityLevel;
                }

                public void setPriorityLevel(String priorityLevel) {
                    this.priorityLevel = priorityLevel;
                }
            }
        }
    }
}
