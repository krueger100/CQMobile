package com.example.cq_mobile.Clock.ClockFolder.ClockOutFolder;

public class ClockOutApiResponse {
    boolean success;
    String error_code;
    String message;
    Data data;

    public class Data {
        String status;
        String message;
    }
}
