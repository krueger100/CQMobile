package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder;

public class Taskmain {
    private int id;
    private String name;
    private String description;
    private String status;
    private String category;
    private String category_color;
    private String start_date;
    private String end_date;
    private String route_order;
    private ClientDetails client_details;
    private Address address;
    private Coordinates coordinates;

    // Getters and setters for all fields
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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

    public String getRoute_order() { return route_order; }
    public void setRoute_order(String route_order) { this.route_order = route_order; }

    public ClientDetails getClient_details() { return client_details; }
    public void setClient_details(ClientDetails client_details) { this.client_details = client_details; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }

    public static class ClientDetails {
        private String company;
        private String email;
        private String title;
        private String first_name;
        private String last_name;
        private String phone;
        private String mobile;

        // Getters and setters for client details


        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }

    public static class Address {
        private String address;
        private String address1;
        private String city;
        private String postal_code;
        private String country;

        // Getters and setters for address

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPostal_code() {
            return postal_code;
        }

        public void setPostal_code(String postal_code) {
            this.postal_code = postal_code;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }

    public static class Coordinates {
        private String latitude;
        private String longitude;

        // Getters and setters for coordinates

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
    }
}
