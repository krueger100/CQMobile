package com.example.cq_mobile.NotificationData.APIResponceFolder;

import android.service.autofill.UserData;


import java.util.List;

public class FilteredNotificationResponse {

    private List<NotificationData> data;
    private JobCategory jobCategory;


    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<NotificationData> getData() {
        return data;
    }

    public void setData(List<NotificationData> data) {
        this.data = data;
    }


    public static class NotificationData {

        private int id;
        private int organization_id;
        private int user_id;
        private int contacts_id;
        private Integer lead_id;
        private Integer lead_to_job_id;
        private int is_usage_recorded;
        private int job_category_id;
        private Integer job_document_id;
        private List<String> job_documents;
        private List<Integer> risk_documents;
        private String title;
        private String description;
        private int job_status;
        private int is_vatable;
        private List<String> assigned;
        private List<String> price_group_markup;
        private String start_at;
        private String end_at;
        private String labour_budget;
        private String expenses_budget;
        private int invoice_schedules_id;
        private String deleted_at;
        private String created_at;
        private String updated_at;
        private Integer contact_address_id;
        private List<String> group_assigned;
        private JobSchedule job_schedule;
        private List<JobScheduleEvent> job_schedule_events;


        private String notificationId;
        private String message;
        private String timestamp;
        private UserData userData;


        private String avatar;
        private String group;
        private String initials;
        private Boolean isAvatar;
        private String group_id;
        private List<Integer> teammembers; // The list of team IDs
        private List<TeamFilterNotif> teammembersdata;

        private JobCategory job_category;

        public JobCategory getJob_category() { // Corrected getter
            return job_category;
        }

        public void setJob_category(JobCategory job_category) { // Corrected setter
            this.job_category = job_category;
        }





        public NotificationData() {
            this.avatar = avatar;
            this.group = group;
            this.initials = initials;
            this.isAvatar = isAvatar;
            this.group_id = group_id;
            this.teammembers = teammembers;
            this.teammembersdata = teammembersdata;



            this.id = id;
            this.organization_id = organization_id;
            this.user_id = user_id;
            this.contacts_id = contacts_id;
            this.lead_id = lead_id;
            this.lead_to_job_id = lead_to_job_id;
            this.is_usage_recorded = is_usage_recorded;
            this.job_category_id = job_category_id;
            this.job_document_id = job_document_id;
            this.job_documents = job_documents;
            this.risk_documents = risk_documents;
            this.title = title;
            this.description = description;
            this.job_status = job_status;
            this.is_vatable = is_vatable;
            this.assigned = assigned;
            this.price_group_markup = price_group_markup;
            this.start_at = start_at;
            this.end_at = end_at;
            this.labour_budget = labour_budget;
            this.expenses_budget = expenses_budget;
            this.invoice_schedules_id = invoice_schedules_id;
            this.deleted_at = deleted_at;
            this.created_at = created_at;
            this.updated_at = updated_at;
            this.contact_address_id = contact_address_id;
            this.group_assigned = group_assigned;

            this.job_schedule = job_schedule;
            this.job_schedule_events = job_schedule_events;
            this.notificationId = notificationId;
            this.message = message;
            this.timestamp = timestamp;
            this.userData = userData;


        }


        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(Boolean avatar) {
            isAvatar = avatar;
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

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

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


        public void setOnClickListener(Object o) {
        }

    }


    // Define the JobCategory class
    public static class JobCategory {
        int id;
        int organization_id;
        int user_id;
        String name;
        String color;
        String description;
        String icon;
        String created_at;
        String updated_at;

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getOrganization_id() {
            return organization_id;
        }

        public void setOrganization_id(int organization_id) {
            this.organization_id = organization_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }


    }


    // Define the JobSchedule class
    public static class JobSchedule {
        private int id;
        private int organization_id;
        private int job_id;
        private int type;
        private int recurring;
        private int invoiceable;
        private String order;

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getOrganization_id() {
            return organization_id;
        }

        public void setOrganization_id(int organization_id) {
            this.organization_id = organization_id;
        }

        public int getJob_id() {
            return job_id;
        }

        public void setJob_id(int job_id) {
            this.job_id = job_id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getRecurring() {
            return recurring;
        }

        public void setRecurring(int recurring) {
            this.recurring = recurring;
        }

        public int getInvoiceable() {
            return invoiceable;
        }

        public void setInvoiceable(int invoiceable) {
            this.invoiceable = invoiceable;
        }

        public String getOrder() {
            return order;
        }

        public void setOrder(String order) {
            this.order = order;
        }
    }

    // Define the JobScheduleEvent class
    public static class JobScheduleEvent {
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

        public int getOrganization_id() {
            return organization_id;
        }

        public void setOrganization_id(int organization_id) {
            this.organization_id = organization_id;
        }

        public int getJob_schedule_id() {
            return job_schedule_id;
        }

        public void setJob_schedule_id(int job_schedule_id) {
            this.job_schedule_id = job_schedule_id;
        }

        public int getUnified_event_id() {
            return unified_event_id;
        }

        public void setUnified_event_id(int unified_event_id) {
            this.unified_event_id = unified_event_id;
        }

        public int getParent_id() {
            return parent_id;
        }

        public void setParent_id(int parent_id) {
            this.parent_id = parent_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getHistory() {
            return history;
        }

        public void setHistory(int history) {
            this.history = history;
        }

        public int getMain() {
            return main;
        }

        public void setMain(int main) {
            this.main = main;
        }

        public int getRecurring() {
            return recurring;
        }

        public void setRecurring(int recurring) {
            this.recurring = recurring;
        }

        public String getDate_done() {
            return date_done;
        }

        public void setDate_done(String date_done) {
            this.date_done = date_done;
        }

        public Event getEvent() {
            return event;
        }

        public void setEvent(Event event) {
            this.event = event;
        }
    }

    // Define the Event class for JobScheduleEvent
    public static class Event {
        private int id;
        private int calendar_id;
        private int organization_id;
        private int user_id;
        private String event_title;
        private String event_description;
        private String date_start;
        private String date_end;
        private String color;
        private String event_type;

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

        public int getOrganization_id() {
            return organization_id;
        }

        public void setOrganization_id(int organization_id) {
            this.organization_id = organization_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
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

        public String getDate_start() {
            return date_start;
        }

        public void setDate_start(String date_start) {
            this.date_start = date_start;
        }

        public String getDate_end() {
            return date_end;
        }

        public void setDate_end(String date_end) {
            this.date_end = date_end;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getEvent_type() {
            return event_type;
        }

        public void setEvent_type(String event_type) {
            this.event_type = event_type;
        }
    }


    public static class TeamFilterNotif {
        private String id;
        private String name;
        private String initials;
        private String type;
        private String avatar;
        private String color;

        // Constructor
        public TeamFilterNotif(String id, String name, String initials, String color) {
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



}
