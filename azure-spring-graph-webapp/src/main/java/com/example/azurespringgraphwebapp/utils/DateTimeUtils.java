package com.example.azurespringgraphwebapp.utils;

import com.microsoft.graph.models.DateTimeTimeZone;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public final class DateTimeUtils {

    public static String getDateFromDateTimeZone(DateTimeTimeZone dateTimeZone) {
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeZone.dateTime);
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String getTimeFromDateTimeZone(DateTimeTimeZone dateTimeZone) {
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeZone.dateTime);
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
    }
}
