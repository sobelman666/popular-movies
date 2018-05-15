package com.sobelman.and.popularmovies.util;

import android.content.Context;
import android.text.TextUtils;

import com.sobelman.and.popularmovies.R;

/**
 * Utilities for displaying Strings.
 */
public class StringUtils {

    /**
     * Ensures that missing data will be displayed nicely. Replaces empty string and null string
     * with a string indicating that the data is not available.
     *
     * @param value The string value being displayed.
     * @param context A context from which string resources can be read.
     * @return a string suitable for display.
     */
    public static String getNonEmptyString(String value, Context context) {
        return TextUtils.isEmpty(value) ? context.getResources().getString(R.string.no_data) : value;
    }

}
