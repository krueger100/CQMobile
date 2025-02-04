package com.example.cq_mobile.NotificationData;


import java.util.List;

public class NotificationResponse {
    private List<NotificationData> data;
    private String avatar;
    private String group;
    private String initials;
    private Boolean isAvatar;
    private String group_id;
    private List<Integer> teammembers; //
    private List<Team> teammembersdata; //  job_schedule_events
    private List<JobCategory> job_category;
    private List<JobScheduleEvents> job_schedule_events;

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


    public List<Team> getTeammembersdata() {
        return teammembersdata;
    }

    public List<JobCategory> getJob_category() {
        return job_category;
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

        public static class JobScheduleEvents {
        private int id;
        private int organization_id;
        private int job_schedule_id;
        private int unified_event_id;
        private int parent_id;
        private int user_id;
        private int status;
        private int history;
        private int main;
        private int recurring;
        private String date_done;
        private Event event;

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }


        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Event getEvent() {
            return event;
        }

        public void setEvent(Event event) {
            this.event = event;
        }
    }

        public static class Event {
        private int id;
        private int calendar_id;
        private int organization_id;
        private int user_id;
        private int colleague_group_id;
        private int requested_by;
        private String calendar_type;
        private int approved_status;
        private String date_start;
        private String date_end;
        private int has_parent;
        private int update_approved;
        private String toappove_date;
        private int parent_id;
        private String color;
        private int color_id;
        private String event_type;
        private String event_title;
        private String event_description;
        private String reference_type;
        private String reference_id;
        private String connnection_type;
        private String connection_id;
        private String job_id;
        private String other_details;
        private int has_travel_time;
        private int travel_time;
        private String deleted_at;
        private String created_at;
        private String updated_at;

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCalendar_id() {
            return calendar_id;
        }

        public void setCalendar_id(int calendar_id) {
            this.calendar_id = calendar_id;
        }

        public String getEvent_type() {
            return event_type;
        }

        public void setEvent_type(String event_type) {
            this.event_type = event_type;
        }

        public String getEvent_title() {
            return event_title;
        }

        public void setEvent_title(String event_title) {
            this.event_title = event_title;
        }

        public String getEvent_description() {
            return event_description;
        }

        public void setEvent_description(String event_description) {
            this.event_description = event_description;
        }
    }
    }
