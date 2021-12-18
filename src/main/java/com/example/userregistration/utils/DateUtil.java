package com.example.userregistration.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static String FORMAT_DATE = "yyyy-MM-dd HH:mm:ss";

    public static String formatDateTime(Date date, String formatDate) {
        return formatDate(date, formatDate);
    }

    public static String formatDateTime(Date date) {
        return formatDate(date, FORMAT_DATE);
    }

    public static String formatDate(Date date, String formatDate) {
        if (date == null)
            return "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatDate);
        return simpleDateFormat.format(date);
    }

    public static Date toDate(String value) {
        if (value == null || value.equals(""))
            return null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DATE);
            return simpleDateFormat.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }
}
