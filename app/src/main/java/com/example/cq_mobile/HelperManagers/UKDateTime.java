package com.example.cq_mobile.HelperManagers;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class UKDateTime {
    public static String getCurrentUKDate() {
        ZonedDateTime ukTime = ZonedDateTime.now(ZoneId.of("Europe/London"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return ukTime.format(formatter);
    }

    public static String getCurrentUKTime() {
        ZonedDateTime ukTime = ZonedDateTime.now(ZoneId.of("Europe/London"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return ukTime.format(formatter);
    }
}
/*
   String ukDate = UKDateTime.getCurrentUKDate();
            String ukTime = UKDateTime.getCurrentUKTime();
 */