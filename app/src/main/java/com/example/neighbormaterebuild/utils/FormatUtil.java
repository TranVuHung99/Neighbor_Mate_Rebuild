package com.example.neighbormaterebuild.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtil {

    public static Date convertStringtoDate(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPAN);
        Date d = format.parse(date);
        return d;
    }

    public static String convertDatetoString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.JAPAN);
        String time = sdf.format(date);
        return time;
    }

    public static String convertTime(String date) {
        try {
            Date d = new Date(Long.valueOf(date));
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.JAPAN);
            String time = sdf.format(d);
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
