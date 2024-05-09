package com.rr.recyclerally.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter extends SimpleDateFormat {
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(DATE_FORMAT, Locale.US);

    public static Date toDate(String str) {
        try {
            return FORMATTER.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String fromDate(Date date) {
        if (date != null) {
            return FORMATTER.format(date);
        } else {
            return null;
        }
    }
}