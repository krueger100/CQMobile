package com.example.cq_mobile.NotificationData;

import java.util.List;

public class NotificationResponse {
    private List<NotificationData> data;
    private String avatar;
    private String group;
    private String initials;
    private Boolean isAvatar;
    private String group_id;
    private List<Integer> teammembers; // The list of team IDs
    private List<Team> teammembersdata; // The list of actual team data

    // Getters and Setters
    public List<NotificationData> getData() {
        return data;
    }

    public void setData(List<NotificationData> data) {
        this.data = data;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public Boolean getIsAvatar() {
        return isAvatar;
    }

    public void setIsAvatar(Boolean isAvatar) {
        this.isAvatar = isAvatar;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public List<Integer> getTeammembers() {
        return teammembers;
    }

    public void setTeammembers(List<Integer> teammembers) {
        this.teammembers = teammembers;
    }

    public List<Team> getTeammembersdata() {
        return teammembersdata;
    }

    public void setTeammembersdata(List<Team> teammembersdata) {
        this.teammembersdata = teammembersdata;
    }

    // Inner classes for NotificationData and Team
    public static class NotificationData {
        private List<Team> teammembersdata;
        private JobData jobData;
        private JobSchedule jobSchedule;

        public List<Team> getTeammembersdata() {
            return teammembersdata;
        }

        public void setTeammembersdata(List<Team> teammembersdata) {
            this.teammembersdata = teammembersdata;
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
    }

    public static class Team {
        private String id;
        private String name;
        private String initials;
        private String type;
        private String avatar;
        private String color;

        // Constructor
        public Team(String id, String name) {
            this.id = id;
            this.name = name;
            this.initials = initials;
            this.color = color;
        }

        // Getters and Setters for Team
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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
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
        }

        public static class JobCategory {
            private String id;
            private String name;
            private String description;

            // Getters and setters for JobCategory...
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

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }

        public static class JobSchedule {
            private String date;
            private String time;

            // Getters and setters for JobSchedule...
            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }
        }
    }
