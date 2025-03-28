package com.example.cq_mobile.ui.home.HomeFolder.JobsFolder.TaskMainFolder;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.SerializedName;

public class Taskmain {
    private int id;

    @SerializedName("job_id")
    private int jobId;

    @SerializedName("job_title")
    private String jobTitle;

    private String name;
    private String description;
    private String status;
    private String category;
    private String category_color;
    private String start_date;
    private String end_date;
    private ClientDetails client_details;
    private Address address;
    private String timezone;
    private Coordinates coordinates;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCategory_color() { return category_color; }
    public void setCategory_color(String category_color) { this.category_color = category_color; }

    public String getStart_date() { return start_date; }
    public void setStart_date(String start_date) { this.start_date = start_date; }

    public String getEnd_date() { return end_date; }
    public void setEnd_date(String end_date) { this.end_date = end_date; }

    public ClientDetails getClient_details() { return client_details; }
    public void setClient_details(ClientDetails client_details) { this.client_details = client_details; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    @Override
    public String toString() {
        return "Taskmain{id=" + id + ", name='" + name + "', status='" + status + "'}";
    }

    public static class ClientDetails {
        private String company;
        private String email;
        private String title;
        private String first_name;
        private String last_name;
        private String suffix;
        private String phone;
        private String mobile;
        private String timezone;

        // Getters and Setters
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getFirst_name() { return first_name; }
        public void setFirst_name(String first_name) { this.first_name = first_name; }

        public String getLast_name() { return last_name; }
        public void setLast_name(String last_name) { this.last_name = last_name; }

        public String getSuffix() { return suffix; }
        public void setSuffix(String suffix) { this.suffix = suffix; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }

        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }
    }

    public static class Address {
        private String address;
        private String address1;
        private String city;
        private String county;
        private String postal_code;
        private String country;

        // Getters and Setters
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getAddress1() { return address1; }
        public void setAddress1(String address1) { this.address1 = address1; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getCounty() { return county; }
        public void setCounty(String county) { this.county = county; }

        public String getPostal_code() { return postal_code; }
        public void setPostal_code(String postal_code) { this.postal_code = postal_code; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
    }

    public static class Coordinates {
        @SerializedName("latitude")
        private String latitude;

        @SerializedName("longitude")
        private String longitude;

        public Coordinates() {
            this.latitude = "0.0";
            this.longitude = "0.0";
        }

        public Coordinates(double lat, double lon) {
            this.latitude = String.valueOf(lat);
            this.longitude = String.valueOf(lon);
        }

        public double getLatitude() {
            if (latitude == null || latitude.isEmpty()) {
                return 0.0;
            }
            try {
                return Double.parseDouble(latitude);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }

        public double getLongitude() {
            if (longitude == null || longitude.isEmpty()) {
                return 0.0;
            }
            try {
                return Double.parseDouble(longitude);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
    }
}
