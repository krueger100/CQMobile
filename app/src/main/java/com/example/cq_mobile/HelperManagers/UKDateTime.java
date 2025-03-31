package com.example.cq_mobile.HelperManagers;

import android.content.Context;

import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class UKDateTime {

    public static String getCurrentUKDate() {
        ZonedDateTime ukTime = ZonedDateTime.now(ZoneId.of("Asia/Manila"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return ukTime.format(formatter);
    }

    public static String getCurrentUKTime(Context context) {
        ZonedDateTime ukTime = ZonedDateTime.now(ZoneId.of("Asia/Manila"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");


        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        sharedPrefManager.saveUkEndTime(ukTime.format(formatter));

        return ukTime.format(formatter);
    }

    public static String getCurrentUKTimeStart(Context context) {
        ZonedDateTime ukTime = ZonedDateTime.now(ZoneId.of("Asia/Manila"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");


        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        sharedPrefManager.saveUkStartTime(ukTime.format(formatter));

        return ukTime.format(formatter);
    }
}

/*
String ukTimeStart = UKDateTime.getCurrentUKTimeStart(NewBuild.this);
String uktimeEnd = UKDateTime.getCurrentUKTime(context);
 */