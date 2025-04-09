package com.example.cq_mobile.HelperManagers;

import android.content.Context;
import android.util.Log;


import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.time.LocalDate;

public class DateAndTimeManager {

    // UK Date and Time
    public static String getCurrentUKDate() {
        ZonedDateTime ukTime = ZonedDateTime.now(ZoneId.of("Europe/London"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return ukTime.format(formatter);
    }
    public static String getCurrentUKTime(Context context) {
        ZonedDateTime ukTime = ZonedDateTime.now(ZoneId.of("Europe/London"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return ukTime.format(formatter);
    }
    public static String getCurrentUKTimeStart(Context context) {
        ZonedDateTime ukTime = ZonedDateTime.now(ZoneId.of("Europe/London"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return ukTime.format(formatter);
    }

    // PH Date and Time
    public static String getCurrentPHDate() {
        ZonedDateTime phDate = ZonedDateTime.now(ZoneId.of("Asia/Manila"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return phDate.format(formatter);
    }
    public static String getCurrentPHTime(Context context) {
        ZonedDateTime phTime = ZonedDateTime.now(ZoneId.of("Asia/Manila"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return phTime.format(formatter);
    }
    public static String getCurrentPHTimeStart(Context context) {
        ZonedDateTime phTime = ZonedDateTime.now(ZoneId.of("Asia/Manila"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return phTime.format(formatter);
    }

    // **
// -- >>> Date and Time Based on User's Location
// **
    public static String getCurrentDateAndTime(Context context) {
        ZonedDateTime localTime = ZonedDateTime.now(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Save stop time
        String formattedDateTime = localTime.format(formatter);
        Log.d("DateAndTimeManager", "Current Date and Time (User's Location): " + formattedDateTime);
        Log.d("DateAndTimeManager", "Stop time saved: " + formattedDateTime);

        return formattedDateTime;
    }

    // **
// -- >> Date Only Based on User's Location
// **
    public static String getCurrentDate(Context context) {
        LocalDate localDate = LocalDate.now(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = localDate.format(formatter);
        Log.d("DateAndTimeManager", "Current Date (User's Location): " + formattedDate);

        return formattedDate;
    }

    // **
// -- >> Time Only Based on User's Location
// **
    public static String getCurrentTime(Context context) {
        LocalTime localTime = LocalTime.now(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        String formattedTime = localTime.format(formatter);
        Log.d("DateAndTimeManager", "Current Time (User's Location): " + formattedTime);

        return formattedTime;
    }

}


