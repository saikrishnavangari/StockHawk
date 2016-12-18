package com.udacity.stockhawk;

import java.text.SimpleDateFormat;

/**
 * Created by krrish on 18/12/2016.
 */

public class Utilities {

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(milliSeconds);
    }
}
