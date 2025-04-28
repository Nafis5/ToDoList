package com.notes.keepnotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AppOpenDateManager {
    private static final String PREF_NAME = "MyAppPreferences";
    private static final String FIRST_OPEN_DATE_KEY = "FirstOpenDate";

    public static void setFirstOpenDate(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (!sharedPreferences.contains(FIRST_OPEN_DATE_KEY)) {
            // Get the current date and store it as the first open date

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDate = Calendar.getInstance().getTime();
            String dateString = dateFormat.format(currentDate);
            editor.putString(FIRST_OPEN_DATE_KEY, dateString);
            editor.apply();
        }


    }


    public static boolean isFirstOpenToday(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if (sharedPreferences.contains(FIRST_OPEN_DATE_KEY)) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDate = Calendar.getInstance().getTime();
            String firstOpenDateString = sharedPreferences.getString(FIRST_OPEN_DATE_KEY, "");

            if (dateFormat.format(currentDate).equals(firstOpenDateString)) {
                return true; // Today is the same as the first open date
            }
        }

        return false; // Today is not the same as the first open date or the date hasn't been set
    }

}
