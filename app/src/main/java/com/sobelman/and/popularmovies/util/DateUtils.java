package com.sobelman.and.popularmovies.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utilities for parsing and displaying Dates.
 */
public class DateUtils {

    private static final String ISO8601_FORMAT = "yyyy-MM-dd";
    /**
     * Parses date strings in ISO 8601 format, i.e. yyyy-MM-dd.
     *
     * @param dateString the date string to be parsed.
     * @return a java.util.Date object for the parsed date.
     */
    public static Date parseISO8601DateString(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat(ISO8601_FORMAT, Locale.US);
        Date parsedDate = null;
        try {
            parsedDate = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    /**
     * Displays a java.util.Date in the long format for the locale.
     *
     * @param date the date to be displayed.
     * @return a String suitable for display.
     */
    public static String getDateDisplayString(Date date) {
        DateFormat format = DateFormat.getDateInstance(DateFormat.LONG);
        return format.format(date);
    }

}
